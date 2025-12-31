package com.joron.waffle.drivehistory.domain.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joron.waffle.drivehistory.domain.LocationEventListener
import com.joron.waffle.drivehistory.domain.LocationUsecase
import com.joron.waffle.drivehistory.domain.TrackUsecase
import com.joron.waffle.drivehistory.domain.model.LocationItem
import com.joron.waffle.drivehistory.domain.model.TrackItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MapViewModel : ViewModel(), LifecycleEventObserver {

    val trackUsecase = TrackUsecase()
    val locationUsecase = LocationUsecase()

    var recording = MutableLiveData(false)

    // 軌跡全量
    var locationList = MutableLiveData(emptyList<LocationItem>())

    // 軌跡実線
    var locationListSolid = MutableLiveData(emptyList<MutableList<LocationItem>>())

    // 軌跡破線
    var locationListBroken = MutableLiveData(emptyList<MutableList<LocationItem>>())

    var accuracy = MutableLiveData(0f)
    var speed = MutableLiveData(0f)
    var speed2 = MutableLiveData(0f)
    var altitude = MutableLiveData(0.0)

    var trackItem = TrackItem()
    val locationEventListener = LocationEventListener(
        onUpdateLocation = { onUpdateLocation(it) }
    )

    fun load(context: Context, trackUuid: String) {
        Log.d(TAG, "enter load")
        viewModelScope.launch(Dispatchers.IO) {
            val tItem = trackUsecase.queryTrackItem(context, trackUuid)
            val recordingUuid = trackUsecase.getRecordingTrackUuid(context)
            Log.d(
                TAG,
                "load currentTrackUuid = ${tItem.trackUuid}" +
                        " recordingUuid = $recordingUuid"
            )
            if (tItem.trackUuid == recordingUuid) {
                // 記録中の場合はPreferenceからの情報を追加する
                val locationsPref = locationUsecase.getLocationPref(context)
                tItem.locationList += locationsPref
            }
            withContext(Dispatchers.Main) {
                recording.value = tItem.trackUuid == recordingUuid
                trackItem = tItem
                var listResult = LocationListResult(
                    locationList.value ?: emptyList(),
                    locationListSolid.value ?: emptyList(),
                    locationListBroken.value ?: emptyList(),
                )
                tItem.locationList.forEach {
                    listResult = createLineList(
                        it,
                        listResult,
                    )
                }
                locationList.value = listResult.locationList
                locationListSolid.value = listResult.locationListSolid
                locationListBroken.value = listResult.locationListBroken
            }
        }
    }

    override fun onStateChanged(
        source: LifecycleOwner,
        event: Lifecycle.Event,
    ) {
        Log.d(TAG, "onStateChanged event = $event")
        when (event) {
            Lifecycle.Event.ON_CREATE -> {
                locationUsecase.addLocationEventListener(TAG, locationEventListener)
            }

            Lifecycle.Event.ON_DESTROY -> {
                locationUsecase.removeLocationEventListener(TAG)
            }

            else -> {}
        }
    }

    private fun onUpdateLocation(location: LocationItem) {
        val tmpRecording = recording.value ?: return
        if (!tmpRecording) {
            return
        }
        Log.d(
            TAG,
            "onUpdateLocation latitude = ${location.latitude}, longitude = ${location.longitude}"
        )
        val listResult = createLineList(
            location,
            LocationListResult(
                locationList.value ?: return,
                locationListSolid.value ?: return,
                locationListBroken.value ?: return,
            )
        )
        locationList.value = listResult.locationList
        locationListSolid.value = listResult.locationListSolid
        locationListBroken.value = listResult.locationListBroken
        listResult.locationList.lastOrNull()?.let {
            accuracy.value = it.accuracy
            speed.value = it.speed
            speed2.value = it.speed * 3.6f
            altitude.value = it.altitude
        }
    }

    private fun createLineList(
        location: LocationItem,
        base: LocationListResult
    ): LocationListResult {
        val loList = base.locationList.toMutableList()
        val lastLocationItem = loList.lastOrNull()
        val loListSolid = base.locationListSolid.toMutableList()
        val loCurrentListSolid = loListSolid.lastOrNull() ?: mutableListOf()
        val loListBroken = base.locationListBroken.toMutableList()
        val loCurrentListBroken = loListBroken.lastOrNull() ?: mutableListOf()
        loList += location
        if (location.accurate) {
            // 位置情報正確
            if (lastLocationItem == null) {
                // 初回追加時は実線リスト新規生成
                val loListNew = mutableListOf(location)
                loListSolid.add(loListNew)
            } else if (!lastLocationItem.accurate) {
                val loListNew = mutableListOf(location)
                loListSolid.add(loListNew)
                // 前回の位置情報が不正確な場合に、破線の終端として追加
                loCurrentListBroken.add(location)
            } else {
                // 前回も正確である場合は素直に位置情報を実線リストへ追加するのみ
                loCurrentListSolid.add(location)
            }
        } else {
            // 位置情報不正確
            if (lastLocationItem?.accurate == true) {
                // 前回の位置情報が正確な場合に、破線の始点として追加
                val loListNew = mutableListOf(lastLocationItem)
                loListBroken.add(loListNew)
            }
        }
        // 新しい軌跡情報返却
        return LocationListResult(
            loList,
            loListSolid,
            loListBroken,
        )
    }

    fun startRecording() {
        recording.value = true
    }

    fun stopRecording() {
        recording.value = false
    }

    private data class LocationListResult(
        val locationList: List<LocationItem> = emptyList(),
        val locationListSolid: List<MutableList<LocationItem>> = emptyList(),
        val locationListBroken: List<MutableList<LocationItem>> = emptyList(),
    )

    companion object {
        const val TAG = "MapViewModel"
    }

}