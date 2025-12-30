package com.joron.waffle.drivehistory.infrastructure.repository

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.joron.waffle.drivehistory.domain.type.TrackStatus
import com.joron.waffle.drivehistory.infrastructure.model.database.TrackEntity

@Dao
interface Dao {

    @Query(
        """
        SELECT * FROM ${TrackEntity.TABLE_NAME_TRACK}
            ORDER BY ${TrackEntity.COLUMN_NAME_CREATED_TIME} ASC
    """
    )
    suspend fun queryTrackList(): List<TrackEntity>

    @Query(
        """
        SELECT * FROM ${TrackEntity.TABLE_NAME_TRACK}
            WHERE ${TrackEntity.COLUMN_NAME_TRACK_UUID} = :trackUuid
    """
    )
    suspend fun queryTrackList(trackUuid: String): List<TrackEntity>

    @Query(
        """
        SELECT * FROM ${TrackEntity.TABLE_NAME_TRACK}
            WHERE ${TrackEntity.COLUMN_NAME_STATUS} = :status
    """
    )
    suspend fun queryTrackListByStatus(status: Int): List<TrackEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertTrackList(entityList: List<TrackEntity>)

    @Query(
        """
        UPDATE ${TrackEntity.TABLE_NAME_TRACK} SET ${TrackEntity.COLUMN_NAME_STATUS} = ${TrackStatus.VALUE_INITIALIZE}
    """
    )
    suspend fun initTrackStatus()

    @Query(
        """
            UPDATE ${TrackEntity.TABLE_NAME_TRACK} SET ${TrackEntity.COLUMN_NAME_STATUS} = :status
                WHERE ${TrackEntity.COLUMN_NAME_TRACK_UUID} = :trackUuid
        """
    )
    suspend fun updateTrackStatus(trackUuid: String, status: Int)
}