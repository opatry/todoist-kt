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
 * Represents a Todoist new section request.
 *
 * @property name Name of the project.
 * @property parentId Parent project ID.
 * @property color The color of the project icon. Refer to the `name` column in the [Colors](https://developer.todoist.com/guides/#colors) guide for more info.
 * @property isFavorite Whether the project is a favorite.
 * @property viewStyle This determines the way the project is displayed within the Todoist clients.
 */
data class TodoistProjectCreationRequest(

    @SerializedName("name")
    val name: String,

    @SerializedName("parent_id")
    val parentId: String? = null,

    @SerializedName("color")
    val color: String? = null,

    @SerializedName("is_favorite")
    val isFavorite: Boolean? = null,

    @SerializedName("view_style")
    val viewStyle: TodoistProject.ViewStyle? = null,
)