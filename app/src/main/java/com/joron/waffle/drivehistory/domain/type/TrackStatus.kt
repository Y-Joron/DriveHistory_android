package com.joron.waffle.drivehistory.domain.type

enum class TrackStatus(val value: Int) {
    INITIALIZE(0),
    PLAY(1),
    ;

    companion object {
        fun valueOf(value: Int): TrackStatus {
            return TrackStatus.entries.find { it.value == value } ?: INITIALIZE
        }

        const val VALUE_INITIALIZE = 0
    }
}