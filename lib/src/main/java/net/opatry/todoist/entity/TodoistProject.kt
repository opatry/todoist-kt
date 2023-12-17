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
 * Represents a Todoist project.
 *
 * @property id Project ID.
 * @property name Project name.
 * @property color The color of the project icon. Refer to the `name` column in the [Colors](https://developer.todoist.com/guides/#colors) guide for more info.
 * @property parentId ID of parent project (will be `null` for top-level projects).
 * @property order Project position under the same parent (read-only, will be `0` for inbox and team inbox projects).
 * @property commentCount Number of project comments.
 * @property isShared Whether the project is shared.
 * @property isFavorite Whether the project is a favorite.
 * @property isInboxProject Whether the project is the `Team Inbox` (read-only).
 * @property isTeamInbox Whether the project is the user's `Inbox` (read-only).
 * @property viewStyle This determines the way the project is displayed within the Todoist clients.
 * @property url URL to access this project in the Todoist web or mobile applications.
 */
data class TodoistProject(

    @SerializedName("id")
    override val id: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("color")
    val color: String,

    @SerializedName("parent_id")
    override val parentId: String? = null,

    @SerializedName("order")
    override val order: Int,

    @SerializedName("comment_count")
    val commentCount: Int,

    @SerializedName("is_shared")
    val isShared: Boolean,

    @SerializedName("is_favorite")
    val isFavorite: Boolean,

    @SerializedName("is_inbox_project")
    val isInboxProject: Boolean,

    @SerializedName("is_team_inbox")
    val isTeamInbox: Boolean,

    @SerializedName("view_style")
    val viewStyle: ViewStyle,

    @SerializedName("url")
    val url: String,
) : EntityInHierarchy {

    enum class ViewStyle {

        @SerializedName("list")
        List,

        @SerializedName("board")
        Board
    }
}
