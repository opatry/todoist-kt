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

import net.opatry.todoist.entity.TodoistTask

val taskData = listOf(
    EntityTestParam.build(
        """{
            "creator_id": "2671355",
            "created_at": "2019-12-11T22:36:50.000000Z",
            "assignee_id": "2671362",
            "assigner_id": "2671355",
            "comment_count": 10,
            "is_completed": false,
            "content": "Buy Milk",
            "description": "",
            "due": {
                "date": "2016-09-01",
                "is_recurring": false,
                "datetime": "2016-09-01T12:00:00.000000Z",
                "string": "tomorrow at 12",
                "timezone": "Europe/Moscow"
            },
            "duration": null,
            "id": "2995104339",
            "labels": ["Food", "Shopping"],
            "order": 1,
            "priority": 1,
            "project_id": "2203306141",
            "section_id": "7025",
            "parent_id": "2995104589",
            "url": "https://todoist.com/showTask?id=2995104339"
        }""".trimIndent(),
        TodoistTask(
            creatorId = "2671355",
            createdAt = "2019-12-11T22:36:50.000000Z",
            assigneeId = "2671362",
            assignerId = "2671355",
            commentCount = 10,
            isCompleted = false,
            content = "Buy Milk",
            description = "",
            due = TodoistTask.DueDate(
                date = "2016-09-01",
                isRecurring = false,
                datetime = "2016-09-01T12:00:00.000000Z",
                string = "tomorrow at 12",
                timezone = "Europe/Moscow"
            ),
            duration = null,
            id = "2995104339",
            labels = listOf("Food", "Shopping"),
            order = 1,
            priority = 1,
            projectId = "2203306141",
            sectionId = "7025",
            parentId = "2995104589",
            url = "https://todoist.com/showTask?id=2995104339"
        )
    ),
)
