package com.joron.waffle.drivehistory.infrastructure.model.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.joron.waffle.drivehistory.domain.model.TrackItem
import com.joron.waffle.drivehistory.domain.type.ActiveType
import com.joron.waffle.drivehistory.domain.type.TrackStatus

@Entity(tableName = TrackEntity.TABLE_NAME_TRACK)
class TrackEntity {
    @PrimaryKey
    @ColumnInfo(name = COLUMN_NAME_TRACK_UUID)
    var trackUuid: String = ""

    @ColumnInfo(name = COLUMN_NAME_ACTIVE_TYPE)
    var activeType: Int = ActiveType.UNKNOWN.value

    @ColumnInfo(name = COLUMN_NAME_STATUS)
    var status: Int = TrackStatus.INITIALIZE.value

    @ColumnInfo(name = COLUMN_NAME_TITLE)
    var title: String = ""

    @ColumnInfo(name = COLUMN_NAME_CREATED_TIME)
    var createdTime: Int = 0

    companion object {
        const val TABLE_NAME_TRACK = "track"
        const val COLUMN_NAME_TRACK_UUID = "track_uuid"
        const val COLUMN_NAME_TITLE = "title"
        const val COLUMN_NAME_ACTIVE_TYPE = "active_type"
        const val COLUMN_NAME_STATUS = "status"
        const val COLUMN_NAME_CREATED_TIME = "created_time"

        fun fromItem(item: TrackItem): TrackEntity {
            return TrackEntity().apply {
                trackUuid = item.trackUuid
                activeType = item.activeType.value
                status = item.status.value
                title = item.title
                createdTime = item.createdTime
            }
        }
    }
}