package com.santiagobattaglino.mvvm.codebase.service

import com.santiagobattaglino.mvvm.codebase.data.room.Database
import com.santiagobattaglino.mvvm.codebase.domain.entity.Notification
import com.santiagobattaglino.mvvm.codebase.presentation.ui.splash.SplashActivity
import com.santiagobattaglino.mvvm.codebase.util.Arguments
import com.santiagobattaglino.mvvm.codebase.util.Constants
import com.santiagobattaglino.mvvm.codebase.util.getCurrentTime
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.res.ResourcesCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.room.Room
import com.google.firebase.messaging.FirebaseMessagingService
import com.santiagobattaglino.mvvm.codebase.BuildConfig
import com.santiagobattaglino.mvvm.codebase.R
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class AppFirebaseMessagingService : FirebaseMessagingService() {

    var notificationId = 0

    companion object {
        private const val TAG = "FCM"
        const val REQUEST_ACCEPT = "REQUEST_ACCEPT"
    }

    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")
        sendRegistrationToServer(token)
    }

    private fun sendRegistrationToServer(token: String?) {
        Log.d(TAG, "sendRegistrationTokenToServer($token)")
    }


    private fun sendNotification(title: String?, body: String?, incidentId: String?) {
        val intent = Intent(this, SplashActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra(Arguments.ARG_INCIDENT_ID, incidentId?.toInt())
        val pendingIntent = PendingIntent.getActivity(
            this, 999, intent,
            PendingIntent.FLAG_ONE_SHOT
        )

        val channelId = if (BuildConfig.DEBUG) {
            Constants.DEV_NOTIFICATION_CHANNEL_ID
        } else {
            Constants.DEFAULT_NOTIFICATION_CHANNEL_ID
        }

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setColor(ResourcesCompat.getColor(resources, R.color.colorBackground, null))
            .setContentTitle(title)
            .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
            .setContentText(body)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Incident Messages",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(notificationId, notificationBuilder.build())
        notificationId++

        val db = Room.databaseBuilder(applicationContext, Database::class.java, Constants.DB_NAME)
            .fallbackToDestructiveMigration()
            .build()

        GlobalScope.launch {
            val notification = Notification(
                at = getCurrentTime(),
                title = title,
                body = body,
                incidentId = incidentId
            )
            db.notificationDAO().save(notification)

            val broadcaster = LocalBroadcastManager.getInstance(applicationContext)
            val intentRequest = Intent(REQUEST_ACCEPT)
            broadcaster.sendBroadcast(intentRequest)
        }
    }

    override fun handleIntent(intent: Intent?) {
        super.handleIntent(intent)
        removeFirebaseOrigianlNotificaitons()

        intent?.let {

            if (it.extras == null)
                return

            Log.d(TAG, "handleIntent(${it.extras})")
            val title = it.extras?.get("gcm.notification.title") as String?
            val body = it.extras?.get("gcm.notification.body") as String?
            val incidentId = it.extras?.get("incidentId") as String?

            sendNotification(
                title,
                body,
                incidentId
            )
        }
    }

    private fun removeFirebaseOrigianlNotificaitons() {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return
        }
        val activeNotifications = notificationManager.activeNotifications ?: return
        for (tmp in activeNotifications) {
            Log.d(
                "FCM",
                "StatusBarNotification tag/id: " + tmp.tag + " / " + tmp.id
            )
            val tag = tmp.tag
            val id = tmp.id
            if (tag != null && tag.contains("FCM-Notification")) notificationManager.cancel(tag, id)
        }
    }
}