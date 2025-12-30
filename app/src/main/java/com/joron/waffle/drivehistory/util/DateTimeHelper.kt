package com.joron.waffle.drivehistory.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateTimeHelper {

    @JvmStatic
    fun toDateTimeStr(epoch: Long): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        val date = Date(epoch)
        return sdf.format(date)
    }

    fun getEpochMillis(): Long {
        return System.currentTimeMillis()
    }

}