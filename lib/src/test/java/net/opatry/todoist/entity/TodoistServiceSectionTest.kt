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
import net.opatry.todoist.entity.data.sectionData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class TodoistServiceSectionTest {

    @Test
    fun `TodoistService getSections`() {
        val testData = sectionData.first()

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
            val sections = todoService.getSections("42")
            assertEquals("/v2/sections", request?.url?.encodedPath)
            assertEquals("project_id=42", request?.url?.encodedQuery)
            assertEquals(HttpMethod.Get, request?.method)
            assertEquals(listOf(testData.expectedEntity), sections)
        }
    }

    @Test
    fun `TodoistService getSections failure`() {
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
                    todoService.getSections("7025")
                }
            }
        }
    }

    @Test
    fun `TodoistService createSection`() {
        var request: HttpRequestData? = null
        val mockEngine = MockEngine {
            request = it
            respond(
                content = ByteReadChannel(
                    """{
                        "id": "7025",
                        "project_id": "2203306141",
                        "order": 1,
                        "name": "Groceries"
                    }""".trimIndent()
                ),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        usingTodoistService(mockEngine) { todoService ->
            val sectionData = TodoistSectionCreationRequest("Groceries", "2203306141")

            val section = todoService.createSection(sectionData)
            assertEquals("/v2/sections", request?.url?.encodedPath)
            assertEquals("", request?.url?.encodedQuery)
            assertEquals(HttpMethod.Post, request?.method)
            val expected = TodoistSection(
                id = "7025",
                projectId = "2203306141",
                order = 1,
                name = "Groceries"
            )
            assertEquals(expected, section)
        }
    }

    @Test
    fun `TodoistService createSection with order`() {
        var request: HttpRequestData? = null
        val mockEngine = MockEngine {
            request = it
            respond(
                content = ByteReadChannel(
                    """{
                        "id": "7025",
                        "project_id": "2203306141",
                        "order": 5,
                        "name": "Groceries"
                    }""".trimIndent()
                ),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        usingTodoistService(mockEngine) { todoService ->
            val sectionData = TodoistSectionCreationRequest("Groceries", "2203306141", 5)

            val section = todoService.createSection(sectionData)
            assertEquals("/v2/sections", request?.url?.encodedPath)
            assertEquals("", request?.url?.encodedQuery)
            assertEquals(HttpMethod.Post, request?.method)
            val expected = TodoistSection(
                id = "7025",
                projectId = "2203306141",
                order = 5,
                name = "Groceries"
            )
            assertEquals(expected, section)
        }
    }

    @Test
    fun `TodoistService createSection failure`() {
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
                    todoService.createSection(TodoistSectionCreationRequest("Foo", "7025"))
                }
            }
        }
    }

    @Test
    fun `TodoistService getSection`() {
        val testData = sectionData.first()

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
            val section = todoService.getSection("7025")
            assertEquals("/v2/sections/7025", request?.url?.encodedPath)
            assertEquals("", request?.url?.encodedQuery)
            assertEquals(HttpMethod.Get, request?.method)
            assertEquals(testData.expectedEntity, section)
        }
    }

    @Test
    fun `TodoistService getSection failure`() {
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
                    todoService.getSection("7025")
                }
            }
        }
    }

    @Test
    fun `TodoistService updateSection`() {
        val testData = sectionData.first()

        var request: HttpRequestData? = null
        val mockEngine = MockEngine {
            request = it
            respond(
                content = ByteReadChannel(
                    """{
                        "id": "7025",
                        "project_id": "2203306141",
                        "order": 1,
                        "name": "Supermarket"
                    }""".trimIndent()
                ),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        usingTodoistService(mockEngine) { todoService ->
            val section = todoService.updateSection("7025", TodoistSectionUpdateRequest(name = "Supermarket"))
            assertEquals("/v2/sections/7025", request?.url?.encodedPath)
            assertEquals("", request?.url?.encodedQuery)
            assertEquals(HttpMethod.Post, request?.method)
            val expected = (testData.expectedEntity as TodoistSection).copy(name = "Supermarket")
            assertEquals(expected, section)
        }
    }

    @Test
    fun `TodoistService updateSection failure`() {
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
                    todoService.updateSection("7025", TodoistSectionUpdateRequest(name = "Supermarket"))
                }
            }
        }
    }

    @Test
    fun `TodoistService deleteSection`() {
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
            todoService.deleteSection("2992679862")
            assertEquals("/v2/sections/2992679862", request?.url?.encodedPath)
            assertEquals("", request?.url?.encodedQuery)
            assertEquals(HttpMethod.Delete, request?.method)
        }
    }

    @Test
    fun `TodoistService deleteSection failure`() {
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
                    todoService.deleteSection("2992679862")
                }
            }
        }
    }
}