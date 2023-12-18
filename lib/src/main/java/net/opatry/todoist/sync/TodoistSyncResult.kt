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

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import net.opatry.todoist.sync.entity.TodoistSyncProject

/**
 * @property projects
 * @property fullSync
 * @property tempIdMapping
 * @property syncStatus
 * @property syncToken
 */
data class TodoistSyncResult(

    // FIXME what is nullable?

    @SerializedName("projects")
    val projects: List<TodoistSyncProject>,

    @SerializedName("full_sync")
    val fullSync: Boolean,

    @SerializedName("temp_id_mapping")
    val tempIdMapping: Map<String, String>, // temporary ID (like UUID) to Entity id

    @SerializedName("sync_status")
    val syncStatus: Map<String, Any>? = null,
//    val syncStatus: Map<String, TodoistSyncStatus>? = null,

    @SerializedName("sync_token")
    val syncToken: String,
) {
    companion object {
        // FIXME should be handled by proper/custom Gson serializer
        @JvmStatic
        fun mapSyncStatus(status: Any): TodoistSyncStatus {
            require(status == "ok" || status is Map<*, *>) { """Status must be a "ok" string or an error object map""" }
            return when (status) {
                "ok" -> TodoistSyncStatus.Ok
                // TODO could check it's a valid Json string content
                else -> with(Gson()) {
                    val jsonTree = toJsonTree(status as Map<*, *>)
                    fromJson(jsonTree, TodoistSyncStatus.Error::class.java)
                }
            }
        }
    }
}

/**
 * Represents a Todoist sync request result.
 *
 * @property syncToken A new synchronization token. Used by the client in the next sync request to perform an incremental sync.
 * @property fullSync Whether the response contains all data (a full synchronization) or just the incremental updates since the last sync.
 * @property user A user object.
 * @property projects An array of [project](https://developer.todoist.com/sync/v9/#projects) objects.
 * @property items An array of [item](https://developer.todoist.com/sync/v9/#items) objects.
 * @property notes An array of [item note](https://developer.todoist.com/sync/v9/#item-notes) objects.
 * @property projectNotes An array of [project note](https://developer.todoist.com/sync/v9/#project-notes) objects.
 * @property sections An array of [section](https://developer.todoist.com/sync/v9/#sections) objects.
 * @property labels An array of [personal label](https://developer.todoist.com/sync/v9/#labels) objects.
 * @property filters An array of [filter](https://developer.todoist.com/sync/v9/#filters) objects.
 * @property dayOrders A JSON object specifying the order of items in daily agenda.
 * @property reminders An array of [reminder](https://developer.todoist.com/sync/v9/#reminders) objects.
 * @property collaborators A JSON object containing all [collaborators](https://developer.todoist.com/sync/v9/#collaborators) for all shared projects. The [projects] field contains the list of all shared projects, where the user acts as one of collaborators.
 * @property collaboratorsStates An array specifying the state of each collaborator in each project. The state can be invited, active, inactive, deleted.
 * @property completedInfo An array of [completed info](https://developer.todoist.com/sync/v9/#completed-info) objects indicating the number of completed items within an active project, section, or parent item. Projects will also include the number of archived sections. Endpoints for accessing archived objects are described in the [Items and sections archive](https://developer.todoist.com/sync/v9/#items-and-sections-archive) section.
 * @property liveNotifications An array of `live_notification` objects.
 * @property liveNotificationsLastRead What is the last live notification the user has seen? This is used to implement unread notifications.
 * @property userSettings A JSON object containing [user settings](https://developer.todoist.com/sync/v9/#user-settings).
 * @property userPlanLimits A JSON object containing [user plan limits](https://developer.todoist.com/sync/v9/#user-plan-limits).
 */
data class TodoistSyncResult2(
    @SerializedName("sync_token")
    val syncToken: String,
    @SerializedName("full_sync")
    val fullSync: String,
    @SerializedName("user")
    val user: String,
    @SerializedName("projects")
    val projects: String,
    @SerializedName("items")
    val items: String,
    @SerializedName("notes")
    val notes: String,
    @SerializedName("project_notes")
    val projectNotes: String,
    @SerializedName("sections")
    val sections: String,
    @SerializedName("labels")
    val labels: String,
    @SerializedName("filters")
    val filters: String,
    @SerializedName("day_orders")
    val dayOrders: String,
    @SerializedName("reminders")
    val reminders: String,
    @SerializedName("collaborators")
    val collaborators: String,
    @SerializedName("collaborators_states")
    val collaboratorsStates: String,
    @SerializedName("completed_info")
    val completedInfo: String,
    @SerializedName("live_notifications")
    val liveNotifications: String,
    @SerializedName("live_notifications_last_read")
    val liveNotificationsLastRead: String,
    @SerializedName("user_settings")
    val userSettings: String,
    @SerializedName("user_plan_limits")
    val userPlanLimits: String,
)
