package com.joron.waffle.drivehistory.infrastructure.repository

import android.content.Context
import android.util.Log

object PreferenceRepository {

    private val TAG = PreferenceRepository::class.java.name
    private const val NAME_PREF_LOCATION = "com.joron.waffle.drivehistory.pref_location"
    private const val KEY_LOCATIONS = "locations"

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
