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
 * Represents a Todoist task.
 *
 * @property id Task ID.
 * @property projectId Task's project ID (read-only).
 * @property sectionId ID of section task belongs to (read-only, will be `null` when the task has no parent section).
 * @property content Task content. This value may contain markdown-formatted text and hyperlinks. Details on markdown support can be found in the [Text Formatting article](https://todoist.com/help/articles/how-to-format-text-e5dHw9) in the Help Center.
 * @property description A description for the task. This value may contain markdown-formatted text and hyperlinks. Details on markdown support can be found in the [Text Formatting article](https://todoist.com/help/articles/how-to-format-text-e5dHw9) in the Help Center.
 * @property isCompleted Flag to mark completed tasks.
 * @property labels The task's labels (a list of names that may represent either personal or shared labels).
 * @property parentId ID of parent task (read-only, will be `null` for top-level tasks).
 * @property order Position under the same parent or project for top-level tasks (read-only).
 * @property priority Task priority from 1 (normal, default value) to 4 (urgent).
 * @property due object representing task due date/time, or `null` if no date is set.
 * @property url URL to access this task in the Todoist web or mobile applications (read-only).
 * @property commentCount Number of task comments (read-only).
 * @property createdAt The date when the task was created (read-only).
 * @property creatorId The ID of the user who created the task (read-only).
 * @property assigneeId The responsible user ID (will be `null` if the task is unassigned).
 * @property assignerId The ID of the user who assigned the task (read-only, will be `null` if the task is unassigned).
 * @property duration A task's duration. The object will be `null` if the task has no duration.
 */
data class TodoistTask(

    @SerializedName("id")
    override val id: String,

    @SerializedName("project_id")
    val projectId: String,

    @SerializedName("section_id")
    val sectionId: String? = null,

    @SerializedName("content")
    val content: String,

    @SerializedName("description")
    val description: String,

    @SerializedName("is_completed")
    val isCompleted: Boolean,

    @SerializedName("labels")
    val labels: List<String>,

    @SerializedName("parent_id")
    override val parentId: String? = null,

    @SerializedName("order")
    override val order: Int,

    @SerializedName("priority")
    val priority: Int,

    @SerializedName("due")
    val due: DueDate? = null,

    @SerializedName("url")
    val url: String,

    @SerializedName("comment_count")
    val commentCount: Int,

    @SerializedName("created_at")
    val createdAt: String,

    @SerializedName("creator_id")
    val creatorId: String,

    @SerializedName("assignee_id")
    val assigneeId: String? = null,

    @SerializedName("assigner_id")
    val assignerId: String? = null,

    @SerializedName("duration")
    val duration: Duration? = null,
) : EntityInHierarchy {

    init {
        require(priority in 1..4) { "priority must be in 1..4 range" }
    }

    /**
     * Represents a Todoist task's due date.
     *
     * @property string Human defined date in arbitrary format.
     * @property date Date in format `YYYY-MM-DD` corrected to user's timezone.
     * @property isRecurring Whether the task has a [recurring due date](https://todoist.com/help/articles/set-a-recurring-due-date-YUYVJJAV).
     * @property datetime Only returned if exact due time set (i.e. it's not a whole-day task), date and time in [RFC3339](https://www.ietf.org/rfc/rfc3339.txt) format in UTC.
     * @property timezone Only returned if exact due time set, user's timezone definition either in tzdata-compatible format ("Europe/Berlin") or as a string specifying east of UTC offset as "UTC±HH:MM" (i.e. "UTC-01:00").*
     */
    data class DueDate(

        @SerializedName("string")
        val string: String,

        @SerializedName("date")
        val date: String,

        @SerializedName("is_recurring")
        val isRecurring: Boolean,

        @SerializedName("datetime")
        val datetime: String? = null,

        @SerializedName("timezone")
        val timezone: String? = null,
    )

    /**
     * An object representing a task duration.
     *
     * @property amount A positive integer (greater than zero).
     * @property unit
     */
    data class Duration(

        @SerializedName("amount")
        val amount: Int,

        @SerializedName("unit")
        val unit: Unit,

        ) {
        init {
            require(amount > 0) { "duration must be positive" }
        }

        enum class Unit {
            @SerializedName("minute")
            Minute,

            @SerializedName("day")
            Day,
        }
    }
}

