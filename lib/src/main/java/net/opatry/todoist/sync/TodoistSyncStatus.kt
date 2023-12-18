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

sealed class TodoistSyncStatus {
    data object Ok : TodoistSyncStatus()

    /**
     *  21 Project not found: The project has been deleted or does not exist (the command should not be retried).
     *  22 Item not found: The task has been deleted or does not exist (the command should not be retried).
     * 411 User deleted: The user account has been deleted (the command should not be retried).
     */
    data class Error(

        @SerializedName("error_code")
        val errorCode: Int,

        @SerializedName("error")
        val error: String,

        @SerializedName("error_tag")
        val errorTag: String? = null, // undocumented

        @SerializedName("http_code")
        val httpCode: Int? = null, // undocumented

        @SerializedName("error_extra")
        val errorExtra: Any? = null, // undocumented
    ) : TodoistSyncStatus()
}