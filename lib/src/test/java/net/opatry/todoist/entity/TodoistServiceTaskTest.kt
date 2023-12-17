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

package net.opatry.todoist.entity

import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.request.HttpRequestData
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.runBlocking
import net.opatry.todoist.entity.data.EntityTestParam
import net.opatry.todoist.entity.data.taskData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class TodoistServiceTaskTest {

    @Test
    fun `TodoistService getTasks`() {
        val tasksPayload = taskData.map(EntityTestParam::jsonPayload).joinToString(",", "[", "]")

        var request: HttpRequestData? = null
        val mockEngine = MockEngine {
            request = it
            respond(
                content = ByteReadChannel(tasksPayload),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        usingTodoistService(mockEngine) { todoService ->
            val tasks = todoService.getTasks()
            assertEquals("/v2/tasks", request?.url?.encodedPath)
            assertEquals("", request?.url?.encodedQuery)
            assertEquals(HttpMethod.Get, request?.method)
            assertEquals(taskData.map(EntityTestParam::expectedEntity), tasks)
        }
    }

    @Test
    fun `TodoistService getTasks failure`() {
        val mockEngine = MockEngine {
            respond(
                content = ByteReadChannel(""),
                status = HttpStatusCode.Forbidden,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        usingTodoistService(mockEngine) { todoService ->
            assertThrows(ClientRequestException::class.java) {
                runBlocking {
                    todoService.getTasks()
                }
            }
        }
    }

    @Test
    fun `TodoistService createTask`() {
        var request: HttpRequestData? = null
        val mockEngine = MockEngine {
            request = it
            respond(
                content = ByteReadChannel(
                    """{
                        "creator_id": "2671355",
                        "created_at": "2019-12-11T22:36:50.000000Z",
                        "assignee_id": null,
                        "assigner_id": null,
                        "comment_count": 0,
                        "is_completed": false,
                        "content": "Buy Milk",
                        "description": "",
                        "due": {
                            "date": "2016-09-01",
                            "is_recurring": false,
                            "datetime": "2016-09-01T12:00:00.000000Z",
                            "string": "tomorrow at 12",
                            "timezone": "Europe/Moscow"
                        },
                        "duration": null,
                        "id": "2995104339",
                        "labels": [],
                        "order": 1,
                        "priority": 4,
                        "project_id": "2203306141",
                        "section_id": null,
                        "parent_id": null,
                        "url": "https://todoist.com/showTask?id=2995104339"
                    }""".trimIndent()
                ),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        usingTodoistService(mockEngine) { todoService ->
            val taskData = TodoistTaskCreationRequest(
                "Buy Milk",
                dueString = "tomorrow at 12:00",
                dueLang = "en",
                priority = 4
            )

            val task = todoService.createTask(taskData)
            assertEquals("/v2/tasks", request?.url?.encodedPath)
            assertEquals("", request?.url?.encodedQuery)
            assertEquals(HttpMethod.Post, request?.method)
            val expected = TodoistTask(
                creatorId = "2671355",
                createdAt = "2019-12-11T22:36:50.000000Z",
                assigneeId = null,
                assignerId = null,
                commentCount = 0,
                isCompleted = false,
                content = "Buy Milk",
                description = "",
                due = TodoistTask.DueDate(
                    date = "2016-09-01",
                    isRecurring = false,
                    datetime = "2016-09-01T12:00:00.000000Z",
                    string = "tomorrow at 12",
                    timezone = "Europe/Moscow"
                ),
                duration = null,
                id = "2995104339",
                labels = emptyList(),
                order = 1,
                priority = 4,
                projectId = "2203306141",
                sectionId = null,
                parentId = null,
                url = "https://todoist.com/showTask?id=2995104339"
            )
            assertEquals(expected, task)
        }
    }

    @Test
    fun `TodoistService createTask failure`() {
        val mockEngine = MockEngine {
            respond(
                content = ByteReadChannel(""),
                status = HttpStatusCode.Forbidden,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        usingTodoistService(mockEngine) { todoService ->
            assertThrows(ClientRequestException::class.java) {
                runBlocking {
                    todoService.createTask(TodoistTaskCreationRequest("Foo", "2995104339"))
                }
            }
        }
    }

    @Test
    fun `TodoistService getTask`() {
        val testData = taskData.first()

        var request: HttpRequestData? = null
        val mockEngine = MockEngine {
            request = it
            respond(
                content = ByteReadChannel(testData.jsonPayload),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        usingTodoistService(mockEngine) { todoService ->
            val task = todoService.getTask("2995104339")
            assertEquals("/v2/tasks/2995104339", request?.url?.encodedPath)
            assertEquals("", request?.url?.encodedQuery)
            assertEquals(HttpMethod.Get, request?.method)
            assertEquals(testData.expectedEntity, task)
        }
    }

    @Test
    fun `TodoistService getTask failure`() {
        val mockEngine = MockEngine {
            respond(
                content = ByteReadChannel(""),
                status = HttpStatusCode.Forbidden,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        usingTodoistService(mockEngine) { todoService ->
            assertThrows(ClientRequestException::class.java) {
                runBlocking {
                    todoService.getTask("2995104339")
                }
            }
        }
    }

    @Test
    fun `TodoistService updateTask`() {
        var request: HttpRequestData? = null
        val mockEngine = MockEngine {
            request = it
            respond(
                content = ByteReadChannel(
                    """{
                        "creator_id": "2671355",
                        "created_at": "2019-12-11T22:36:50.000000Z",
                        "assignee_id": "2671362",
                        "assigner_id": "2671355",
                        "comment_count": 10,
                        "is_completed": false,
                        "content": "Buy Coffee",
                        "description": "",
                        "due": {
                            "date": "2016-09-01",
                            "is_recurring": false,
                            "datetime": "2016-09-01T12:00:00.000000Z",
                            "string": "tomorrow at 12",
                            "timezone": "Europe/Moscow"
                        },
                        "duration": null,
                        "id": "2995104339",
                        "labels": ["Food", "Shopping"],
                        "order": 1,
                        "priority": 1,
                        "project_id": "2203306141",
                        "section_id": "2995104339",
                        "parent_id": "2995104589",
                        "url": "https://todoist.com/showTask?id=2995104339"
                    }""".trimIndent()
                ),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        usingTodoistService(mockEngine) { todoService ->
            val task = todoService.updateTask("2995104339", TodoistTaskUpdateRequest(content = "Buy Coffee"))
            assertEquals("/v2/tasks/2995104339", request?.url?.encodedPath)
            assertEquals("", request?.url?.encodedQuery)
            assertEquals(HttpMethod.Post, request?.method)
            val expected = TodoistTask(
                creatorId = "2671355",
                createdAt = "2019-12-11T22:36:50.000000Z",
                assigneeId = "2671362",
                assignerId = "2671355",
                commentCount = 10,
                isCompleted = false,
                content = "Buy Coffee",
                description = "",
                due = TodoistTask.DueDate(
                    date = "2016-09-01",
                    isRecurring = false,
                    datetime = "2016-09-01T12:00:00.000000Z",
                    string = "tomorrow at 12",
                    timezone = "Europe/Moscow"
                ),
                duration = null,
                id = "2995104339",
                labels = listOf("Food", "Shopping"),
                order = 1,
                priority = 1,
                projectId = "2203306141",
                sectionId = "2995104339",
                parentId = "2995104589",
                url = "https://todoist.com/showTask?id=2995104339"
            )
            assertEquals(expected, task)
        }
    }

    @Test
    fun `TodoistService updateTask failure`() {
        val mockEngine = MockEngine {
            respond(
                content = ByteReadChannel(""),
                status = HttpStatusCode.Forbidden,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        usingTodoistService(mockEngine) { todoService ->
            assertThrows(ClientRequestException::class.java) {
                runBlocking {
                    todoService.updateTask("2995104339", TodoistTaskUpdateRequest(content = "Bar"))
                }
            }
        }
    }

    @Test
    fun `TodoistService deleteTask`() {
        var request: HttpRequestData? = null
        val mockEngine = MockEngine {
            request = it
            respond(
                content = ByteReadChannel(""),
                status = HttpStatusCode.NoContent,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        usingTodoistService(mockEngine) { todoService ->
            todoService.deleteTask("2992679862")
            assertEquals("/v2/tasks/2992679862", request?.url?.encodedPath)
            assertEquals("", request?.url?.encodedQuery)
            assertEquals(HttpMethod.Delete, request?.method)
        }
    }

    @Test
    fun `TodoistService deleteTask failure`() {
        val mockEngine = MockEngine {
            respond(
                content = ByteReadChannel(""),
                status = HttpStatusCode.Forbidden,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        usingTodoistService(mockEngine) { todoService ->
            assertThrows(ClientRequestException::class.java) {
                runBlocking {
                    todoService.deleteTask("2992679862")
                }
            }
        }
    }
}