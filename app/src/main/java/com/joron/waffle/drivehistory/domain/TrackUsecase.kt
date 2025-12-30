package com.joron.waffle.drivehistory.domain

import android.content.Context
import com.google.android.gms.maps.model.LatLng
import com.joron.waffle.drivehistory.domain.model.TrackItem
import com.joron.waffle.drivehistory.domain.type.TrackStatus
import com.joron.waffle.drivehistory.infrastructure.model.database.TrackEntity
import com.joron.waffle.drivehistory.infrastructure.repository.TrackDbRepository
import com.joron.waffle.drivehistory.infrastructure.repository.TrackFileRepository
import com.joron.waffle.drivehistory.infrastructure.repository.TrackRepository
import com.joron.waffle.drivehistory.util.LocationHelper

class TrackUsecase {

    suspend fun queryTrackList(context: Context): List<TrackItem> {
        val entityList = TrackDbRepository.queryTrackList(context)
        // 軌跡情報は詰めない
        return entityList.map { TrackItem.fromEntity(it) }
    }

    suspend fun queryTrackItem(context: Context, trackUuid: String): TrackItem {
        val entityList = TrackDbRepository.queryTrackList(context, trackUuid)
        val locations = TrackFileRepository.readLocations(context, trackUuid)
        return entityList.map { TrackItem.fromEntity(it, locations) }
            .firstOrNull() ?: TrackItem()
    }

    suspend fun queryTrackListByStatus(context: Context, status: TrackStatus): List<TrackItem> {
        val entityList = TrackDbRepository.queryTrackListByStatus(context, status)
        // 軌跡情報は詰めない
        return entityList.map { TrackItem.fromEntity(it) }
    }

    suspend fun upsertTrackItem(context: Context, trackItem: TrackItem) {
        val entity = TrackEntity.fromItem(trackItem)
        TrackDbRepository.upsertTrackList(context, listOf(entity))
        TrackFileRepository.writeLocations(
            context,
            trackItem.trackUuid,
            LocationHelper.toLocationStr(trackItem.locationList),
        )
    }

    fun updateTrackLocation(
        context: Context,
        trackUuid: String,
        locationList: List<LatLng>,
    ) {
        TrackFileRepository.writeLocations(
            context,
            trackUuid,
            LocationHelper.toLocationStr(locationList),
        )
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