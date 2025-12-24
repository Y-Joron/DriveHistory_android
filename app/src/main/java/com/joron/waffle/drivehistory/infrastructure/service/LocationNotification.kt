package com.joron.waffle.drivehistory.infrastructure.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.joron.waffle.drivehistory.R
import com.joron.waffle.drivehistory.presentation.MainActivity

class LocationNotification {

    private lateinit var notification: Notification

    fun create(context: Context): Notification {
        val manager = getNotificationManager(context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, context.getString(R.string.channel_name),
                NotificationManager.IMPORTANCE_MAX
            ).apply {
                description = context.getString(R.string.channel_description)
            }
            manager.createNotificationChannel(channel)
        }
        notification = buildStampSpotNone(context)
        return notification
    }

    private fun buildStampSpotNone(context: Context): Notification {
        return NotificationCompat.Builder(context, CHANNEL_ID).apply {
            setContentIntent(getAppLaunchIntent(context))
            setContentText(context.getString(R.string.no_registrable_stamp_spot))
            priority = NotificationCompat.PRIORITY_MAX
        }.build()
    }

    private fun getNotificationManager(context: Context) =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    /**
     * アプリを起動するPendingIntentを取得する
     */
    private fun getAppLaunchIntent(context: Context): PendingIntent {
        return PendingIntent.getActivity(
            context,
            PENDING_CODE_LAUNCH_APP,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    companion object {
        const val CHANNEL_ID = "location_notification"
        private const val PENDING_CODE_LAUNCH_APP = 1001
    }

}