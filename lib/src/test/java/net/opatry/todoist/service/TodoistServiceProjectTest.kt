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

package net.opatry.todoist.service

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
import net.opatry.todoist.entity.TodoistProject
import net.opatry.todoist.entity.TodoistProjectCreationRequest
import net.opatry.todoist.entity.TodoistProjectUpdateRequest
import net.opatry.todoist.entity.data.projectData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class TodoistServiceProjectTest {

    @Test
    fun `TodoistService getProjects`() {
        val testData = projectData.first()

        var request: HttpRequestData? = null
        val mockEngine = MockEngine {
            request = it
            respond(
                content = ByteReadChannel("[${testData.jsonPayload}]"),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        usingTodoistService(mockEngine) { todoService ->
            val projects = todoService.getProjects()
            assertEquals("/v2/projects", request?.url?.encodedPath)
            assertEquals("", request?.url?.encodedQuery)
            assertEquals(HttpMethod.Get, request?.method)
            assertEquals(listOf(testData.expectedEntity), projects)
        }
    }

    @Test
    fun `TodoistService getProjects failure`() {
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
                    todoService.getProjects()
                }
            }
        }
    }

    @Test
    fun `TodoistService createProject`() {
        var request: HttpRequestData? = null
        val mockEngine = MockEngine {
            request = it
            respond(
                content = ByteReadChannel(
                    """{
                        "id": "2203306141",
                        "name": "Shopping List",
                        "comment_count": 0,
                        "color": "charcoal",
                        "is_shared": false,
                        "order": 1,
                        "is_favorite": true,
                        "is_inbox_project": false,
                        "is_team_inbox": false,
                        "view_style": "list",
                        "url": "https://todoist.com/showProject?id=2203306141",
                        "parent_id": null
                    }""".trimIndent()
                ),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        usingTodoistService(mockEngine) { todoService ->
            val projectData = TodoistProjectCreationRequest("Shopping List")

            val project = todoService.createProject(projectData)
            assertEquals("/v2/projects", request?.url?.encodedPath)
            assertEquals("", request?.url?.encodedQuery)
            assertEquals(HttpMethod.Post, request?.method)
            val expected = TodoistProject(
                id = "2203306141",
                name = "Shopping List",
                commentCount = 0,
                color = "charcoal",
                isShared = false,
                order = 1,
                isFavorite = true,
                isInboxProject = false,
                isTeamInbox = false,
                viewStyle = TodoistProject.ViewStyle.List,
                url = "https://todoist.com/showProject?id=2203306141",
                parentId = null
            )
            assertEquals(expected, project)
        }
    }

    @Test
    fun `TodoistService createProject failure`() {
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
                    todoService.createProject(TodoistProjectCreationRequest("Foo"))
                }
            }
        }
    }

    @Test
    fun `TodoistService getProject`() {
        val testData = projectData.first()

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
            val project = todoService.getProject("2203306141")
            assertEquals("/v2/projects/2203306141", request?.url?.encodedPath)
            assertEquals("", request?.url?.encodedQuery)
            assertEquals(HttpMethod.Get, request?.method)
            assertEquals(testData.expectedEntity, project)
        }
    }

    @Test
    fun `TodoistService getProject failure`() {
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
                    todoService.getProject("2203306141")
                }
            }
        }
    }

    @Test
    fun `TodoistService updateProject`() {
        var request: HttpRequestData? = null
        val mockEngine = MockEngine {
            request = it
            respond(
                content = ByteReadChannel(
                    """{
                        "id": "2203306141",
                        "name": "Things to buy",
                        "comment_count": 0,
                        "color": "charcoal",
                        "is_shared": false,
                        "order": 1,
                        "is_favorite": false,
                        "is_inbox_project": false,
                        "is_team_inbox": false,
                        "view_style": "list",
                        "url": "https://todoist.com/showProject?id=2203306141",
                        "parent_id": null
                    }""".trimIndent()
                ),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        usingTodoistService(mockEngine) { todoService ->
            val project = todoService.updateProject("2203306141", TodoistProjectUpdateRequest(name = "Things to buy"))
            assertEquals("/v2/projects/2203306141", request?.url?.encodedPath)
            assertEquals("", request?.url?.encodedQuery)
            assertEquals(HttpMethod.Post, request?.method)
            val expected = TodoistProject(
                id = "2203306141",
                name = "Things to buy",
                commentCount = 0,
                color = "charcoal",
                isShared = false,
                order = 1,
                isFavorite = false,
                isInboxProject = false,
                isTeamInbox = false,
                viewStyle = TodoistProject.ViewStyle.List,
                url = "https://todoist.com/showProject?id=2203306141",
                parentId = null
            )
            assertEquals(expected, project)
        }
    }

    @Test
    fun `TodoistService updateProject failure`() {
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
                    todoService.updateProject("2995104339", TodoistProjectUpdateRequest(name = "Bar"))
                }
            }
        }
    }

    @Test
    fun `TodoistService deleteProject`() {
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
            todoService.deleteProject("2992679862")
            assertEquals("/v2/projects/2992679862", request?.url?.encodedPath)
            assertEquals("", request?.url?.encodedQuery)
            assertEquals(HttpMethod.Delete, request?.method)
        }
    }

    @Test
    fun `TodoistService deleteProject failure`() {
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
                    todoService.deleteProject("2992679862")
                }
            }
        }
    }
}