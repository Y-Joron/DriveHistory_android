package com.joron.waffle.drivehistory.presentation

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.joron.waffle.drivehistory.databinding.MainActivityBinding
import com.joron.waffle.drivehistory.domain.viewmodel.MainViewModel
import com.joron.waffle.drivehistory.infrastructure.service.DriveLocationListener
import com.joron.waffle.drivehistory.infrastructure.service.LocationService
import com.joron.waffle.drivehistory.util.KEY_TRACK_UUID

class MainActivity : AppCompatActivity() {

    private lateinit var binding: MainActivityBinding
    private var service: LocationService? = null
    private var isBoundService = false
    private var mainViewModel = MainViewModel()

    private val connection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName?, service: IBinder) {
            Log.d(TAG, "onServiceConnected className:$className")
            this@MainActivity.service =
                (service as LocationService.LocationServiceBinder).service
            this@MainActivity.service?.setLocationListener(object : DriveLocationListener {
                override fun onUpdatedLocation(latitude: Double, longitude: Double) {
                    Log.d(TAG, "onUpdatedLocation latitude = $latitude, longitude = $longitude")
                    mainViewModel.notifyUpdateLocation(latitude, longitude)
                }
            })
        }

        override fun onServiceDisconnected(className: ComponentName?) {
            Log.d(TAG, "onServiceDisconnected className:$className")
            service?.setLocationListener(null)
            service = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        applySafeArea(binding.root)

        if (mainViewModel.isRecording()) {
            // 記録中(位置情報サービス起動中)であれば、バインドする
            bindLocationService()
        }
    }

    private fun applySafeArea(root: View) {
        ViewCompat.setOnApplyWindowInsetsListener(root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            root.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom,
            )
            insets
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Edge to Edge のステータスバー透明化を無効化
            getWindow().setStatusBarContrastEnforced(true)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
        unbindLocationService()
    }

    fun startLocationService(trackUuid: String) {
        if (isBoundService) {
            Log.d(TAG, "It is already bound LocationService.")
            return
        }
        val intent = Intent(this, LocationService::class.java).apply {
            putExtra(KEY_TRACK_UUID, trackUuid)
        }
        startService(intent)
        bindLocationService()
    }

    fun stopLocationService() {
        unbindLocationService()
        val intent = Intent(this, LocationService::class.java)
        stopService(intent)
    }

    private fun bindLocationService() {
        val intent = Intent(this, LocationService::class.java)
        isBoundService = bindService(intent, connection, BIND_AUTO_CREATE)
    }

    private fun unbindLocationService() {
        unbindService(connection)
        isBoundService = false
    }

    companion object {
        const val TAG = "MainActivity"
    }
}