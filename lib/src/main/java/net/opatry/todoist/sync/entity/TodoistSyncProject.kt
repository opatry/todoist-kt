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
 * Represents a Todoist sync project.
 *
 * @property id The ID of the project.
 * @property name The name of the project.
 * @property color The color of the project icon. Refer to the `name` column in the [Colors](https://developer.todoist.com/guides/#colors) guide for more info.
 * @property parentId The ID of the parent project. Set to `null` for root projects.
 * @property childOrder The order of the project. Defines the position of the project among all the projects with the same [parentId]
 * @property isCollapsed Whether the project's sub-projects are collapsed.
 * @property isShared Whether the project is shared.
 * @property isDeleted Whether the project is marked as deleted.
 * @property isArchived Whether the project is marked as archived.
 * @property isFavorite Whether the project is a favorite.
 * @property syncId Identifier to find the match between different copies of shared projects. When you share a project, its copy has a different ID for your collaborators. To find a project in a different account that matches yours, you can use the "sync_id" attribute. For non-shared projects the attribute is set to `null`.
 * @property isInboxProject Whether the project is `Inbox` (`true` or otherwise this property is not sent).
 * @property isTeamInbox Whether the project is `TeamInbox` (`true` or otherwise this property is not sent).
 * @property viewStyle This determines the way the project is displayed within the Todoist clients.
 */
data class TodoistSyncProject(

    @SerializedName("id")
    val id: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("color")
    val color: String,

    @SerializedName("parent_id")
    val parentId: String? = null,

    @SerializedName("child_order")
    val childOrder: Int,

    @SerializedName("collapsed")
    val isCollapsed: Boolean,

    @SerializedName("shared")
    val isShared: Boolean,

    @SerializedName("is_deleted")
    val isDeleted: Boolean,

    @SerializedName("is_archived")
    val isArchived: Boolean,

    @SerializedName("is_favorite")
    val isFavorite: Boolean,

    @SerializedName("sync_id")
    val syncId: String? = null,

    @SerializedName("inbox_project")
    val isInboxProject: Boolean? = null,

    @SerializedName("team_inbox")
    val isTeamInbox: Boolean? = null,

    @SerializedName("view_style")
    val viewStyle: ViewStyle,
) {

    enum class ViewStyle {

        @SerializedName("list")
        List,

        @SerializedName("board")
        Board
    }
}

