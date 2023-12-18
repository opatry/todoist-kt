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

package net.opatry.todoist.entity.sync

import com.google.gson.annotations.SerializedName

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
)
