package com.e15.alarmnats.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Icon
import android.os.Build
import com.e15.alarmnats.activity.TimerActivity
import com.e15.alarmnats.R
import com.e15.alarmnats.service.PomodoroService

//Setting icons of the Notification include start, pause, stop
class NotificationUtil


(private val mainContext: Context) {

    private val CHANNEL_ID = "TIMER_CHANNEL_ID"
    private val CHANNEL_NAME = "Timer"

    //Class to notify the user of events that happen.
    // This is how you tell the user that something has happened in the background.
    private val notificationManager: NotificationManager
    //(Builder class) for (Notification objects). Provides a convenient way
    // to set the various fields of a Notification and generate content views
    // using the platform's notification layout template
    private var builder: Notification.Builder? = null

    //Structure to (encapsulate a named action) that can be shown as part of this notification.
    // It must include an (icon), (a label), and (a PendingIntent)  to be fired when the action is selected by the user.
    //Icons of the notification
    private var startAction: Notification.Action? = null
    private var pauseAction: Notification.Action? = null
    private var stopAction: Notification.Action? = null

    init {
        notificationManager = mainContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        //START TIMER
        val startIntent = Intent(mainContext, TimerReceiver::class.java)
        startIntent.action = PomodoroService.ACTION_START
        val startPendingIntent =
                PendingIntent.getBroadcast(mainContext, 0, startIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        //PAUSE TIMER
        val pauseIntent = Intent(mainContext, TimerReceiver::class.java)
        pauseIntent.action = PomodoroService.ACTION_PAUSE
        val pausePendingIntent =
                PendingIntent.getBroadcast(mainContext, 0, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        //STOP TIMER
        val stopIntent = Intent(mainContext, TimerReceiver::class.java)
        stopIntent.action = PomodoroService.ACTION_STOP
        val stopPendingIntent =
                PendingIntent.getBroadcast(mainContext, 0, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val startIcon = Icon.createWithResource(mainContext, R.drawable.ic_play_arrow_24dp)
            val pauseIcon = Icon.createWithResource(mainContext, R.drawable.ic_pause_24dp)
            val stopIcon = Icon.createWithResource(mainContext, R.drawable.ic_stop_24dp)
            startAction = Notification.Action.Builder(startIcon, "START", startPendingIntent).build()
            pauseAction = Notification.Action.Builder(pauseIcon, "PAUSE", pausePendingIntent).build()
            stopAction = Notification.Action.Builder(stopIcon, "STOP", stopPendingIntent).build()
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
                startAction =
                        Notification.Action.Builder(R.drawable.ic_play_arrow_24dp, "START", startPendingIntent).build()
                pauseAction = Notification.Action.Builder(R.drawable.ic_pause_24dp, "PAUSE", pausePendingIntent).build()
                stopAction = Notification.Action.Builder(R.drawable.ic_stop_24dp, "STOP", stopPendingIntent).build()
            }
        }
    }

    /**
     * Construct the notification with necessary pending intents and actions
     * @param currentTime formatted time from to display
     * @param timerRunning current state of timer
     * @param timer current timer in loop
     */

    fun buildNotification(currentTime: String, timerRunning: Boolean, timer: String): Notification {

        //notification clicked
        val timerIntent = Intent(mainContext, TimerActivity::class.java)
        timerIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        val timerPendingIntent =
                PendingIntent.getActivity(mainContext, 0, timerIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        builder = Notification.Builder(mainContext)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder!!.setSmallIcon(R.drawable.ic_alarm_24dp)
                    .setAutoCancel(true)
                    .setContentIntent(timerPendingIntent)
                    .setContentTitle(timer)
                    .setContentText(currentTime)
                    .setOnlyAlertOnce(true)
                    .setCategory(Notification.CATEGORY_ALARM)
        }

        //Change available action options depending on state
        if (timerRunning) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
                builder!!.addAction(pauseAction)
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
                builder!!.addAction(startAction)
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            builder!!.addAction(stopAction)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                    NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
            notificationChannel.lightColor = Color.BLUE
            notificationManager.createNotificationChannel(notificationChannel)
            builder!!.setChannelId(CHANNEL_ID)
        }

        return builder!!.build()
    }

    /**
     * Updates the timer to reflect current state
     * @param time current time
     * @param timer current timer
     */

    fun updateNotification(time: String, timer: String) {
        builder!!.setContentTitle(timer)
        builder!!.setContentText(time)

        //Post a (notification) to be shown in the (status bar).
        // If a notification with the (same id) has already been posted
        // by your application and has not yet been canceled,
        // it will be replaced by the updated information.
        notificationManager.notify(NOTIFICATION_ID, builder!!.build())
    }

    companion object {

        val NOTIFICATION_ID = 1
    }


}