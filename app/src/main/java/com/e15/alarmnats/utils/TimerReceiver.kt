package com.e15.alarmnats.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.e15.alarmnats.Model.Task
import com.e15.alarmnats.service.PomodoroService

//This class receives (broadcast) from (PomodoroFragment class)
class TimerReceiver : BroadcastReceiver() {

    /**
     * Interface with PomodoroService
     */

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != null) {

            when (intent.action) {

                PomodoroService.ACTION_PAUSE -> {

                    val pauseIntent = Intent(context, PomodoroService::class.java)

                    var task = Task(0);

                    task.fromIntent(intent);

                    task.toIntent(pauseIntent)

                    pauseIntent.action = PomodoroService.ACTION_PAUSE

                    context.startService(pauseIntent)
                }
                PomodoroService.ACTION_START -> {

                    val startIntent = Intent(context, PomodoroService::class.java)

                    startIntent.action = PomodoroService.ACTION_START

                    var task = Task(0);

                    task.fromIntent(intent);

                    task.toIntent(startIntent)

                    context.startService(startIntent)
                }
                PomodoroService.ACTION_RESET -> {

                    val resetIntent = Intent(context, PomodoroService::class.java)

                    resetIntent.action = PomodoroService.ACTION_RESET

                    //resetIntent.action = PomodoroService.ACTION_START

                    var task = Task(0);

                    task.fromIntent(intent);

                    task.toIntent(resetIntent)

                    context.startService(resetIntent)
                }
                PomodoroService.ACTION_STOP -> {

                    val stopIntent = Intent(context, PomodoroService::class.java)

                    stopIntent.action = PomodoroService.ACTION_STOP

                    var task = Task(0);

                    task.fromIntent(intent);

                    task.toIntent(stopIntent)

                    context.startService(stopIntent)
                }
            }
        }
    }
}
