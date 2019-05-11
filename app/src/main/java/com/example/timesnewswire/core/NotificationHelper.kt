package com.example.timesnewswire.core

import android.annotation.TargetApi
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.timesnewswire.R

class NotificationHelper(context: Context): ContextWrapper(context) {
    companion object {
        var notifyId: Int = 0
            get() {
                return field++
            }
            set(value) {
                field = if (value + 1 == Int.MAX_VALUE) 1 else value
            }
    }

    private val tnwChannelID = "New York Times Articles"
    private val tnwChannelName = "New York Times Articles check"
    private val tnwChannelDescription = "Check for new 'New York Times' published articles."
    private var tnwChannelImportance = NotificationManager.IMPORTANCE_DEFAULT

    val mManager: NotificationManager by lazy { getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            createNotificationChannels()
        else {
            //For Android 7.1 and lower...
        }
    }

    /**
     * Notification channel appear in Android 8.0
     */
    @TargetApi(Build.VERSION_CODES.O)
    private fun createNotificationChannels() {
        val tnwArticleChannel = NotificationChannel(tnwChannelID, tnwChannelName, tnwChannelImportance)
        tnwArticleChannel.description = tnwChannelDescription
        mManager.createNotificationChannel(tnwArticleChannel)
    }

    fun getChannelNotification(contentTitle: String, contentText: String): NotificationCompat.Builder {
        return NotificationCompat.Builder(applicationContext, tnwChannelID)
            .setAutoCancel(true)
            .setSmallIcon(R.mipmap.notification_icon)
            .setContentTitle(contentTitle)
            .setContentText(contentText)
    }

    fun getChannelNotification(contentTitle: String, contentText: String, contentIntent: PendingIntent): NotificationCompat.Builder {
        return getChannelNotification(contentTitle, contentText)
            .setContentIntent(contentIntent)
    }
}