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
 * Represents a Todoist update task request.
 *
 * @property content Task content. This value may contain markdown-formatted text and hyperlinks. Details on markdown support can be found in the [Text Formatting article](https://todoist.com/help/articles/how-to-format-text-e5dHw9) in the Help Center.
 * @property description A description for the task. This value may contain markdown-formatted text and hyperlinks. Details on markdown support can be found in the [Text Formatting article](https://todoist.com/help/articles/how-to-format-text-e5dHw9) in the Help Center.
 * @property projectId Task project ID. If not set, task is put to user's Inbox.
 * @property sectionId ID of section to put task into.
 * @property parentId Parent task ID.
 * @property order Non-zero Int value used by clients to sort tasks under the same parent.
 * @property labels The task's labels (a list of names that may represent either personal or shared labels).
 * @property priority Task priority from 1 (normal) to 4 (urgent).
 * @property dueString [Human defined](https://todoist.com/help/articles/introduction-to-due-dates-and-due-times-q7VobO) task due date (ex.: "next Monday", "Tomorrow"). Value is set using local (not UTC) time.
 * @property dueDate Specific date in `YYYY-MM-DD` format relative to user’s timezone.
 * @property dueDatetime Specific date and time in [RFC3339](https://www.ietf.org/rfc/rfc3339.txt) format in UTC.
 * @property dueLang 2-letter code specifying language in case [dueString] is not written in English.
 * @property assigneeId The responsible user ID (only applies to shared tasks).
 * @property duration A positive (greater than zero) integer for the amount of [durationUnit] the task will take. If specified, you must define a [durationUnit].
 * @property durationUnit The unit of time that the [duration] field above represents. Must be either `minute` or `day`. If specified, [duration] must be defined as well.
 *
 * Please note that only one of the `due*` fields can be used at the same time ([dueLang] is a special case).
 *
 * Also note that to remove the due date of a task completely, you should set the [dueString] parameter to `no date` or `no due date`.
 */
data class TodoistTaskUpdateRequest(

    @SerializedName("content")
    val content: String? = null,

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("labels")
    val labels: List<String>? = null,

    @SerializedName("priority")
    val priority: Int? = null,

    @SerializedName("due_string")
    val dueString: String? = null,

    @SerializedName("due_date")
    val dueDate: String? = null,

    @SerializedName("due_datetime")
    val dueDatetime: String? = null,

    @SerializedName("due_lang")
    val dueLang: String? = null,

    @SerializedName("assignee_id")
    val assigneeId: String? = null,

    @SerializedName("duration")
    val duration: Int? = null,

    @SerializedName("duration_unit")
    val durationUnit: String? = null,
)
