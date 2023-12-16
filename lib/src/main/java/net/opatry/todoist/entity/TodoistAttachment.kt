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
 * @property resourceType
 * @property fileName
 * @property fileSize
 * @property fileType
 * @property fileUrl
 * @property fileDuration
 * @property uploadState
 * @property image
 * @property imageWidth
 * @property imageHeight
 * @property url
 * @property title
 */
data class TodoistAttachment(

    @SerializedName("resource_type")
    val resourceType: String,

    @SerializedName("file_name")
    val fileName: String? = null,

    @SerializedName("file_size")
    val fileSize: Int? = null,

    @SerializedName("file_type")
    val fileType: String? = null,

    @SerializedName("file_url")
    val fileUrl: String? = null,

    @SerializedName("file_duration")
    val fileDuration: Int? = null,

    @SerializedName("upload_state")
    val uploadState: UploadState? = null,

    @SerializedName("image")
    val image: String? = null,

    @SerializedName("image_width")
    val imageWidth: Int? = null,

    @SerializedName("image_height")
    val imageHeight: Int? = null,

    @SerializedName("url")
    val url: String? = null,

    @SerializedName("title")
    val title: String? = null,
) {
    enum class UploadState {

        @SerializedName("pending")
        Pending,

        @SerializedName("completed")
        Completed
    }
}
