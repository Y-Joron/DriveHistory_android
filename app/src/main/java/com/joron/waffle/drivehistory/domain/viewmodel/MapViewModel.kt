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

    var loading = MutableLiveData(false)
    var recording = MutableLiveData(false)

    // 軌跡全量
    var locationMap = MutableLiveData(mapOf<Int, MutableList<LocationItem>>())

    // 軌跡実線
    var locationMapSolid =
        MutableLiveData(mapOf<Int, MutableList<MutableList<LocationItem>>>())

    // 軌跡破線
    var locationMapBroken =
        MutableLiveData(mapOf<Int, MutableList<MutableList<LocationItem>>>())

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
        loading.value = true
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
                tItem.locationList += listOf(locationsPref)
            }
            withContext(Dispatchers.Main) {
                recording.value = tItem.trackUuid == recordingUuid
                trackItem = tItem
                var listResult = LocationListResult()
                tItem.locationList.forEachIndexed { sectionIndex, sectionList ->
                    sectionList.forEach {
                        listResult = createLineList(
                            sectionIndex,
                            it,
                            listResult,
                        )
                    }
                }
                this@MapViewModel.locationMap.value = listResult.locationMap
                this@MapViewModel.locationMapSolid.value = listResult.locationMapSolid
                this@MapViewModel.locationMapBroken.value = listResult.locationMapBroken
                loading.value = false
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
        var sectionIndex = lastSectionIndex()
        if (sectionIndex < 0) {
            // 軌跡Mapにkeyが存在しない場合は0としてMapを生成する (fail safe)
            sectionIndex = 0
        }
        val listResult = createLineList(
            sectionIndex,
            location,
            LocationListResult(
                locationMap.value ?: return,
                locationMapSolid.value ?: return,
                locationMapBroken.value ?: return,
            ),
        )
        locationMap.value = listResult.locationMap
        locationMapSolid.value = listResult.locationMapSolid
        locationMapBroken.value = listResult.locationMapBroken
        listResult.locationMap[sectionIndex]?.lastOrNull()?.let {
            accuracy.value = it.accuracy
            speed.value = it.speed
            speed2.value = it.speed * 3.6f
            altitude.value = it.altitude
        }
    }

    private fun createLineList(
        sectionIndex: Int,
        location: LocationItem,
        base: LocationListResult
    ): LocationListResult {
        val loMap = base.locationMap.toMutableMap()
        loMap.putIfAbsent(sectionIndex, mutableListOf())
        val loList = loMap[sectionIndex] ?: mutableListOf()
        val lastLocationItem = loList.lastOrNull()
        val loMapSolid = base.locationMapSolid.toMutableMap()
        loMapSolid.putIfAbsent(sectionIndex, mutableListOf())
        val loListSolid = loMapSolid[sectionIndex] ?: mutableListOf()
        val loCurrentListSolid = loListSolid.lastOrNull() ?: mutableListOf()
        val loMapBroken = base.locationMapBroken.toMutableMap()
        loMapBroken.putIfAbsent(sectionIndex, mutableListOf())
        val loListBroken = loMapBroken[sectionIndex] ?: mutableListOf()
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
            loMap,
            loMapSolid,
            loMapBroken,
        )
    }

    fun startRecording() {
        createListToMap()
        recording.value = true
    }

    fun stopRecording() {
        recording.value = false
    }

    private fun createListToMap() {
        val sectionIndex = lastSectionIndex()
        val loMap = locationMap.value?.toMutableMap() ?: return
        val loMapSolid = locationMapSolid.value?.toMutableMap() ?: return
        val loMapBroken = locationMapBroken.value?.toMutableMap() ?: return
        loMap.putIfAbsent(sectionIndex + 1, mutableListOf())
        loMapSolid.putIfAbsent(sectionIndex + 1, mutableListOf())
        loMapBroken.putIfAbsent(sectionIndex + 1, mutableListOf())
        locationMap.value = loMap
        locationMapSolid.value = loMapSolid
        locationMapBroken.value = loMapBroken
    }

    /**
     * 軌跡Mapの最後のキー(Index)を取得する
     * キーが1件も存在しない場合は-1を返す
     */
    private fun lastSectionIndex(): Int {
        return locationMap.value?.toSortedMap()?.keys?.lastOrNull() ?: -1
    }

    private data class LocationListResult(
        val locationMap: Map<Int, MutableList<LocationItem>> = emptyMap(),
        val locationMapSolid: Map<Int, MutableList<MutableList<LocationItem>>> = emptyMap(),
        val locationMapBroken: Map<Int, MutableList<MutableList<LocationItem>>> = emptyMap(),
    )

    companion object {
        const val TAG = "MapViewModel"
    }

}