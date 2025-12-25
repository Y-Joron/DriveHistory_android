package com.joron.waffle.drivehistory.domain

import android.content.Context
import com.google.android.gms.maps.model.LatLng
import com.joron.waffle.drivehistory.domain.model.TrackItem
import com.joron.waffle.drivehistory.domain.type.TrackStatus
import com.joron.waffle.drivehistory.infrastructure.model.database.TrackEntity
import com.joron.waffle.drivehistory.infrastructure.repository.TrackDbRepository
import com.joron.waffle.drivehistory.infrastructure.repository.TrackRepository
import com.joron.waffle.drivehistory.util.LocationHelper

class TrackUsecase {

    suspend fun queryTrackList(context: Context): List<TrackItem> {
        val entityList = TrackDbRepository.queryTrackList(context)
        return entityList.map { TrackItem.fromEntity(it) }
    }

    suspend fun queryTrackItem(context: Context, trackUuid: String): TrackItem {
        val entityList = TrackDbRepository.queryTrackList(context, trackUuid)
        return entityList.map { TrackItem.fromEntity(it) }
            .firstOrNull() ?: TrackItem()
    }

    suspend fun queryTrackListByStatus(context: Context, status: TrackStatus): List<TrackItem> {
        val entityList = TrackDbRepository.queryTrackListByStatus(context, status)
        return entityList.map { TrackItem.fromEntity(it) }
    }

    suspend fun upsertTrackItem(context: Context, trackItem: TrackItem) {
        val entity = TrackEntity.fromItem(trackItem)
        TrackDbRepository.upsertTrackList(context, listOf(entity))
    }

    suspend fun updateTrackLocation(
        context: Context,
        trackUuid: String,
        locationList: List<LatLng>
    ) {
        val locationStr = LocationHelper.toLocationStr(locationList)
        TrackDbRepository.updateTrackLocation(context, trackUuid, locationStr)
    }

    suspend fun initTrackStatus(context: Context) {
        TrackDbRepository.initTrackStatus(context)
    }

    suspend fun updateTrackStatus(context: Context, trackUuid: String, status: TrackStatus) {
        TrackDbRepository.updateTrackStatus(context, trackUuid, status)
    }

    fun getRecordingTrackUuid(): String {
        return TrackRepository.recordingTrackUuid
    }

    fun isRecording(): Boolean {
        return TrackRepository.recordingTrackUuid.isNotEmpty()
    }

    fun setRecordingTrackUuid(uuid: String) {
        TrackRepository.recordingTrackUuid = uuid
    }

}