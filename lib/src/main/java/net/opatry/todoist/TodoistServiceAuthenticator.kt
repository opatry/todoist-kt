/*
 * Copyright (c) 2024 Olivier Patry
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE
 * OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package net.opatry.todoist

import com.google.gson.annotations.SerializedName
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.CurlUserAgent
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.Url
import io.ktor.http.contentType
import io.ktor.http.fullPath
import io.ktor.http.isSuccess
import io.ktor.serialization.gson.gson
import io.ktor.server.application.call
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.opatry.todoist.TodoistServiceAuthenticator.OAuthToken.TokenType.Bearer
import net.opatry.todoist.TodoistServiceAuthenticator.OAuthToken.TokenType.Mac
import net.opatry.todoist.TodoistServiceAuthenticator.Permission.DataDelete
import net.opatry.todoist.TodoistServiceAuthenticator.Permission.DataRead
import net.opatry.todoist.TodoistServiceAuthenticator.Permission.DataReadWrite
import net.opatry.todoist.TodoistServiceAuthenticator.Permission.ProjectDelete
import net.opatry.todoist.TodoistServiceAuthenticator.Permission.TaskAdd
import java.util.*
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

interface TodoistServiceAuthenticator {

    /**
     * @property TaskAdd `"task:add"` Grants permission to add new tasks (the application cannot read or modify any existing data).
     * @property DataRead `"data:read"` Grants read-only access to application data, including tasks, projects, labels, and filters.
     * @property DataReadWrite `"data:read_write"` Grants read and write access to application data, including tasks, projects, labels, and filters. This scope includes [TaskAdd] and [DataRead] scopes.
     * @property DataDelete `"data:delete"` Grants permission to delete application data, including tasks, labels, and filters.
     * @property ProjectDelete `"project:delete"` Grants permission to delete projects.
     */
    enum class Permission(val scope: String) {
        TaskAdd("task:add"),
        DataRead("data:read"),
        DataReadWrite("data:read_write"),
        DataDelete("data:delete"),
        ProjectDelete("project:delete"),
    }

    /**
     * @property accessToken The access token issued by the authorization server.
     * @property tokenType The type of the token issued.
     */
    data class OAuthToken(

        @SerializedName("access_token")
        val accessToken: String,

        @SerializedName("token_type")
        val tokenType: TokenType,
    ) {
        /**
         * Value is case insensitive.
         *
         * @property Bearer `"bearer"` token type defined in [RFC6750](https://datatracker.ietf.org/doc/html/rfc6750) is utilized by simply including the access token string in the request.
         * @property Mac `"mac"` token type defined in [OAuth-HTTP-MAC](https://datatracker.ietf.org/doc/html/rfc6749#ref-OAuth-HTTP-MAC) is utilized by issuing a Message Authentication Code (MAC) key together with the access token that is used to sign certain components of the HTTP requests.
         */
        enum class TokenType {

            @SerializedName("bearer")
            Bearer,

            @SerializedName("mac")
            Mac,
        }
    }

    /**
     * @param permissions Permission scope. The currently available scopes are [Permission.TasksWrite], [Permission.TasksRead]
     * @param requestUserAuthorization The URL to which to request user authorization before direction
     *
     * @return auth code
     *
     * @see Permission
     */
    suspend fun authorize(permissions: List<Permission>, requestUserAuthorization: suspend (url: String) -> Unit): String

    /**
     * @param code The code obtained through [authorize].
     *
     * @return OAuth access token
     */
    suspend fun getToken(code: String): OAuthToken
}

class HttpTodoistServiceAuthenticator(private val config: ApplicationConfig) : TodoistServiceAuthenticator {

    /**
     * @property redirectUrl Redirect url
     * @property clientId OAuth2 Client ID
     * @property clientSecret OAuth2 Client Secret
     */
    data class ApplicationConfig(
        val redirectUrl: String,
        val clientId: String,
        val clientSecret: String,
    )

    private companion object {
        const val TODOIST_ROOT_URL = "https://todoist.com"
    }

    private val httpClient: HttpClient by lazy {
        HttpClient(CIO) {
            CurlUserAgent()
            install(ContentNegotiation) {
                gson()
            }
            defaultRequest {
                url(TODOIST_ROOT_URL)
            }
        }
    }

    override suspend fun authorize(permissions: List<TodoistServiceAuthenticator.Permission>, requestUserAuthorization: suspend (url: String) -> Unit): String {
        val uuid = UUID.randomUUID()
        val params = mapOf(
            "client_id" to config.clientId,
            "scope" to permissions.joinToString(",", transform = TodoistServiceAuthenticator.Permission::scope),
            "state" to uuid.toString(),
            "redirect_uri" to config.redirectUrl,
            "response_type" to "code",
        ).entries.joinToString(prefix = "?", separator = "&") {
            "${it.key}=${it.value}"
        }

        var server: ApplicationEngine? = null
        return try {
            suspendCoroutine { continuation ->
                // FIXME calling several times this in parallel with fail
                val url = Url(config.redirectUrl)
                server = embeddedServer(Netty, port = url.port, host = url.host) {
                    routing {
                        get(url.fullPath.takeIf(String::isNotEmpty) ?: "/") {
                            val queryParams = call.request.queryParameters
                                try {
                                    queryParams["error"]?.let { error ->
                                        error("error=$error")
                                    }
                                    require(uuid == UUID.fromString(requireNotNull(queryParams["state"])))
                                    val authCode = requireNotNull(queryParams["code"])
                                    call.respond(HttpStatusCode.OK)
                                    continuation.resumeWith(Result.success(authCode))
                                } catch (e: Exception) {
                                    call.respond(HttpStatusCode.BadRequest)
                                    continuation.resumeWithException(e)
                                }
                        }
                    }
                }.start(wait = false)

                CoroutineScope(continuation.context).launch {
                    requestUserAuthorization("$TODOIST_ROOT_URL/oauth/authorize$params")
                }
            }
        } finally {
            server?.stop()
            server = null
        }
    }

    override suspend fun getToken(code: String): TodoistServiceAuthenticator.OAuthToken {
        val response = httpClient.post("oauth/access_token") {
            parameter("client_id", config.clientId)
            parameter("client_secret", config.clientSecret)
            parameter("code", code)
            parameter("redirect_uri", config.redirectUrl)
            contentType(ContentType.Application.FormUrlEncoded)
        }

        if (response.status.isSuccess()) {
            return response.body()
        } else {
            throw ClientRequestException(response, response.bodyAsText())
        }
    }
}