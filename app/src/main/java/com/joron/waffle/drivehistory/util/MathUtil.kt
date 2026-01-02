package com.joron.waffle.drivehistory.util

import kotlin.math.floor

fun Double.floor(decimal: Int): Double = floor(this * decimal) / decimal
fun Float.floor(decimal: Int): Float = floor(this * decimal) / decimal
fun Long.floor(digit: Int): Long = (floor(this.toDouble() / digit) * digit).toLong()