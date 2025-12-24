package com.joron.waffle.drivehistory.domain.type

enum class ActiveType(val value: Int) {
    WALKING(0),
    RUNNING(1),
    CYCLING(2),
    DRIVING(3),
    UNKNOWN(-1),
    ;

    companion object {
        fun valueOf(value: Int): ActiveType {
            return ActiveType.entries.find { it.value == value } ?: UNKNOWN
        }
    }
}