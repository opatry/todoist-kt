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

import com.google.gson.Gson
import net.opatry.todoist.entity.data.EntityTestParam
import net.opatry.todoist.entity.data.collaboratorData
import net.opatry.todoist.entity.data.commentData
import net.opatry.todoist.entity.data.labelData
import net.opatry.todoist.entity.data.projectData
import net.opatry.todoist.entity.data.sectionData
import net.opatry.todoist.entity.data.taskData
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class EntityTest(private val param: EntityTestParam) {

    private val gson: Gson = Gson()

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{index}: {0}")
        fun data(): Iterable<EntityTestParam> {
            return buildList {
                addAll(collaboratorData)
                addAll(commentData)
                addAll(labelData)
                addAll(projectData)
                addAll(sectionData)
                addAll(taskData)
            }
        }
    }

    @Test
    fun `JSON payload properly mapped to Entity`() {
        val entity = gson.fromJson(param.jsonPayload, param.entityClass)
        assertEquals(param.expectedEntity, entity)
    }
}
