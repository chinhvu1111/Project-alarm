package com.e15.alarmnats.activity

import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.preference.PreferenceManager
import com.e15.alarmnats.Main_AlarmActivity
import com.e15.alarmnats.Model.Task
import com.e15.alarmnats.R
import com.e15.alarmnats.databinding.PomodoroMainBinding
import com.e15.alarmnats.fragment.PomodoroFragment
import com.e15.alarmnats.service.PomodoroService
import com.e15.alarmnats.service.RingtonePlayService
import com.e15.alarmnats.utils.TimerReceiver

class TimerActivity : BaseActivityPomodoro(), PomodoroFragment.PomodoroListener {

    lateinit var currentTask:Task

    companion object {

        var remainingTime:Long=0

        var tempId:String=""

        var checkClickSetting:Boolean = false

        lateinit var intentFromClass:String

    }

    //Binding Service
    override fun connectService(serviceConnection: ServiceConnection) {

        //Binding (PomodoroService) with (TimerActivity class)
        bindService(Intent(this, PomodoroService::class.java), serviceConnection, AppCompatActivity.BIND_AUTO_CREATE)

    }

    //Unbinding Service
    override fun disconnectService(serviceConnection: ServiceConnection) {

        unbindService(serviceConnection)

    }

    var flagInSetting:Boolean = false;

    //This method extends from (PomodoroFragment class) and this is used to (send broadcast) to (TimerReceiver class)
    //for starting clock
    override fun publishAction(action: String, dataIntent: Intent) {

        val intent = Intent(this, TimerReceiver::class.java)

        var task=Task(0);

        currentTask=task

        tempId=currentTask.hashId

        task.fromIntent(dataIntent);

        intent.setAction(action)

        task.toIntent(intent);

        //Send to TimerReceiver class
        sendBroadcast(intent)

    }


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        flagInSetting=false

        //Creating binding of this (TimerActivity)
        val mBinding:PomodoroMainBinding = DataBindingUtil.setContentView(this, R.layout.pomodoro_main)

        setupToolbar(mBinding)

        //Firstly, Sets all values of attributes of Preferences pomodoro are false
        PreferenceManager.setDefaultValues(this, R.xml.preferences_pomodoro, false)

//        var str=applicationContext.getString(R.string.chagingAttr);
//
//        var setting:SharedPreferences=PreferenceManager.getDefaultSharedPreferences(this);
//
//        var editor:SharedPreferences.Editor=setting.edit();
//
//        editor.putInt("pref_work_time",10);
//
//        editor.commit();

//        editor.apply();

        //If we start new Activity
        //then add fragment to the activity by using function addFragmenToActivity of (BaseActivity class)
        if (savedInstanceState == null) {
            addFragmentToActivity(getSupportFragmentManager(), R.id.fragment_container, PomodoroFragment.instance)
        }

        //insures service persists bound lifecycle
        val timerIntent = Intent(this@TimerActivity, PomodoroService::class.java)

        var task = Task(0);

        task.fromIntent(intent);

        intentFromClass=intent.getStringExtra("fromClass")

//        Log.e("---------this is task: ",task.date)

        task.toIntent(timerIntent);

        //Here is (not) implement (IntentSerice) so it is not executed immediately
        //And bound service we don't need call startService(intent) we just use bindService(intent...)
        startService(timerIntent)
    }

    private fun setupToolbar(mainBinding: PomodoroMainBinding) {

        //Here, we use SettingButton : id (directly)
        //Using binding for id directly
        mainBinding.SettingsButton.setOnClickListener({ view: View ->
            val data = Intent(this@TimerActivity, SettingsActivity::class.java)
            startActivityForResult(data, THEME_REQUEST_CODE)

            flagInSetting=true

            checkClickSetting=true

        })
    }

    override fun onDestroy() {

        var task = Task(0);

        if(!flagInSetting) {

            val timerIntent = Intent(this@TimerActivity, PomodoroService::class.java)

            task.fromIntent(intent);

//            Log.e("---------this is task: ", task.date)

            task.toIntent(timerIntent);

            stopService(timerIntent)
        }

//        var rsIntent=Intent();
//
//        rsIntent.putExtra("done",PomodoroService.taskIsDone);
//
//        task.toIntent(rsIntent)
//
//        setResult(Activity.RESULT_OK,rsIntent)
//
//        finish();
        var intent=Intent(this, RingtonePlayService::class.java)

        stopService(intent)

        super.onDestroy()

    }


    override fun onBackPressed() {

        var task = Task(0);

        if(!flagInSetting) {

            val timerIntent = Intent(this@TimerActivity, PomodoroService::class.java)

            task.fromIntent(intent);

//            Log.e("---------this is task: ", task.date)

            task.toIntent(timerIntent);

            stopService(timerIntent)
        }

        var rsIntent:Intent=Intent();

        if(intentFromClass.equals("TaskManagement")) rsIntent= Intent(this, TaskManagementActivity::class.java);
        if(intentFromClass.equals("EIS")) rsIntent= Intent(this, EisenHowerActivity::class.java);
        if(intentFromClass.equals("Main_AlarmActivity")) rsIntent= Intent(this, Main_AlarmActivity::class.java);
        if(intentFromClass.equals("")) rsIntent= Intent(this, Main_AlarmActivity::class.java);

        rsIntent.putExtra("done",PomodoroService.taskIsDone);

        task.toIntent(rsIntent)

        if(::currentTask.isInitialized){

            rsIntent.putExtra("id",currentTask.hashId)

            rsIntent.putExtra("remainTime",remainingTime)

        }else if(checkClickSetting){

            rsIntent.putExtra("id", tempId)

            rsIntent.putExtra("remainTime",remainingTime)

        }else{
            rsIntent.putExtra("id", -1)
        }


        PomodoroService.taskIsDone=false

//        rsIntent.flags=Intent.FLAG_ACTIVITY_SINGLE_TOP

        remainingTime=0

        startActivity(rsIntent)

        //stop music
        var intent=Intent(this, RingtonePlayService::class.java)

        stopService(intent)

        this.recreate()

        finish()

    }

}