package com.joron.waffle.drivehistory.infrastructure.service

import android.Manifest
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.joron.waffle.drivehistory.domain.LocationUsecase
import com.joron.waffle.drivehistory.domain.TrackUsecase
import com.joron.waffle.drivehistory.domain.model.LocationItem
import com.joron.waffle.drivehistory.util.KEY_TRACK_UUID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LocationService : Service() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var request: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationNotification: LocationNotification
    private val locationUsecase = LocationUsecase()
    private val trackUsecase = TrackUsecase()
    private var locationListener: DriveLocationListener? = null

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    inner class LocationServiceBinder : Binder() {
        val service: LocationService
            get() = this@LocationService
    }

    private val binder: IBinder = LocationServiceBinder()

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate() threadName = ${Thread.currentThread().name} ")
        ALIVE = true
        createLocationApi()
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {
        super.onStartCommand(intent, flags, startId)
        Log.d(TAG, "onStartCommand() threadName = ${Thread.currentThread().name} ")
        applyIntent(intent)
        locationNotification = LocationNotification()
        val notification = locationNotification.create(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(NOTIFICATION_ID, notification, FOREGROUND_SERVICE_TYPE_LOCATION)
        }
        startLocationUpdate()
        return START_STICKY
    }

    private fun applyIntent(intent: Intent?) {
        Log.d(TAG, "enter applyIntent")
        intent?.apply {
            val trackUuid = getStringExtra(KEY_TRACK_UUID) ?: ""
            Log.d(TAG, "applyIntent trackUuid = $trackUuid")
            if (trackUuid.isNotEmpty()) {
                trackUsecase.setRecordingTrackUuid(
                    this@LocationService,
                    trackUuid,
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy() threadName = ${Thread.currentThread().name} ")
        stopLocationUpdate()
        stopRecording()
        ALIVE = false
    }

    fun stopRecording() {
        serviceScope.launch(Dispatchers.IO) {
            // DBからのと、preferenceからのを結合して、軌跡情報を保存する
            val track = trackUsecase.queryTrackItem(
                this@LocationService,
                trackUsecase.getRecordingTrackUuid(this@LocationService)
            )
            val locationsPref = locationUsecase.getLocationPref(this@LocationService)
            Log.d(TAG, "track = $track, locationsPref = $locationsPref")
            val loList = if (track.locationList.none { it.isNotEmpty() })
            // 初回の記録の場合
                listOf(locationsPref)
            else
                track.locationList + listOf(locationsPref)
            trackUsecase.updateTrackLocation(
                this@LocationService,
                track.trackUuid,
                loList,
            )

            withContext(Dispatchers.Main) {
                // 記録停止状態 + Preferenceの軌跡情報消去
                trackUsecase.clearRecordingTrackUuid(this@LocationService)
                locationUsecase.clearLocationsPref(this@LocationService)
                serviceScope.cancel()
            }
        }
    }

    override fun onBind(p0: Intent?): IBinder? {
        return binder
    }

    private fun createLocationApi() {
        request = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            3000,
        ).apply {
            setMinUpdateIntervalMillis(3000)
            setMinUpdateDistanceMeters(5f)
        }.build()
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                locationResult.lastLocation?.let { location ->
                    Log.d(
                        TAG,
                        "threadName = ${Thread.currentThread().name} lat = ${location.latitude}, lng = ${location.longitude}"
                    )
                    val locationItem = LocationItem.fromLocation(location)
                    locationUsecase.addLocationsPref(
                        this@LocationService,
                        locationItem,
                    )
                    locationListener?.onUpdatedLocation(locationItem)
                }
            }
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    /**
     * 位置情報更新処理スタート
     */
    private fun startLocationUpdate() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.requestLocationUpdates(
            request,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    /**
     * 位置情報更新処理ストップ
     */
    private fun stopLocationUpdate() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    fun setLocationListener(listener: DriveLocationListener?) {
        locationListener = listener
    }

    companion object {
        private const val TAG = "LocationService"
        const val NOTIFICATION_ID = 101
        var ALIVE = false
    }
}