package com.joron.waffle.drivehistory.util

import java.util.UUID

object UuidUtil {
    fun generateUuid(): String {
        return UUID.randomUUID().toString().replace("-", "")
    }
}