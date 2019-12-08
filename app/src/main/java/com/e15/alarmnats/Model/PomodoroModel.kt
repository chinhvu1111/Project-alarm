package com.e15.alarmnats.Model

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.CountDownTimer
import android.os.VibrationEffect
import android.os.Vibrator
import android.preference.PreferenceManager
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.e15.alarmnats.R
import com.e15.alarmnats.service.PomodoroService
import com.e15.alarmnats.service.RingtonePlayService

class PomodoroModel {
    enum class TimerState {
        WORK,
        BREAK,
        LONG_BREAK
    }

    lateinit var WORK_TIME: String;
    lateinit var BREAK_TIME: String;
    lateinit var LONG_BREAK_TIME: String;
    lateinit var LOOP_AMOUNT_VALUE: String;
    lateinit var VIBRATE: String;
    lateinit var RINGTONE:String

    lateinit var context:Context

    var intervalTimeOrgiginal: MutableLiveData<Double>

    //MutableLiveDate extends LiveDate
    //LiveData which publicly exposes setValue(T) and postValue(T) method.
    //Type parameters:
    //<T> – The type of data hold by this instance
    var currentTime: MutableLiveData<String>;
    var isRunning: MutableLiveData<Boolean>;
    var sessionStart: MutableLiveData<Boolean>;
    var timeIsDone:MutableLiveData<String>

    //Enum data describte state of WORK
    //It includes (WORK, BREAK, LONG_BREAK)
    lateinit var currentState: TimerState;
    lateinit var vibrator: Vibrator;

    var milliSecondsLeft: MutableLiveData<Double>
    var workTime: Double = 0.0;
    var breakTime: Double = 0.0;
    var longBreakTime: Double = 0.0;
    var sessionBeforeLongBreak: Int = 0;
    var sessionCount: Int;

    //Schedule a countdown until a time in the future,
    // with regular notifications on intervals along the way.
    // Example of showing a 30 second countdown in a text field:
    lateinit var timer: CountDownTimer;
    lateinit var sharedPref: SharedPreferences;

    var offsetTime: Double = 0.0;

    constructor(context: Context) {

        this.context=context

        currentTime = MutableLiveData<String>();
        isRunning = MutableLiveData<Boolean>();
        sessionStart = MutableLiveData<Boolean>();
        timeIsDone=MutableLiveData()

        timeIsDone.value="Chưa hoàn thành"

        isRunning.value = false;

        sessionStart.value = false;

        currentState = TimerState.WORK

        sessionCount = 0

        intervalTimeOrgiginal= MutableLiveData()

        intervalTimeOrgiginal.value=0.0

        milliSecondsLeft= MutableLiveData()

        milliSecondsLeft.value=0.0

        sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            vibrator = context.getSystemService(Vibrator::class.java)

        } else {

            vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator;

        }

        WORK_TIME = context.resources.getString(R.string.WORK_KEY)
        BREAK_TIME = context.resources.getString(R.string.BREAK_KEY)
        LONG_BREAK_TIME = context.resources.getString(R.string.LONG_BREAK_KEY)
        LOOP_AMOUNT_VALUE = context.resources.getString(R.string.LOOP_KEY)
        VIBRATE = context.resources.getString(R.string.VIBRATE_KEY)
        RINGTONE= context.resources.getString(R.string.RINGTONE_KEY)

    }

    //Return value*60000 :Long
    private fun convertTime(value: Double): Double {
        return (value.toDouble()) * 60000
    }

    //conver (miliseconds) to (minute):(seconds)
    private fun formatTime(milliSecondsLeft: Double): String {
        val minutes = milliSecondsLeft.toInt() / 60000
        val seconds = milliSecondsLeft.toInt() % 60000 / 1000

        //This is new currentTime
        var currentTime = ""

        if (minutes < 10) {
            currentTime = "0$minutes"
        } else {
            currentTime += minutes
        }

        currentTime += ":"

        if (seconds < 10) {
            currentTime += "0$seconds"
        } else {
            currentTime += seconds
        }
        return currentTime
    }

    private fun updateTimer(milliSecondsLeft: Double) {

        //Updating original currentTime
        currentTime.value = formatTime(milliSecondsLeft)
    }

    //Restart to (Default time)
    fun refreshTimers() {

        //Default work time=25
//        workTime = convertTime(sharedPref.getInt(WORK_TIME, 25))
        if (sharedPref.getInt(WORK_TIME, 25) < intervalTimeOrgiginal.value!! / 1000 / 60) {

            workTime = convertTime(sharedPref.getInt(WORK_TIME, 25).toDouble())

        } else {

            workTime = convertTime((intervalTimeOrgiginal.value!!.toDouble() / 1000 / 60).toDouble())

        }
        //Default break time =5
        breakTime = convertTime(sharedPref.getInt(BREAK_TIME, 5).toDouble())
        //Default long break time=15
        longBreakTime = convertTime(sharedPref.getInt(LONG_BREAK_TIME, 15).toDouble())
        //4 times work time then appear to 1 time long break time
        sessionBeforeLongBreak = sharedPref.getInt(LOOP_AMOUNT_VALUE, 4)

        //Example convert worktime (25:int) to (25:00) String then ==> (Int)

        Log.e("-------------------: ", "Work time " + workTime.toString() + " mili: " + milliSecondsLeft.toString())

        updateTimer(workTime)
    }

    //Move in Running state
    //We can click pause button/ start button
    //So, we must handle all cases
    fun startTimer() {

        Log.e("-------------------", intervalTimeOrgiginal.toString() + currentState)

//        if(intervalTimeOrgiginal.toInt()<0){
//            return
//        }

        //The session is paused
        if (currentState == TimerState.WORK && sessionStart?.value == false) {
            //start session
            sessionStart.value = true
            //state of session is running
            isRunning.value = true

            Log.e("This is interval time: ", intervalTimeOrgiginal.toString())

            //Input is value of work time = mupltiply (current value) and (60000) together
            if (sharedPref.getInt(WORK_TIME, 25) < intervalTimeOrgiginal.value!! / 1000 / 60) {

                timer(convertTime(sharedPref.getInt(WORK_TIME, 25).toDouble()))

            } else {

                timer(convertTime((intervalTimeOrgiginal.value!!.toDouble() / 1000 / 60)))

            }
        } else {
            //This session is (not) running
            isRunning.value = true

            //Recusive miliSeconLeft
            timer(milliSecondsLeft.value!!.toDouble())
        }
    }

    private fun timer(timeLeft: Double) {

        //(Remaining time) of session==0
        //Done session
        //Why setting condition milliSecondsLeft.toInt()==0 ==> Because at (last session) milliSecondsLeft always has value=0

        //When intervalTimeOriginal=0 then we run timer(worktime) worktime=0
        //TimeLeft=0
        if (currentState == TimerState.WORK && milliSecondsLeft.value!!.toInt() == 0) {

            //Warning
            offsetTime = timeLeft;

//            intervalTimeOrgiginal.value -= timeLeft;

            //Because is is here, we are updated intervaltime period
            //And if loop not done, we will add to unit (offset)
            intervalTimeOrgiginal.value = intervalTimeOrgiginal.value!! - timeLeft

            Log.e("-------------------", "substraction of interval time" + intervalTimeOrgiginal.toString())

        }

        //Params:
        //millisInFuture –
        // The number of millis (in the future) from the call to start() until (the countdown is done) and onFinish() is called.

        //countDownInterval –
        // The interval along the way to receive onTick(long) callbacks.
        timer = object : CountDownTimer(timeLeft.toLong(), 300) {

            //(Callback) fired (on regular interval).
            //millisUntilFinished – (The amount of time) until finished.
            override fun onTick(millisUntilFinished: Long) {

                //Remaining (miliSecondLefts)
                milliSecondsLeft.value = millisUntilFinished.toDouble()

//                Log.e("-------------------", "milliSecondsLeft"+milliSecondsLeft.toString())

                //Push data to the UI
                updateTimer(milliSecondsLeft.value!!)

                //Save all (value) attributes to the (SharedPreference)
                //SaveTimer is running consecutively because milliSecondsLeft is added to SharePreference
                //In (each loops)
                saveTimerState()
            }

            //when session is done
            //Finish()
            override fun onFinish() {

                //Notification done task

                milliSecondsLeft.value = 0.0;

                if (intervalTimeOrgiginal.value!!.toInt() == 0) {

                    PomodoroService.taskIsDone = true;

                    timeIsDone.value="Hoàn thành tác vụ"

                    stopMusic();

                    return;

                }

                vibrate()

                when (currentState) {

                    //We have (two case) if currentState=Work
                    //(Short beak time) / (long break time)
                    PomodoroModel.TimerState.WORK ->

                        //Session Count that is amount done session
                        if (sessionCount < sessionBeforeLongBreak) {

                            sessionCount++

                            //Going to break time
                            currentState = TimerState.BREAK

                            //Updating all (values time) (base on SharedPreference) set up in the setting activity
                            refreshTimers()

                            //New Task to execute break time action
                            timer(breakTime)

                        } else {
                            //Done session is restarted
                            sessionCount = 0

                            currentState = TimerState.LONG_BREAK

                            timer(longBreakTime)

                        }
                    PomodoroModel.TimerState.BREAK -> {

                        currentState = TimerState.WORK

                        //Warning
                        //workTime is assigned to new value base on intervalTime
                        refreshTimers()

                        timer(workTime)

                    }
                    PomodoroModel.TimerState.LONG_BREAK -> {

                        currentState = TimerState.WORK

                        refreshTimers()

                        timer(workTime)

                    }
                    else -> {
                    }
                }
            }
        }.start()
    }

    //Set up SharedPreferences to read then match with (SettingSharedPreferences)
    //It not (SettingSharedPreferences)
    private fun saveTimerState() {

        //Create a (new Editor) for (these preferences),
        // through which you can (make modifications) to the data in the preferences and
        // (atomically commit) those changes back to the SharedPreferences object.
        //Note that you must (call SharedPreferences.Editor.commit) to have (any changes)
        // you perform in the Editor actually show up in (the SharedPreferences)
        val editor = sharedPref.edit()

        //Set a boolean value in (the preferences editor), to be written back once commit or apply are called.
        editor.putBoolean("sessionStart", sessionStart.value!!)

        editor.putBoolean("running", isRunning.value!!)

        editor.putInt("sessionCount", sessionCount)

        editor.putInt("sessionBeforeLongBreak", sessionBeforeLongBreak)

        editor.putLong("timeLeft", milliSecondsLeft.value!!.toLong()!!)

        if (currentState == TimerState.WORK) {

            editor.putInt("currentTimer", 0)

        } else if (currentState == TimerState.BREAK) {

            editor.putInt("currentTimer", 1)

        } else {

            editor.putInt("currentTimer", 2)

        }

        //Commit (your preferences changes) back from (this Editor) to the SharedPreferences object it is editing.
        // This atomically performs the requested modifications, replacing whatever is currently in the SharedPreferences.
        //Note that when (two editors) are modifying preferences (at the same time), (the last one) to call apply (wins).
        editor.apply()
    }

    fun pauseTimer() {

        //Cancel the countdown.
        timer.cancel()

        isRunning.value = false

    }

    ///continue to running
    fun restartTimer() {

        restoreTimerState()

        //When (click stop button) then (restart value) of (all) (attribuites) through (new SharePreferences)
        if (isRunning.value == true) {

            //If clock is running/
            //We offset substracted timer

            isRunning.value = false

            startTimer()

        }
    }

    //Reseting from beginning
    //It is here, we just add offset to intervaltime when click reset looping
    fun resetTimer() {

        pauseTimer()

        //Reseting (milliSecondsLeft=0) to subtract (intervalTimeOrgiginal)
        milliSecondsLeft.value = 0.0;

        if (sessionStart.value == true && currentState == TimerState.WORK) {

            intervalTimeOrgiginal.value = intervalTimeOrgiginal.value!! + offsetTime;

        }

        Log.e("-------------------:", "Inside resetTimer " + intervalTimeOrgiginal.toString())

        currentState = TimerState.WORK

        //Session is not started
        //Check starting session
        sessionStart.value = false

        refreshTimers()

    }

    private fun restoreTimerState() {

        //Set up all value of Preferences again
        sessionStart.value = sharedPref.getBoolean("sessionStart", false)

        isRunning.value = sharedPref.getBoolean("running", false)

        sessionCount = sharedPref.getInt("sessionCount", 0)

        sessionBeforeLongBreak = sharedPref.getInt("sessionBeforeLongBreak", 4)

        milliSecondsLeft.value = sharedPref.getLong("timeLeft", 0).toDouble()

        val timerState = sharedPref.getInt("currentTimer", 0)

        if (timerState == 0) {

            currentState = TimerState.WORK

        } else if (timerState == 1) {

            currentState = TimerState.BREAK

        } else {

            currentState = TimerState.LONG_BREAK

        }
    }

    @SuppressLint("MissingPermission")
    private fun vibrate() {

        val vibratePref = sharedPref.getBoolean(VIBRATE, false)

        if (vibratePref) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                vibrator.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE))

            } else {

                vibrator.vibrate(1000)

            }
        }
    }

    fun getCurrentTimer(): String {
        return if (currentState == TimerState.WORK) {
            "Work"
        } else if (currentState == TimerState.BREAK) {
            "Break"
        } else {
            "Long Break"
        }
    }

    fun playMusic(){

        var ringtone_pref=sharedPref.getBoolean(RINGTONE,false)

        if(ringtone_pref){

            var intent=Intent(context,RingtonePlayService::class.java)

            context.startService(intent)

        }

    }

    fun stopMusic(){

        var ringtone_pref=sharedPref.getBoolean(RINGTONE,false)

        if(ringtone_pref){

            var intent=Intent(context,RingtonePlayService::class.java)

            context.stopService(intent)

        }

    }

}