package com.joron.waffle.drivehistory.infrastructure.repository

object TrackRepository {

    var recordingTrackUuid = ""
    val recording: Boolean
        get() = recordingTrackUuid.isNotEmpty()

}