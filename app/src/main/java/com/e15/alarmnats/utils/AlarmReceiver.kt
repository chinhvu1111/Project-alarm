package com.e15.alarmnats.utils

import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.SystemClock
import android.util.Log
import androidx.core.app.TaskStackBuilder
import androidx.legacy.content.WakefulBroadcastReceiver
import com.e15.alarmnats.Database.ReminderDatabase
import com.e15.alarmnats.R
import com.e15.alarmnats.activity.TaskManagementActivity
import com.e15.alarmnats.activity.TimerActivity
import java.util.*

//This helper is for an old pattern of implementing a (BroadcastReceiver)
// that receives (a device wakeup event) and then passes the work off to (a android.app.Service),
// while ensuring that the device does not go back to sleep during the transition.
//This class takes care of creating and managing a partial wake lock for you;
// you must request the (android.Manifest.permission.WAKE_LOCK permission) to use it.
class AlarmReceiver : WakefulBroadcastReceiver() {

    private var alarmManager: AlarmManager? = null

    private var dbHandler: ReminderDatabase? = null

    override fun onReceive(context: Context, intent: Intent) {

        //When alarm is fired
        //holding corresponding intent
        val hashId = intent.getStringExtra("hashId")
        val requestCode = intent.getIntExtra("requestCode",0)

        dbHandler = ReminderDatabase(context)

        var task=dbHandler!!.getEventId(hashId);

        //(Get event) that has id (stored) in the (transition intent)
        val event = dbHandler!!.getEventId(hashId)

        //When click on (the notification) then navigate to (the destination activity)
        val i = Intent(context, TimerActivity::class.java)

        i.putExtra("id",hashId);
        i.putExtra("date",task!!.date);
        i.putExtra("startTime",task.startTime);
        i.putExtra("endTime",task.endTime);
        i.putExtra("title",task.title)
        i.putExtra("description",task.description);
        i.putExtra("enabled",task.enabled);

        i.putExtra("taskIsDone",task.isDone)
        i.putExtra("remainingTime",task.remainingTime)
        i.putExtra("fromClass","TaskManagement")

        i.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP

        var stackBuilder:TaskStackBuilder= TaskStackBuilder.create(context)

//        var backIntent=Intent(context,TaskManagementActivity::class.java);
//
//        stackBuilder.addNextIntent(backIntent)

        stackBuilder.addNextIntentWithParentStack(i)

        //FLAG_UPDATE_CURRENT
        //Flag indicating that if the (described PendingIntent) already exists,
        // then keep it but (replace) its (extra data) with what is in this (new Intent).
        // For use with (getActivity), (getBroadcast), and (getService).
        //This can be used if you are (creating intents) where only the (extras change),
        // and don't care that (any entities) that received your previous PendingIntent will be able to launch it
        // with your new extras even if they are not explicitly given to it.
        val pendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT)

        var sharedPreferences=context.getSharedPreferences("alarm_tune",Context.MODE_PRIVATE)

        var uri=sharedPreferences.getInt("tune",0)

        val builder = Notification.Builder(context)
                .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher))
                .setSmallIcon(R.drawable.ic_alarm_small)
                .setContentTitle("Reminder")
                .setContentText(event!!.title)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setOnlyAlertOnce(true)
                .setSound(Uri.parse("android.resource://"+context.packageName+"/"+uri))

        val manager:NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        manager.notify(requestCode, builder.build())

        Log.i("Receiver", "called")

    }

    fun setAlarm(context: Context, startCalendar: Calendar,endCalendar: Calendar, requestCode: Int,hashId:String) {

        alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, AlarmReceiver::class.java)

        intent.putExtra("hashId", hashId)
        intent.putExtra("requestCode", requestCode)

        val pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent,
                PendingIntent.FLAG_CANCEL_CURRENT)

        val now = Calendar.getInstance()

        Log.e("---------current time",now.toString())

        val currentTime = now.timeInMillis

        val differentTime = startCalendar.timeInMillis - currentTime

        //ELAPSED_REALTIME
        //Alarm time in SystemClock.elapsedRealtime (time since boot), including sleep).
        // This alarm does not wake the device up; if it goes off while the device is asleep,
        // it will not be delivered until the next time the device wakes up.

        //This means when setting alarm then you end application, difference time will be saved
        //Util (your devive is restarted), since (booting app), after ( <difference> mili times) the broadcast is sent
        alarmManager!!.set(AlarmManager.ELAPSED_REALTIME , SystemClock.elapsedRealtime() + differentTime,
                pendingIntent)

        // Restart alarm if device is rebooted
        val receiver = ComponentName(context, BootReceiver::class.java!!)

        val pm = context.packageManager

        //Flag parameter for setComponentEnabledSetting(ComponentName, int, int) to
        // indicate that you (don't want to kill) (the app containing the component).
        // Be careful when you set this since changing component states can make the containing application's behavior unpredictable.
        pm.setComponentEnabledSetting(receiver,

                //Flag for setApplicationEnabledSetting(String, int, int) and
                // setComponentEnabledSetting(ComponentName, int, int):
                // This (component or application) has been explictily enabled,
                // regardless of what it has specified in its manifest.
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,

                PackageManager.DONT_KILL_APP)

    }


    //This method to cancel (Alarm set)
    fun cancelAlarm(context: Context, id: Int) {

        alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val pendingIntent = PendingIntent.getBroadcast(context, id, Intent(context, AlarmReceiver::class.java), 0)

        //Remove any alarms with a matching Intent.
        // Any alarm, of any type,
        // whose Intent matches this one (as defined by Intent.filterEquals), will be canceled.
        alarmManager!!.cancel(pendingIntent)

        //Create a (new component) identifier from a (Context) and (Class) object.
        // Disable alarm
        val receiver = ComponentName(context, BootReceiver::class.java!!)

        //context.getPackageMananger()
        //Return PackageManager instance to find global (package information).
        val pm = context.packageManager

        //Set the (enabled setting) for a package component (activity, receiver, service, provider).
        // This setting will override (any enabled state) which may have been set by the component in its manifest.
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP)

    }


    fun setRepeatAlarm(context: Context, startCalendar: Calendar, endCalendar: Calendar, requestCode: Int, repeatTime: Long,hashId:String) {

        //Return the handle to (a system-level service) by name.
        // The class of the returned object varies by the requested name.
        alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Put Reminder ID in Intent Extra
        val intent = Intent(context, AlarmReceiver::class.java)

        intent.putExtra("requestCode", requestCode)
        intent.putExtra("hashId", hashId)

        val pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT)

        // Calculate notification time in
        val c = Calendar.getInstance()

        val currentTime = c.timeInMillis

        val diffTime = startCalendar.timeInMillis - currentTime

        // Start alarm using initial notification time and repeat (interval time)
        alarmManager!!.setRepeating(AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + diffTime,
                repeatTime, pendingIntent)

        // Restart alarm if device is rebooted
        val receiver = ComponentName(context, BootReceiver::class.java!!)

        val pm = context.packageManager

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP)

    }
}
