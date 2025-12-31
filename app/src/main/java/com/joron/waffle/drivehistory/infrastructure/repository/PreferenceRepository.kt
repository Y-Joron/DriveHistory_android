package com.joron.waffle.drivehistory.infrastructure.repository

import android.content.Context
import android.util.Log

object PreferenceRepository {

    private val TAG = PreferenceRepository::class.java.name
    private const val NAME_PREF_LOCATION = "com.joron.waffle.drivehistory.pref_location"
    private const val KEY_RECORDING_TRACK_UUID = "recording_track_uuid"
    private const val KEY_LOCATIONS = "locations"

    fun getRecordingTrackUuid(context: Context): String {
        val preference = context.getSharedPreferences(
            NAME_PREF_LOCATION,
            Context.MODE_PRIVATE,
        )
        return preference.getString(KEY_RECORDING_TRACK_UUID, "") ?: ""
    }

    fun setRecordingTrackUuid(context: Context, uuid: String) {
        val preference = context.getSharedPreferences(
            NAME_PREF_LOCATION,
            Context.MODE_PRIVATE,
        )
        preference.edit().apply {
            putString(KEY_RECORDING_TRACK_UUID, uuid)
            apply()
        }
    }

    fun clearRecordingTrackUuid(context: Context) {
        Log.d(TAG, "enter clearRecordingTrackUuid")
        val preference = context.getSharedPreferences(
            NAME_PREF_LOCATION,
            Context.MODE_PRIVATE,
        )
        preference.edit().apply {
            remove(KEY_RECORDING_TRACK_UUID)
            apply()
        }
    }

    fun getLocations(context: Context): String {
        val preference = context.getSharedPreferences(
            NAME_PREF_LOCATION,
            Context.MODE_PRIVATE,
        )
        return preference.getString(KEY_LOCATIONS, "") ?: ""
    }

    fun saveLocations(context: Context, locations: String) {
        val preference = context.getSharedPreferences(
            NAME_PREF_LOCATION,
            Context.MODE_PRIVATE,
        )
        preference.edit().apply {
            putString(KEY_LOCATIONS, locations)
            apply()
        }
    }

    fun clearLocations(context: Context) {
        Log.d(TAG, "enter clearLocations")
        val preference = context.getSharedPreferences(
            NAME_PREF_LOCATION,
            Context.MODE_PRIVATE,
        )
        preference.edit().apply {
            remove(KEY_LOCATIONS)
            apply()
        }
    }
}
