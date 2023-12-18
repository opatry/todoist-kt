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

package net.opatry.todoist.sync

import com.google.gson.annotations.SerializedName
import net.opatry.todoist.sync.entity.TodoistSyncProject
import java.util.*

/**
 * Represents a Todoist sync command.
 *
 * ## Command UUID
 * Clients should generate a unique string ID for each command and specify it in the `uuid` field. The Command UUID will be used for two purposes:
 * 1. Command result mapping: Each command's result will be stored in the `sync_status` field of the response JSON object. The `sync_status` object has its key mapped to a command's `uuid` and its value containing the result of a command.
 * 2. Command idempotency: Todoist will not execute a command that has same UUID as a previously executed command. This will allow clients to safely retry each command without accidentally performing the action twice.
 *
 * ## Temporary resource id
 * Some commands depend on the result of previous command. For instance, you have a command sequence: `"project_add"` and `"item_add"` which first creates a project and then add a new task to the newly created project. In order to run the later `item_add` command, we need to obtain the project ID returned from the previous command. Therefore, the normal approach would be to run these two commands in two separate HTTP requests.
 *
 * The temporary resource ID feature allows you to run two or more dependent commands in a single HTTP request. For commands that are related to creation of resources (i.e. `item_add`, `project_add`), you can specify an extra [tempId] as a placeholder for the actual ID of the resource. The other commands in the same sequence could directly refer to [tempId] if needed.
 *
 * @property type The type of the command.
 * @property args The parameters of the command.
 * @property uuid Command UUID.
 * @property tempId Temporary resource ID, Optional. Only specified for commands that create a new resource (e.g. `item_add` command).
 */
data class TodoistSyncCommand(

    @SerializedName("type")
    val type: String,

    @SerializedName("args")
    val args: Any,

    @SerializedName("uuid")
    val uuid: String,

    @SerializedName("temp_id")
    val tempId: String? = null,
) {
    companion object {

        /**
         * Add a new project.
         *
         * @param name The name of the project.
         * @param color The color of the project icon. Refer to the `name` column in the [Colors](https://developer.todoist.com/guides/#colors) guide for more info.
         * @param parentId The ID of the parent project. Set to null for root projects
         * @param childOrder The order of the project. Defines the position of the project among all the projects with the same [TodoistSyncProject.parentId]
         * @param isFavorite Whether the project is a favorite.
         * @param viewStyle This determines the way the project is displayed within the Todoist clients.
         */
        @Suppress("FunctionName")
        @JvmStatic
        fun AddProject(
            name: String,
            color: String? = null,
            parentId: String? = null,
            childOrder: Int? = null,
            isFavorite: Boolean? = null,
            viewStyle: TodoistSyncProject.ViewStyle? = null,
        ): TodoistSyncCommand {
            return TodoistSyncCommand(
                type = "project_add",
                args = buildMap<String, Any> {
                    put("name", name)
                    color?.let { put("color", color) }
                    parentId?.let { put("parent_id", parentId) }
                    childOrder?.let { put("child_order", childOrder) }
                    isFavorite?.let { put("is_favorite", isFavorite) }
                    viewStyle?.let { put("view_style", viewStyle) }
                },
                uuid = UUID.randomUUID().toString(),
                tempId = UUID.randomUUID().toString(),
            )
        }

        /**
         * Update an existing project.
         *
         * @param id The ID of the project (could be temp id).
         * @param name The name of the project.
         * @param color The color of the project icon. Refer to the `name` column in the [Colors](https://developer.todoist.com/guides/#colors) guide for more info.
         * @param isCollapsed Whether the project's sub-projects are collapsed.
         * @param isFavorite Whether the project is a favorite.
         * @param viewStyle This determines the way the project is displayed within the Todoist clients.
         */
        @Suppress("FunctionName")
        @JvmStatic
        fun UpdateProject(
            id: String,
            name: String? = null,
            color: String? = null,
            isCollapsed: Boolean? = null,
            isFavorite: Boolean? = null,
            viewStyle: TodoistSyncProject.ViewStyle? = null,
        ): TodoistSyncCommand {
            return TodoistSyncCommand(
                type = "project_update",
                args = buildMap<String, Any> {
                    put("id", id)
                    name?.let { put("name", name) }
                    color?.let { put("color", color) }
                    isCollapsed?.let { put("collapsed", isCollapsed) }
                    isFavorite?.let { put("is_favorite", isFavorite) }
                    viewStyle?.let { put("view_style", viewStyle) }
                },
                uuid = UUID.randomUUID().toString(),
            )
        }

        /**
         * Update parent project relationships of the project.
         *
         * @param id The ID of the project (could be temp id).
         * @param parentId The ID of the parent project (could be temp id). If set to `null`, the project will be moved to the root
         */
        @Suppress("FunctionName")
        @JvmStatic
        fun MoveProject(
            id: String,
            parentId: String? = null,
        ): TodoistSyncCommand {
            return TodoistSyncCommand(
                type = "project_move",
                args = buildMap {
                    put("id", id)
                    put("parent_id", parentId)
                },
                uuid = UUID.randomUUID().toString(),
            )
        }

        /**
         * Delete an existing project and all its descendants.
         *
         * @param id ID of the project to delete (could be a temp id).
         */
        @Suppress("FunctionName")
        @JvmStatic
        fun DeleteProject(
            id: String,
        ): TodoistSyncCommand {
            return TodoistSyncCommand(
                type = "project_delete",
                args = buildMap {
                    put("id", id)
                },
                uuid = UUID.randomUUID().toString(),
            )
        }

        /**
         * Archive a project and its descendants.
         *
         * @param id ID of the project to archive (could be a temp id).
         */
        @Suppress("FunctionName")
        @JvmStatic
        fun ArchiveProject(
            id: String,
        ): TodoistSyncCommand {
            return TodoistSyncCommand(
                type = "project_archive",
                args = buildMap {
                    put("id", id)
                },
                uuid = UUID.randomUUID().toString(),
            )
        }

        /**
         * Unarchive a project. No ancestors will be unarchived along with the unarchived project. Instead, the project is unarchived alone, loses any parent relationship (becomes a root project), and is placed at the end of the list of other root projects.
         *
         * @param id ID of the project to unarchive (could be a temp id).
         */
        @Suppress("FunctionName")
        @JvmStatic
        fun UnarchiveProject(
            id: String,
        ): TodoistSyncCommand {
            return TodoistSyncCommand(
                type = "project_unarchive",
                args = buildMap {
                    put("id", id)
                },
                uuid = UUID.randomUUID().toString(),
            )
        }

        /**
         * @property id ID of the project to update.
         * @property childOrder the new project order.
         */
        data class ProjectOrder(

            @SerializedName("id")
            val id: String,

            @SerializedName("child_order")
            val childOrder: Int,
        )

        /**
         * The command updates [TodoistSyncProject.childOrder] properties of projects in bulk.
         *
         * @param projects An array of objects to update.
         *
         * @see ProjectOrder
         */
        @Suppress("FunctionName")
        @JvmStatic
        fun ReorderProjects(
            projects: List<ProjectOrder>,
        ): TodoistSyncCommand {
            return TodoistSyncCommand(
                type = "project_reorder",
                args = buildMap {
                    put("projects", projects)
                },
                uuid = UUID.randomUUID().toString(),
            )
        }
    }
}
