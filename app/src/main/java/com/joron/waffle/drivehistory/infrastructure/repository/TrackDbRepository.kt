package com.joron.waffle.drivehistory.infrastructure.repository

import android.content.Context
import com.joron.waffle.drivehistory.domain.type.TrackStatus
import com.joron.waffle.drivehistory.infrastructure.datasource.database.DriveHistoryDatabase
import com.joron.waffle.drivehistory.infrastructure.model.database.TrackEntity

object TrackDbRepository {

    suspend fun queryTrackList(context: Context): List<TrackEntity> {
        val dao = DriveHistoryDatabase.getInstance(context).getDao()
        return dao.queryTrackList()
    }

    suspend fun queryTrackList(context: Context, trackUuid: String): List<TrackEntity> {
        val dao = DriveHistoryDatabase.getInstance(context).getDao()
        return dao.queryTrackList(trackUuid)
    }

    suspend fun queryTrackListByStatus(context: Context, status: TrackStatus): List<TrackEntity> {
        val dao = DriveHistoryDatabase.getInstance(context).getDao()
        return dao.queryTrackListByStatus(status.value)
    }

    suspend fun upsertTrackList(context: Context, trackList: List<TrackEntity>) {
        val dao = DriveHistoryDatabase.getInstance(context).getDao()
        dao.upsertTrackList(trackList)
    }

    suspend fun updateTrackLocation(context: Context, trackUuid: String, locations: String) {
        val dao = DriveHistoryDatabase.getInstance(context).getDao()
        dao.updateTrackLocation(trackUuid, locations)
    }

    suspend fun initTrackStatus(context: Context) {
        val dao = DriveHistoryDatabase.getInstance(context).getDao()
        dao.initTrackStatus()
    }

    suspend fun updateTrackStatus(context: Context, trackUuid: String, status: TrackStatus) {
        val dao = DriveHistoryDatabase.getInstance(context).getDao()
        dao.updateTrackStatus(trackUuid, status.value)
    }

}