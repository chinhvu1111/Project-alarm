package com.e15.alarmnats

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast

import com.e15.alarmnats.activity.AlarmFiredActivity

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        try {
            if (intent.action.equals("android.intent.action.BOOT_COMPLETED") || intent.action.equals("com.mine.alarm")) {

                //Display (AlarmFiredActivity class)
                ring(context, intent)

            }
        } catch (e: NullPointerException) {
            ring(context, intent)
        }

    }

    private fun ring(context: Context, intent: Intent) {
        val ringtone = intent.getStringExtra("ringtoneUri")
        val alarmTime = intent.getStringExtra("alarmTime")
        val question = intent.getStringExtra("question")
        val answer = intent.getStringExtra("answer")
        val label = intent.getStringExtra("label")
        //        System.out.println("my receiver label : " + label);

        Log.d("ringtone", ringtone!!)
        println("ringing here: " + alarmTime!!)

        Toast.makeText(context, "Alarm fired", Toast.LENGTH_LONG).show()
        Log.i("broadcast receiver", "Alarm Fired")

        val alarmFiredIntent = Intent(context, AlarmFiredActivity::class.java)

        //        alarmFiredIntent.putExtra("lock", true);
        alarmFiredIntent.putExtra("ringtone", ringtone)
        alarmFiredIntent.putExtra("alarmTime", alarmTime)
        alarmFiredIntent.putExtra("question", question)
        alarmFiredIntent.putExtra("answer", answer)
        alarmFiredIntent.putExtra("label", label)

        //Add additional flags to the intent (or with existing flags value).
        //
        //Params:
        //flags – The new flags to set.
        //Returns:
        //Returns the same Intent object, for chaining multiple calls into a single statement.
        alarmFiredIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        context.startActivity(alarmFiredIntent)
    }
}