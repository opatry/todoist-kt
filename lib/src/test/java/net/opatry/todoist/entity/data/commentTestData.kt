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


package net.opatry.todoist.entity.data

import net.opatry.todoist.entity.TodoistAttachment
import net.opatry.todoist.entity.TodoistComment

val commentData = listOf(
    EntityTestParam.build(
        """{
            "content": "Need one bottle of milk",
            "id": "2992679862",
            "posted_at": "2016-09-22T07:00:00.000000Z",
            "project_id": null,
            "task_id": "2995104339",
            "attachment": {
                "file_name": "File.pdf",
                "file_type": "application/pdf",
                "file_url": "https://cdn-domain.tld/path/to/file.pdf",
                "resource_type": "file"
            }
        }""".trimIndent(),
        TodoistComment(
            content = "Need one bottle of milk",
            id = "2992679862",
            postedAt = "2016-09-22T07:00:00.000000Z",
            projectId = null,
            taskId = "2995104339",
            attachment = TodoistAttachment(
                fileName = "File.pdf",
                fileType = "application/pdf",
                fileUrl = "https://cdn-domain.tld/path/to/file.pdf",
                resourceType = "file"
            )
        )
    )
)
