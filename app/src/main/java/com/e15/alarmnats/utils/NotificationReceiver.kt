package com.e15.alarmnats.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.e15.alarmnats.service.PomodoroService

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        if (intent.action != null) {

            if (intent.action == Intent.ACTION_SCREEN_OFF) {

                //If you turn of your screen
                val notificationIntent = Intent(context, PomodoroService::class.java)

                //(Sent request) by delivering (action==CREEN_OFF) to (class PomodoroService.kt)
                notificationIntent.action = PomodoroService.SCREEN_OFF

                context.startService(notificationIntent)

            }
        }
    }
}