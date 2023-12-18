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

package net.opatry.todoist.sync.entity

import com.google.gson.annotations.SerializedName

/**
 * Availability of comments functionality is dependent on the current user plan. This value is indicated by the `comments` property of the [user plan limits](https://developer.todoist.com/sync/v9/#user-plan-limits) object.
 *
 * @property id The ID of the note.
 * @property postedUid The ID of the user that posted the note.
 * @property projectId The project which the note is part of.
 * @property content The content of the note. This value may contain markdown-formatted text and hyperlinks. Details on markdown support can be found in the [Text Formatting article](https://todoist.com/help/articles/how-to-format-text-e5dHw9) in the Help Center.
 * @property fileAttachment A file attached to the note (see the [File Attachments](https://developer.todoist.com/sync/v9/#file-attachments) section for details).
 * @property uidsToNotify A list of user IDs to notify.
 * @property isDeleted Whether the note is marked as deleted.
 * @property postedAt The date when the note was posted.
 * @property reactions List of emoji reactions and corresponding user IDs.
 */
data class TodoistSyncProjectNotes(

    // FIXME what is nullable?

    @SerializedName("id")
    val id: String,

    @SerializedName("posted_uid")
    val postedUid: Int,

    @SerializedName("project_id")
    val projectId: Int,

    @SerializedName("content")
    val content: String,

    @SerializedName("file_attachment")
    val fileAttachment: Any, // FIXME type

    @SerializedName("uids_to_notify")
    val uidsToNotify: List<String>,

    @SerializedName("is_deleted")
    val isDeleted: Boolean,

    @SerializedName("posted_at")
    val postedAt: String,

    @SerializedName("reactions")
    val reactions: Any?, // FIXME type Map<String, List<String>> where key is the reaction (aka emoji) and value the list of user IDs reacting with it
)
