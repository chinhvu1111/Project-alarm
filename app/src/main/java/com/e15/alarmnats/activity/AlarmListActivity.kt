package com.e15.alarmnats.activity

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.e15.alarmnats.AlarmReceiver
import com.e15.alarmnats.Database.AlarmDbHelper
import com.e15.alarmnats.Model.Alarm
import com.e15.alarmnats.R
import com.e15.alarmnats.adapter.RecyclerViewAdapter

import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Date

import android.app.Activity.RESULT_OK

//This class handles RecycleView and bind ViewHolder
class AlarmListActivity : Fragment() {

    // variable declarations
    private var addAlarmButton: FloatingActionButton? = null

    private var alarm: Alarm? = null
    private var dbHelper: AlarmDbHelper? = null

    // list to save database alarm data
    private val mAlarmTimes = ArrayList<String>()
    private val mAlarmTimesInMillis = ArrayList<Long>()
    private val mAlarmStatuses = ArrayList<Boolean>()
    private val mRingtoneNames = ArrayList<String>()
    private val mRingtoneUris = ArrayList<String>()
    private val mLabels = ArrayList<String>()
    private val mQuestions = ArrayList<String>()
    private val mFlags = ArrayList<Int>()

    private var adapter: RecyclerViewAdapter? = null

    private var alarmIntent: Intent? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.alarm_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Inflate the layout for this fragment
        alarm = Alarm()
        dbHelper = AlarmDbHelper.getInstance(context!!)
        // get all alarms from db
        getAlarms()

        // intent to pass to broadcast receiver
//        alarmIntent = Intent(context, AlarmReceiver::class.java)

        addAlarmButton = getView()!!.findViewById(R.id.addAlarmButton)

        // add new alarm
        addAlarmButton!!.setOnClickListener {
            val alarm = defaultAlarmObject()
            val setAlarmIntent = Intent(context, SetAlarmActivity::class.java)
            setAlarmIntent.putExtra("alarmObject", alarm)
            setAlarmIntent.putExtra("isNewAlarm", true)
            this@AlarmListActivity.startActivityForResult(setAlarmIntent, SET_ALARM_INTENT_REQUEST_CODE)
        }
    }

    // get result from new alarm activity
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //        adapter.onRecAdapterActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            alarm = data!!.getSerializableExtra("alarmObject") as Alarm
            if (requestCode == SET_ALARM_INTENT_REQUEST_CODE) {
                dbHelper!!.addAlarm(alarm!!)
                getAlarms()
            } else if (requestCode == EDIT_ALARM_INTENT_REQUEST_CODE) {
                dbHelper!!.updateAlarm(alarm!!)
                getAlarms()
            }
        }
    }

    // get all alarms
    fun getAlarms() {
        val alarmList = dbHelper!!.allAlarms

        mAlarmTimes.clear()
        mAlarmTimesInMillis.clear()
        mAlarmStatuses.clear()
        mRingtoneNames.clear()
        mRingtoneUris.clear()
        mLabels.clear()
        mQuestions.clear()
        mFlags.clear()

        for (onealarm in alarmList) {
            mAlarmTimes.add(onealarm.alarmTime!!)
            mAlarmTimesInMillis.add(onealarm.alarmTimeInMillis)
            mAlarmStatuses.add(onealarm.isAlarmStatus)
            mRingtoneNames.add(onealarm.ringtoneName!!)
            mRingtoneUris.add(onealarm.ringtoneUri!!)
            mLabels.add(onealarm.label!!)
            mQuestions.add(onealarm.question!!)
            mFlags.add(onealarm.flag!!)
        }
        initRecyclerView()
    }

    // initialize recycler view
    private fun initRecyclerView() {
        val recyclerView = view!!.findViewById<RecyclerView>(R.id.recycler_view)
        adapter = RecyclerViewAdapter(mAlarmTimes,
                mAlarmTimesInMillis,
                mAlarmStatuses,
                mRingtoneNames,
                mRingtoneUris,
                mLabels,
                mQuestions,
                mFlags,
                this!!.context!!,
                this@AlarmListActivity)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)
    }

    // delete alarm
    fun deleteAlarm(flag: Int, position: Int) {
        Log.d("option_display", "before if")
        if (dbHelper!!.deleteAlarm(flag)) {
            Log.d("option_display", "row deleted")
            cancelAlarm(mFlags[position], false) //delete alarm in DB => no need to change status in DB
            getAlarms()
        }
    }

    // cancel / disable Alarm
    fun cancelAlarm(flag: Int, changeStatus: Boolean) {
        val alarmManager = context!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val receiverIntent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, flag, receiverIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        alarmManager.cancel(pendingIntent)

        if (changeStatus) {
            dbHelper!!.updateStatus(flag, 0)
        }
    }

    // enable alarm
    fun enableExistingAlarm(flag: Int) {
        val alarm = dbHelper!!.getAlarm(flag)

        val receiverIntent = Intent(context, AlarmReceiver::class.java)
        receiverIntent.putExtra("alarmTime", alarm.alarmTime)
        receiverIntent.putExtra("question", alarm.question)
        receiverIntent.putExtra("answer", alarm.answer)
        receiverIntent.putExtra("ringtoneUri", alarm.ringtoneUri)

        val alarmManager = context!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val pendingIntent = PendingIntent.getBroadcast(context, flag, receiverIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarm.alarmTimeInMillis, pendingIntent)
        dbHelper!!.updateStatus(flag, 1)
    }

    //edit alarm
    fun editAlarm(flag: Int) {
        val alarm = dbHelper!!.getAlarm(flag)
        val intent = Intent(context, SetAlarmActivity::class.java)
        intent.putExtra("alarmObject", alarm)
        intent.putExtra("isNewAlarm", false)
        startActivityForResult(intent, EDIT_ALARM_INTENT_REQUEST_CODE)
    }

    fun defaultAlarmObject(): Alarm {
        val sdf = SimpleDateFormat("HH:mm")
        val currentTime = sdf.format(Date())
        val ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        val ringtoneName = RingtoneManager.getRingtone(context, ringtoneUri).getTitle(context)

        return Alarm(0,
                currentTime,
                System.currentTimeMillis(),
                true,
                ringtoneName,
                ringtoneUri.toString(),
                "",
                -1,
                getString(R.string.default_question),
                "default")
    }

    companion object {

        private val SET_ALARM_INTENT_REQUEST_CODE = 1
        val SCAN_QR_CODE_INTENT_REQUEST_CODE = 100
        val MATH_TEST_INTENT_REQUEST_CODE = 200
        val VERIFY_CAPTCHA_INTENT_REQUEST_CODE = 201
        val EDIT_ALARM_INTENT_REQUEST_CODE = 300
        val CAMERA_PERMISSION_REQUEST_CODE = 301
        val CHOOSE_TASK_REQUEST_CODE = 400
    }
}