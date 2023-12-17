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
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import net.opatry.todoist.entity.TodoistCollaborator
import net.opatry.todoist.entity.TodoistComment
import net.opatry.todoist.entity.TodoistCommentCreationRequest
import net.opatry.todoist.entity.TodoistCommentUpdateRequest
import net.opatry.todoist.entity.TodoistLabel
import net.opatry.todoist.entity.TodoistLabelCreationRequest
import net.opatry.todoist.entity.TodoistLabelUpdateRequest
import net.opatry.todoist.entity.TodoistProject
import net.opatry.todoist.entity.TodoistProjectCreationRequest
import net.opatry.todoist.entity.TodoistProjectUpdateRequest
import net.opatry.todoist.entity.TodoistSection
import net.opatry.todoist.entity.TodoistSectionCreationRequest
import net.opatry.todoist.entity.TodoistSectionUpdateRequest
import net.opatry.todoist.entity.TodoistSharedLabelRemovalRequest
import net.opatry.todoist.entity.TodoistSharedLabelRenameRequest
import net.opatry.todoist.entity.TodoistTask
import net.opatry.todoist.entity.TodoistTaskCreationRequest
import net.opatry.todoist.entity.TodoistTaskUpdateRequest

interface TodoistService {

    // region Projects

    /**
     * Returns all user projects.
     */
    suspend fun getProjects(): List<TodoistProject>

    /**
     * Returns a [TodoistProject] object related to the given ID.
     */
    suspend fun getProject(projectId: String): TodoistProject

    /**
     * Creates a new project and returns it as a [TodoistProject] object.
     */
    suspend fun createProject(request: TodoistProjectCreationRequest): TodoistProject

    /**
     * Returns a [TodoistProject] object of the updated project object.
     */
    suspend fun updateProject(projectId: String, request: TodoistProjectUpdateRequest): TodoistProject

    /**
     * Deletes a project.
     */
    suspend fun deleteProject(projectId: String)

    /**
     * Returns all collaborators of a shared project.
     */
    suspend fun getCollaborators(projectId: String): List<TodoistCollaborator>

    // endregion

    // region Sections

    /**
     * Returns all sections.
     */
    suspend fun getSections(projectId: String): List<TodoistSection>

    /**
     * Creates a new section and returns it as a [TodoistSection] object.
     */
    suspend fun createSection(request: TodoistSectionCreationRequest): TodoistSection

    /**
     * Returns a single section as a [TodoistSection] object.
     */
    suspend fun getSection(sectionId: String): TodoistSection

    /**
     * Returns the updated section as a [TodoistSection] object.
     */
    suspend fun updateSection(sectionId: String, request: TodoistSectionUpdateRequest): TodoistSection

    /**
     * Deletes a section.
     */
    suspend fun deleteSection(sectionId: String)

    // endregion

    // region Tasks

    /**
     * Returns all active tasks.
     */
    suspend fun getTasks(): List<TodoistTask>

    /**
     * Creates a new task and returns it as a [TodoistTask] object.
     */
    suspend fun createTask(request: TodoistTaskCreationRequest): TodoistTask

    /**
     * Returns a single active (non-completed) task by ID as a [TodoistTask] object.
     */
    suspend fun getTask(taskId: String): TodoistTask

    /**
     * Returns the updated task as a a [TodoistTask] object.
     */
    suspend fun updateTask(taskId: String, request: TodoistTaskUpdateRequest): TodoistTask

    /**
     * Closes a task.
     *
     * The command performs in the same way as our official clients:
     *  - Regular tasks are marked complete and moved to history, along with their subtasks.
     *  - Tasks with [recurring due dates](https://todoist.com/help/articles/set-a-recurring-due-date-YUYVJJAV) will be scheduled to their next occurrence.
     */
    suspend fun closeTask(task: TodoistTask)

    /**
     * Reopens a task.
     *
     * Any ancestor items or sections will also be marked as uncomplete and restored from history.
     *
     * The reinstated items and sections will appear at the end of the list within their parent, after any previously active items.
     */
    suspend fun reopenTask(task: TodoistTask)

    /**
     * Deletes a task.
     */
    suspend fun deleteTask(taskId: String)

    // endregion

    // region Comments

    /**
     * Returns all project comments for a given [projectId].
     */
    suspend fun getProjectComments(projectId: String): List<TodoistComment>

    /**
     * Returns all task comments for a given [taskId].
     */
    suspend fun getTaskComments(taskId: String): List<TodoistComment>

    /**
     * Creates a new comment on a project or task and returns it as a [TodoistComment] object.
     * Note that one of [TodoistCommentCreationRequest.taskId] or [TodoistCommentCreationRequest.projectId] arguments is required.
     */
    suspend fun createComment(request: TodoistCommentCreationRequest): TodoistComment

    /**
     * Returns a single comment as a [TodoistComment] object.
     */
    suspend fun getComment(commentId: String): TodoistComment

    /**
     * Returns the updated comment as a [TodoistComment] object.
     */
    suspend fun updateComment(commentId: String, request: TodoistCommentUpdateRequest): TodoistComment

    /**
     * Deletes a comment.
     */
    suspend fun deleteComment(commentId: String)

    // endregion

    // region Labels

    /**
     * Returns all user labels.
     */
    suspend fun getLabels(): List<TodoistLabel>

    /**
     * Creates a new personal label and returns it as [TodoistLabel] object.
     */
    suspend fun createLabel(request: TodoistLabelCreationRequest): TodoistLabel

    /**
     * Returns a personal label by ID.
     */
    suspend fun getLabel(labelId: String): TodoistLabel

    /**
     * Returns the updated label.
     */
    suspend fun updateLabel(labelId: String, request: TodoistLabelUpdateRequest): TodoistLabel

    /**
     * Deletes a personal label, all instances of the label will be removed from tasks.
     */
    suspend fun deleteLabel(labelId: String)

    // endregion

    // region Shared Labels

    /**
     * Returns all labels currently assigned to tasks.
     *
     * By default, the names of a user's [personal labels](https://developer.todoist.com/rest/v2/#labels) will also be included. These can be excluded by passing the [omitPersonal] parameter.
     *
     * @param omitPersonal Whether to exclude the names of the user's personal labels from the results. The default value is `false`.
     */
    suspend fun getSharedLabels(omitPersonal: Boolean = false): List<String>

    /**
     * Renames all instances of a shared label.
     */
    suspend fun renameSharedLabel(request: TodoistSharedLabelRenameRequest)

    /**
     * Removes all instances of a shared label from the tasks where it is applied. If no instances of the label name are found, the request will still be considered successful.
     */
    suspend fun removeSharedLabel(request: TodoistSharedLabelRemovalRequest)

    // endregion
}

class HttpTodoistService(private val httpClient: HttpClient) : TodoistService {
    private companion object {
        suspend inline fun <reified T> HttpClient.getOrThrow(endpoint: String, parameters: Map<String, Any> = emptyMap()): T {
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

        suspend inline fun HttpClient.deleteOrThrow(endpoint: String) {
            val response = delete(endpoint)

            if (response.status.isSuccess()) {
                return response.body()
            } else {
                throw ClientRequestException(response, response.bodyAsText())
            }
        }
    }

    // region Projects

    override suspend fun getProjects(): List<TodoistProject> {
        return httpClient.getOrThrow("v2/projects")
    }

    override suspend fun getProject(projectId: String): TodoistProject {
        return httpClient.getOrThrow("v2/projects/$projectId")
    }

    override suspend fun createProject(request: TodoistProjectCreationRequest): TodoistProject {
        return httpClient.postOrThrow("v2/projects", request)
    }

    override suspend fun updateProject(projectId: String, request: TodoistProjectUpdateRequest): TodoistProject {
        return httpClient.postOrThrow("v2/projects/$projectId", request)
    }

    override suspend fun deleteProject(projectId: String) {
        return httpClient.deleteOrThrow("v2/projects/$projectId")
    }

    override suspend fun getCollaborators(projectId: String): List<TodoistCollaborator> {
        return httpClient.getOrThrow("v2/projects/$projectId/collaborators")
    }

    // endregion

    // region Sections

    override suspend fun getSections(projectId: String): List<TodoistSection> {
        return httpClient.getOrThrow("v2/sections", mapOf("project_id" to projectId))
    }

    override suspend fun createSection(request: TodoistSectionCreationRequest): TodoistSection {
        return httpClient.postOrThrow("v2/sections", request)
    }

    override suspend fun getSection(sectionId: String): TodoistSection {
        return httpClient.getOrThrow("v2/sections/$sectionId")
    }

    override suspend fun updateSection(sectionId: String, request: TodoistSectionUpdateRequest): TodoistSection {
        return httpClient.postOrThrow("v2/sections/$sectionId", request)
    }

    override suspend fun deleteSection(sectionId: String) {
        return httpClient.deleteOrThrow("v2/sections/$sectionId")
    }

    // endregion

    // region Tasks

    override suspend fun getTasks(): List<TodoistTask> {
        return httpClient.getOrThrow("v2/tasks")
    }

    override suspend fun getTask(taskId: String): TodoistTask {
        return httpClient.getOrThrow("v2/tasks/$taskId")
    }

    override suspend fun createTask(request: TodoistTaskCreationRequest): TodoistTask {
        return httpClient.postOrThrow("v2/tasks", request)
    }

    override suspend fun updateTask(taskId: String, request: TodoistTaskUpdateRequest): TodoistTask {
        return httpClient.postOrThrow("v2/tasks/$taskId", request)
    }

    override suspend fun closeTask(task: TodoistTask) {
        val response = httpClient.post("v2/tasks/${task.id}/close")
        if (!response.status.isSuccess()) {
            throw ClientRequestException(response, response.bodyAsText())
        }
    }

    override suspend fun reopenTask(task: TodoistTask) {
        val response = httpClient.post("v2/tasks/${task.id}/reopen")
        if (!response.status.isSuccess()) {
            throw ClientRequestException(response, response.bodyAsText())
        }
    }

    override suspend fun deleteTask(taskId: String) {
        return httpClient.deleteOrThrow("v2/tasks/$taskId")
    }

    // endregion

    // region Comments

    override suspend fun getProjectComments(projectId: String): List<TodoistComment> {
        return httpClient.getOrThrow("v2/comments", mapOf("project_id" to projectId))
    }

    override suspend fun getTaskComments(taskId: String): List<TodoistComment> {
        return httpClient.getOrThrow("v2/comments", mapOf("task_id" to taskId))
    }

    override suspend fun createComment(request: TodoistCommentCreationRequest): TodoistComment {
        return httpClient.postOrThrow("v2/comments", request)
    }

    override suspend fun getComment(commentId: String): TodoistComment {
        return httpClient.getOrThrow("v2/comments/$commentId")
    }

    override suspend fun updateComment(commentId: String, request: TodoistCommentUpdateRequest): TodoistComment {
        return httpClient.postOrThrow("v2/comments/$commentId", request)
    }

    override suspend fun deleteComment(commentId: String) {
        return httpClient.deleteOrThrow("v2/comments/$commentId")
    }

    // endregion

    // region Labels

    override suspend fun getLabels(): List<TodoistLabel> {
        return httpClient.getOrThrow("v2/labels")
    }

    override suspend fun createLabel(request: TodoistLabelCreationRequest): TodoistLabel {
        return httpClient.postOrThrow("v2/labels", request)
    }

    override suspend fun getLabel(labelId: String): TodoistLabel {
        return httpClient.getOrThrow("v2/labels/$labelId")
    }

    override suspend fun updateLabel(labelId: String, request: TodoistLabelUpdateRequest): TodoistLabel {
        return httpClient.postOrThrow("v2/labels/$labelId", request)
    }

    override suspend fun deleteLabel(labelId: String) {
        return httpClient.deleteOrThrow("v2/labels/$labelId")
    }

    // endregion

    // region Shared Labels

    override suspend fun getSharedLabels(omitPersonal: Boolean): List<String> {
        return httpClient.getOrThrow("v2/labels/shared", mapOf("omit_personal" to omitPersonal))
    }

    override suspend fun renameSharedLabel(request: TodoistSharedLabelRenameRequest) {
        return httpClient.postOrThrow("v2/labels/shared/rename", request)
    }

    override suspend fun removeSharedLabel(request: TodoistSharedLabelRemovalRequest) {
        return httpClient.postOrThrow("v2/labels/shared/remove", request)
    }

    // endregion
}