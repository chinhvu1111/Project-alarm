package com.e15.alarmnats.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.e15.alarmnats.Database.ReminderDatabase
import com.e15.alarmnats.R
import java.util.*

//Class Broadcast handle (the boot action events)
class BootReceiver : BroadcastReceiver() {

    //Handling ring
    private var alarmReceiver: AlarmReceiver? = null
    private var hashId: String=""
    var requestCode:Int=0
    private var year: Int = 0
    private var month: Int = 0
    private var day: Int = 0
    private var startHour: Int = 0
    private var startMinute: Int = 0
    private var endHour:Int = 0;
    private var endMinute:Int = 0;
    private var repeatTime: Long = 0
    private var repeatCount: String? = null
    private var startTime: Array<String>? = null
    private var endTime: Array<String>? = null
    private var date: Array<String>? = null


    override fun onReceive(context: Context, intent: Intent) {

        if (intent.action == "android.intent.action.BOOT_COMPLETED") {

            val dbHandler = ReminderDatabase(context)

            alarmReceiver = AlarmReceiver()

            //Get (all items0 from (database)
            val allevents = dbHandler.allEvents

            for (i in allevents.indices) {

                val item = allevents.get(i)
                hashId = item.hashId
                requestCode=item.requestCode
                //get hour and minute
                startTime = item.startTime!!.trim().split(":") as Array<String>
                startHour = Integer.parseInt(startTime!![0])
                startMinute = Integer.parseInt(startTime!![1])

                endTime=item.endTime!!.trim().split(":")as Array<String>
                endHour=Integer.parseInt(endTime!![0])
                endMinute=Integer.parseInt(endTime!![1])

                //get year,month,day
                date = item.date!!.trim().split("/") as Array<String>
                month = Integer.parseInt(date!![0])
                day = Integer.parseInt(date!![1])
                year = Integer.parseInt(date!![2])

                repeatCount = item.repeatCount

                if (item.repeatType.equals(context.resources.getString(R.string.Min))) {

                    repeatTime = Integer.parseInt(repeatCount!!) * milMinute

                } else if (item.repeatType.equals(context.resources.getString(R.string.Hour))) {

                    repeatTime = Integer.parseInt(repeatCount!!) * milHour

                }

                //Mode on/off notify and repeat
                if (item.notify.equals("on")) {

                    if (item.repeatMode.equals("on")) {

                        callBroadcastReceiver(requestCode, REPEAT_MODE_ON, context,hashId)

                    } else {

                        callBroadcastReceiver(requestCode, REPEAT_MODE_OFF, context,hashId)

                    }
                }
            }
        }
    }

    //Set up ring alarm base on (repeatTime)
    fun callBroadcastReceiver(row: Int, mode: Int, context: Context,hashId:String) {

        val startCalendar = Calendar.getInstance()
        startCalendar.set(Calendar.YEAR, year)
        startCalendar.set(Calendar.MONTH, month)
        startCalendar.set(Calendar.DAY_OF_MONTH, day)
        startCalendar.set(Calendar.HOUR_OF_DAY, startHour)
        startCalendar.set(Calendar.MINUTE, startMinute)
        startCalendar.set(Calendar.SECOND, 0)

        val endCalendar = Calendar.getInstance()
        endCalendar.set(Calendar.YEAR, year)
        endCalendar.set(Calendar.MONTH, month)
        endCalendar.set(Calendar.DAY_OF_MONTH, day)
        endCalendar.set(Calendar.HOUR_OF_DAY, endHour)
        endCalendar.set(Calendar.MINUTE, endMinute)
        endCalendar.set(Calendar.SECOND, 0)

        when (mode) {

            //Adding parameter repeatTime
            REPEAT_MODE_ON ->

                alarmReceiver!!.setRepeatAlarm(context, startCalendar, endCalendar, row, repeatTime,hashId)
            REPEAT_MODE_OFF ->

                alarmReceiver!!.setAlarm(context, startCalendar,endCalendar, row,hashId)

        }
    }

    companion object {

        private val REPEAT_MODE_ON = 0
        private val REPEAT_MODE_OFF = 1
        private val milMinute = 60000L
        private val milHour = 3600000L
    }
}