package com.joron.waffle.drivehistory.domain.model

import com.google.android.gms.maps.model.LatLng
import com.joron.waffle.drivehistory.domain.type.ActiveType
import com.joron.waffle.drivehistory.domain.type.TrackStatus
import com.joron.waffle.drivehistory.infrastructure.model.database.TrackEntity
import com.joron.waffle.drivehistory.util.LocationHelper

data class TrackItem(
    val trackUuid: String = "",
    val activeType: ActiveType = ActiveType.UNKNOWN,
    val status: TrackStatus = TrackStatus.INITIALIZE,
    val title: String = "",
    var locationList: List<LatLng> = emptyList(),
    val createdTime: Int = 0,
) {
    val valid: Boolean
        get() = trackUuid.isNotEmpty()

    companion object {
        fun fromEntity(entity: TrackEntity): TrackItem {
            val trackUuid = entity.trackUuid
            val activeType = ActiveType.valueOf(entity.activeType)
            val status = TrackStatus.valueOf(entity.status)
            val title = entity.title
            val locationList = LocationHelper.toLocationList(entity.locations)
            val createdTime = entity.createdTime
            return TrackItem(
                trackUuid,
                activeType,
                status,
                title,
                locationList,
                createdTime,
            )
        }
    }
}
