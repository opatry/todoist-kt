/*
 * Copyright (c) 2023 Olivier Patry
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
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import net.opatry.todoist.sync.TodoistSyncCommand
import net.opatry.todoist.sync.TodoistSyncResult
import net.opatry.todoist.sync.entity.TodoistSyncProjectInfo

interface TodoistSyncService {
    /**
     * To retrieve your user resources, make a Sync API request with the following parameters:
     *
     * #### Incremental sync
     * The Sync API allows clients to retrieve only updated resources, and this is done by using the `sync_token` in your Sync API request.
     *
     * On your initial sync request, specify `sync_token=*` in your request, and all the user's active resource data will be returned. The server will also return a new `sync_token` in the Sync API response.
     *
     * In your subsequent Sync request, use the `sync_token` that you received from your previous sync response, and the Todoist API server will return only the updated resource data.
     *
     * @param syncToken A special string, used to allow the client to perform incremental sync. Pass `*` to retrieve all active resource data.
     * @param resourceTypes Used to specify what resources to fetch from the server.
     * Here is a list of available resource types: `labels`, `projects`, `items`, `notes`, `sections`, `filters`,
     * `reminders`, `reminders_location`, `locations`, `user`, `live_notifications`, `collaborators`, `user_settings`,
     * `notification_settings`, `user_plan_limits`, `completed_info`, `stats`.
     * You may use all to include all the resource types.
     * Resources can also be excluded by prefixing a `-` prior to the name, for example, `-projects`
     * @param commands An array of Command objects. Each command will be processed in the specified order.
     *
     * In order to fetch both types of reminders you must include both resource types in your request, for example: `resource_types=["reminders", "reminders_location"]`.
     *
     * @return an object containing the requested resources and a new `sync_token`.
     *
     * @see [TodoistSyncResult.syncToken]
     */
    suspend fun sync(syncToken: String, resourceTypes: List<String>, commands: List<TodoistSyncCommand>? = null): TodoistSyncResult

    /**
     * This function is used to extract detailed information about the project, including all the notes.
     *
     * It is especially important, because a full sync only returns up to 10 notes for each project. If a client requires more, they can be downloaded using this endpoint.
     *
     * It returns an object with the [TodoistSyncProjectInfo.project], and optionally the [TodoistSyncProjectInfo.notes] attributes.
     *
     * @param projectId
     * @param allData Whether to return the notes of the project (the default is `true`).
     */
    suspend fun getProjectInfo(projectId: String, allData: Boolean? = true): TodoistSyncProjectInfo
}

class HttpTodoistSyncService(private val httpClient: HttpClient) : TodoistSyncService {
    private companion object {
        suspend inline fun <reified T> HttpClient.getOrThrow(
            endpoint: String,
            parameters: Map<String, Any> = emptyMap()
        ): T {
            val response = get(endpoint) {
                contentType(ContentType.Application.Json)
                parameters.forEach { (k, v) ->
                    parameter(k, v)
                }
            }

            if (response.status.isSuccess()) {
                return response.body()
            } else {
                throw ClientRequestException(response, response.bodyAsText())
            }
        }

        suspend inline fun <reified T, reified R> HttpClient.postOrThrow(endpoint: String, body: T): R {
            val response = post(endpoint) {
                contentType(ContentType.Application.Json)
                setBody(body)
            }

            if (response.status.isSuccess()) {
                return response.body()
            } else {
                throw ClientRequestException(response, response.bodyAsText())
            }
        }
    }

    override suspend fun sync(
        syncToken: String,
        resourceTypes: List<String>,
        commands: List<TodoistSyncCommand>?
    ): TodoistSyncResult {
        return httpClient.postOrThrow(
            "v9/sync",
            buildMap {
                put("sync_token", syncToken)
                // FIXME in doc, it's mentioned resource_types='["foo", "bar"]' with encapsulating single quotes but this doesn't work
                // TODO Obj to Json with Gson should do the trick
                put("resource_types", resourceTypes.joinToString(prefix = "[", postfix = "]") { "\"$it\"" })
                commands?.let { put("commands", commands) }
            }
        )
    }

    override suspend fun getProjectInfo(projectId: String, allData: Boolean?): TodoistSyncProjectInfo {
        return httpClient.postOrThrow("v9/projects/get", mapOf("project_id" to projectId))
    }
}