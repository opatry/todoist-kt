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

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.CurlUserAgent
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.URLBuilder
import io.ktor.http.encodedPath
import io.ktor.http.takeFrom
import io.ktor.serialization.gson.gson
import kotlinx.coroutines.runBlocking
import net.opatry.todoist.sync.TodoistSyncCommand
import net.opatry.todoist.sync.TodoistSyncResult
import java.awt.Desktop
import java.io.File
import java.net.URI


suspend fun runAuthenticationFlow(): TodoistServiceAuthenticator.OAuthToken {
    val permissions = TodoistServiceAuthenticator.Permission.entries
    val config = HttpTodoistServiceAuthenticator.ApplicationConfig(
        redirectUrl = "http://localhost:8888/todoist-callback",
        clientId = System.getenv("TODOIST_API_CLIENT_ID"),
        clientSecret = System.getenv("TODOIST_API_CLIENT_SECRET"),
    )
    val authenticator: TodoistServiceAuthenticator = HttpTodoistServiceAuthenticator(config)
    // even if we have a code, can't reuse it, authorize again
    val code = authenticator.authorize(permissions) { url ->
        Desktop.getDesktop().browse(URI.create(url))
    }

    return authenticator.getToken(code)
}

private fun httpClient(token: String): HttpClient = HttpClient(CIO) {
    CurlUserAgent()
    install(ContentNegotiation) {
        gson()
    }
    install(Auth) {
        bearer {
            sendWithoutRequest { true }
            loadTokens {
                BearerTokens(token, "")
            }
        }
    }
    defaultRequest {
        // Directly using `url("https://api.todoist.com/sync")` won't work if url ends with base path like `/sync`,
        // most likely because ktor will simply replace the full encoded path by the requested path and not combine both.
        // Here, we prepend base path coming from configuration if any to enforce it if not explicitly requesting
        // an absolute base path (starting with '/').
        if (url.host.isEmpty()) {
            val defaultUrl = URLBuilder().takeFrom("https://api.todoist.com/sync")
            url.host = defaultUrl.host
            url.protocol = defaultUrl.protocol
            if (!url.encodedPath.startsWith('/')) {
                // prepend base path from configuration
                val basePath = defaultUrl.encodedPath
                url.encodedPath = "$basePath/${url.encodedPath}"
            }
        }
    }
}

fun main() {
    // totally unsafe, quick & dirty persistence of access token to avoid authorizing at each launch
    val tokenCacheFile = File("token_cache.txt")
    runBlocking {

        // region access token negotiation
        val tokenCache = if (tokenCacheFile.isFile) tokenCacheFile.readText() else ""
        val accessToken = tokenCache.ifEmpty {
            val token = runAuthenticationFlow()
            tokenCacheFile.writeText(token.accessToken)
            token.accessToken
        }
        // endregion

//        val httpClient = httpClient(System.getenv("TODOIST_API_TOKEN"))
        val httpClient = httpClient(accessToken)
        val syncService: TodoistSyncService = HttpTodoistSyncService(httpClient)
        val result = syncService.sync("*", listOf("projects"))
        println("result=$result")
        result.syncStatus?.forEach { (uuid, status) ->
            println("status $uuid=${TodoistSyncResult.mapSyncStatus(status)}")
        }
        val result2 = syncService.sync(result.syncToken, listOf("projects"))
        println("result2=$result2")
        result2.syncStatus?.forEach { (uuid, status) ->
            println("status $uuid=${TodoistSyncResult.mapSyncStatus(status)}")
        }

        val p = syncService.getProjectInfo(result.projects.first().id)
        println("p=$p")

        val result3 = syncService.sync(result2.syncToken, listOf("projects"), listOf(
            TodoistSyncCommand.DeleteProject("2217382345")
        ))
        println("result3=$result3")
        result3.syncStatus?.forEach { (uuid, status) ->
            println("status $uuid=${TodoistSyncResult.mapSyncStatus(status)}")
        }

        val result4 = syncService.sync(result3.syncToken, listOf("projects"), listOf(
            TodoistSyncCommand.AddProject(
                "Hello World! Sync Project"
            )
        ))
        println("result4=$result4")
        result4.syncStatus?.forEach { (uuid, status) ->
            println("status $uuid=${TodoistSyncResult.mapSyncStatus(status)}")
        }
    }
}