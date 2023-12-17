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
import net.opatry.todoist.entity.data.commentData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class TodoistServiceCommentTest {

    @Test
    fun `TodoistService getProjectComments`() {
        val projectComments = commentData.filterNot { it.jsonPayload.contains("\"project_id\": null") }
        val commentsPayload = projectComments.map(EntityTestParam::jsonPayload).joinToString(",", "[", "]")

        var request: HttpRequestData? = null
        val mockEngine = MockEngine {
            request = it
            respond(
                content = ByteReadChannel(commentsPayload),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        usingTodoistService(mockEngine) { todoService ->
            val comments = todoService.getProjectComments("42")
            assertEquals("/v2/comments", request?.url?.encodedPath)
            assertEquals("project_id=42", request?.url?.encodedQuery)
            assertEquals(HttpMethod.Get, request?.method)
            assertEquals(projectComments.map(EntityTestParam::expectedEntity), comments)
        }
    }

    @Test
    fun `TodoistService getTaskComments`() {
        val taskComments = commentData.filterNot { it.jsonPayload.contains("\"task_id\": null") }
        val commentsPayload = taskComments.map(EntityTestParam::jsonPayload).joinToString(",", "[", "]")

        var request: HttpRequestData? = null
        val mockEngine = MockEngine {
            request = it
            respond(
                content = ByteReadChannel(commentsPayload),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        usingTodoistService(mockEngine) { todoService ->
            val comments = todoService.getTaskComments("666")
            assertEquals("/v2/comments", request?.url?.encodedPath)
            assertEquals("task_id=666", request?.url?.encodedQuery)
            assertEquals(HttpMethod.Get, request?.method)
            assertEquals(taskComments.map(EntityTestParam::expectedEntity), comments)
        }
    }

    @Test
    fun `TodoistService getComments failure`() {
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
                    todoService.getTaskComments("42")
                }
            }
        }
    }

    @Test
    fun `TodoistService createProjectComment`() {
        var request: HttpRequestData? = null
        val mockEngine = MockEngine {
            request = it
            respond(
                content = ByteReadChannel(
                    """{
                        "content": "Need one bottle of milk",
                        "id": "2992679862",
                        "posted_at": "2016-09-22T07:00:00.000000Z",
                        "project_id": "2995104339",
                        "task_id": null
                    }""".trimIndent()
                ),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        usingTodoistService(mockEngine) { todoService ->
            val commentData = TodoistCommentCreationRequest.Project("2995104339", "Need one bottle of milk")

            val comment = todoService.createComment(commentData)
            assertEquals("/v2/comments", request?.url?.encodedPath)
            assertEquals("", request?.url?.encodedQuery)
            assertEquals(HttpMethod.Post, request?.method)
            val expected = TodoistComment(
                id = "2992679862",
                taskId = null,
                projectId = "2995104339",
                postedAt = "2016-09-22T07:00:00.000000Z",
                content = "Need one bottle of milk",
                attachment = null
            )
            assertEquals(expected, comment)
        }
    }

    @Test
    fun `TodoistService createTaskComment`() {
        var request: HttpRequestData? = null
        val mockEngine = MockEngine {
            request = it
            respond(
                content = ByteReadChannel(
                    """{
                        "content": "Need one bottle of milk",
                        "id": "2992679862",
                        "posted_at": "2016-09-22T07:00:00.000000Z",
                        "project_id": null,
                        "task_id": "2995104339"
                    }""".trimIndent()
                ),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        usingTodoistService(mockEngine) { todoService ->
            val commentData = TodoistCommentCreationRequest.Task("2995104339", "Need one bottle of milk")

            val comment = todoService.createComment(commentData)
            assertEquals("/v2/comments", request?.url?.encodedPath)
            assertEquals("", request?.url?.encodedQuery)
            assertEquals(HttpMethod.Post, request?.method)
            val expected = TodoistComment(
                id = "2992679862",
                taskId = "2995104339",
                projectId = null,
                postedAt = "2016-09-22T07:00:00.000000Z",
                content = "Need one bottle of milk",
                attachment = null
            )
            assertEquals(expected, comment)
        }
    }

    @Test
    fun `TodoistService createComment failure`() {
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
                    todoService.createComment(TodoistCommentCreationRequest.Task("42", "Foo"))
                }
            }
        }
    }

    @Test
    fun `TodoistService getComment`() {
        val testData = commentData[1]

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
            val comment = todoService.getComment("42")
            assertEquals("/v2/comments/42", request?.url?.encodedPath)
            assertEquals("", request?.url?.encodedQuery)
            assertEquals(HttpMethod.Get, request?.method)
            assertEquals(testData.expectedEntity, comment)
        }
    }

    @Test
    fun `TodoistService getComment failure`() {
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
                    todoService.getComment("2992679862")
                }
            }
        }
    }

    @Test
    fun `TodoistService updateComment`() {
        val testData = commentData[1]

        var request: HttpRequestData? = null
        val mockEngine = MockEngine {
            request = it
            respond(
                content = ByteReadChannel(
                    """{
                        "content": "Need two bottles of milk",
                        "id": "2992679863",
                        "posted_at": "2016-09-22T07:00:00.000000Z",
                        "project_id": "2995104330",
                        "task_id": null
                    }""".trimIndent()
                ),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        usingTodoistService(mockEngine) { todoService ->
            val comment = todoService.updateComment(
                "2992679862",
                TodoistCommentUpdateRequest(content = "Need two bottles of milk")
            )
            assertEquals("/v2/comments/2992679862", request?.url?.encodedPath)
            assertEquals("", request?.url?.encodedQuery)
            assertEquals(HttpMethod.Post, request?.method)
            val expected = (testData.expectedEntity as TodoistComment).copy(content = "Need two bottles of milk")
            assertEquals(expected, comment)
        }
    }

    @Test
    fun `TodoistService updateComment failure`() {
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
                    todoService.updateComment(
                        "2992679862",
                        TodoistCommentUpdateRequest(content = "Need two bottles of milk")
                    )
                }
            }
        }
    }

    @Test
    fun `TodoistService deleteComment`() {
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
            todoService.deleteComment("2992679862")
            assertEquals("/v2/comments/2992679862", request?.url?.encodedPath)
            assertEquals("", request?.url?.encodedQuery)
            assertEquals(HttpMethod.Delete, request?.method)
        }
    }

    @Test
    fun `TodoistService deleteComment failure`() {
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
                    todoService.deleteComment("2992679862")
                }
            }
        }
    }
}