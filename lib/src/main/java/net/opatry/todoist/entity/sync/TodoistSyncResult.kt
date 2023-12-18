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
 * @property projects
 * @property fullSync
 * @property tempIdMapping
 * @property syncStatus
 * @property syncToken
 */
data class TodoistSyncResult(
    @SerializedName("projects")
    val projects: List<TodoistProject>,
    @SerializedName("full_sync")
    val fullSync: Boolean,
    @SerializedName("temp_id_mapping")
    val tempIdMapping: Map<String, Int>, // temporary ID (like UUID) to Entity id
    @SerializedName("sync_status")
    val syncStatus: Map<String, String>, // request UUID to status label ("ok")
    @SerializedName("sync_token")
    val syncToken: String,
)
