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

import net.opatry.todoist.entity.TodoistProject

val projectData = listOf(
    EntityTestParam.build(
        """{
            "id": "220474322",
            "name": "Inbox",
            "comment_count": 10,
            "order": 0,
            "color": "grey",
            "is_shared": false,
            "is_favorite": false,
            "is_inbox_project": true,
            "is_team_inbox": false,
            "view_style": "list",
            "url": "https://todoist.com/showProject?id=220474322",
            "parent_id": null
        }""".trimIndent(),
        TodoistProject(
            id = "220474322",
            name = "Inbox",
            commentCount = 10,
            order = 0,
            color = "grey",
            isShared = false,
            isFavorite = false,
            isInboxProject = true,
            isTeamInbox = false,
            viewStyle = TodoistProject.ViewStyle.List,
            url = "https://todoist.com/showProject?id=220474322",
            parentId = null,
        )
    ),
)
