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

/**
 * https://developer.todoist.com/guides/#colors
 *
 * ```
 * ID	Name		Hexadecimal		ID	Name		Hexadecimal
 * 30	berry_red	#b8256f			40	light_blue	#96c3eb
 * 31	red			#db4035			41	blue		#4073ff
 * 32	orange		#ff9933			42	grape		#884dff
 * 33	yellow		#fad000			43	violet		#af38eb
 * 34	olive_green	#afb83b			44	lavender	#eb96eb
 * 35	lime_green	#7ecc49			45	magenta		#e05194
 * 36	green		#299438			46	salmon		#ff8d85
 * 37	mint_green	#6accbc			47	charcoal	#808080
 * 38	teal		#158fad			48	grey		#b8b8b8
 * 39	sky_blue	#14aaf5			49	taupe		#ccac93
 * ```
 */
fun Todoist.color(colorName: String): Long {
    return when (colorName) {
        "berry_red" -> 0xff_b8_25_6f
        "light_blue" -> 0xff_96_c3_eb
        "red" -> 0xff_db_40_35
        "blue" -> 0xff_40_73_ff
        "orange" -> 0xff_ff_99_33
        "grape" -> 0xff_88_4d_ff
        "yellow" -> 0xff_fa_d0_00
        "violet" -> 0xff_af_38_eb
        "olive_green" -> 0xff_af_b8_3b
        "lavender" -> 0xff_eb_96_eb
        "lime_green" -> 0xff_7e_cc_49
        "magenta" -> 0xff_e0_51_94
        "green" -> 0xff_29_94_38
        "salmon" -> 0xff_ff_8d_85
        "mint_green" -> 0xff_6a_cc_bc
        "charcoal" -> 0xff_80_80_80
        "teal" -> 0xff_15_8f_ad
        "grey" -> 0xff_b8_b8_b8
        "sky_blue" -> 0xff_14_aa_f5
        "taupe" -> 0xff_cc_ac_93
        else -> 0x00_00_00_00
    }
}