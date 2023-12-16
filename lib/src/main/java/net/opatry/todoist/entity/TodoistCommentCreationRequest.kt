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

import com.google.gson.annotations.SerializedName

/**
 * Represents a Todoist new comment request.
 *
 * @property taskId Comment's task ID (for task comments).
 * @property projectId Comment's project ID (for project comments).
 * @property content New content for the comment. This value may contain markdown-formatted text and hyperlinks. Details on markdown support can be found in the [Text Formatting article](https://todoist.com/help/articles/how-to-format-text-e5dHw9) in the Help Center.
 * @property attachment Object for attachment object.
 */
data class TodoistCommentCreationRequest(

    @SerializedName("task_id")
    val taskId: String? = null,

    @SerializedName("project_id")
    val projectId: String? = null,

    @SerializedName("content")
    val content: String,

    @SerializedName("attachment")
    val attachment: TodoistAttachment? = null,
) {
    companion object {
        @Suppress("FunctionName")
        @JvmStatic
        fun Project(projectId: String, content: String, attachment: TodoistAttachment? = null): TodoistCommentCreationRequest {
            return TodoistCommentCreationRequest(projectId = projectId, content = content, attachment = attachment)
        }

        @Suppress("FunctionName")
        @JvmStatic
        fun Task(taskId: String, content: String, attachment: TodoistAttachment? = null): TodoistCommentCreationRequest {
            return TodoistCommentCreationRequest(taskId = taskId, content = content, attachment = attachment)
        }
    }
}