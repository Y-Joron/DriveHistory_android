package com.joron.waffle.drivehistory.domain.viewmodel

import android.content.Context
import android.location.Location
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joron.waffle.drivehistory.domain.LocationUsecase
import com.joron.waffle.drivehistory.domain.TrackUsecase
import com.joron.waffle.drivehistory.domain.model.TrackItem
import com.joron.waffle.drivehistory.util.DateTimeHelper
import com.joron.waffle.drivehistory.util.UuidUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LibraryViewModel : ViewModel(), LifecycleEventObserver {

    val trackUsecase = TrackUsecase()
    val locationUsecase = LocationUsecase()

    var trackList = MutableLiveData<List<TrackItem>>(emptyList())

    fun load(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val tList = trackUsecase.queryTrackList(context)
            withContext(Dispatchers.Main) {
                trackList.value = tList
            }
        }
    }

    fun createTrack(
        context: Context,
        title: String,
        onCreated: ((String) -> Unit)? = null,
    ) {
        Log.d(TAG, "enter createTrack title = $title")
        viewModelScope.launch(Dispatchers.IO) {
            val trackUuid = UuidUtil.generateUuid()
            val createdTime = DateTimeHelper.getEpochMillis()
            trackUsecase.upsertTrackItem(
                context,
                TrackItem(
                    trackUuid = trackUuid,
                    title = title,
                    createdTime = createdTime,
                ),
            )
            withContext(Dispatchers.Main) {
                onCreated?.invoke(trackUuid)
            }
        }
    }

    fun notifyUpdateLocation(location: Location) {
        locationUsecase.notifyUpdateLocation(location)
    }

    override fun onStateChanged(
        source: LifecycleOwner,
        event: Lifecycle.Event
    ) {
        Log.d(TAG, "onStateChanged event = $event")
        when (event) {
            Lifecycle.Event.ON_CREATE -> {

            }

            Lifecycle.Event.ON_DESTROY -> {

            }

            else -> {

            }
        }
    }

    companion object {
        const val TAG = "LibraryViewModel"
    }
}