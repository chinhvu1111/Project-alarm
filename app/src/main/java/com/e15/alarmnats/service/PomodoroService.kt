package com.e15.alarmnats.service

import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.Observer
import com.e15.alarmnats.Model.PomodoroModel
import com.e15.alarmnats.Model.Task
import com.e15.alarmnats.data.PomodoroRepository
import com.e15.alarmnats.utils.InjectorUtils
import com.e15.alarmnats.utils.NotificationUtil
import java.text.SimpleDateFormat

//We can understand that (LifecycleService) is a (service) of (LifecycleOwner)
class PomodoroService : LifecycleService() {

    private var notifUtil: NotificationUtil? = null

    private var pomodoroRepository: PomodoroRepository? = null
    private var pomodoroModel: PomodoroModel? = null
    private var connected: Boolean? = null
    private var notification: Boolean? = null
    private var session: Boolean? = null

    override fun onCreate() {
        super.onCreate()
        connected = false
        notification = false

        notifUtil = NotificationUtil(this)
        pomodoroModel = PomodoroModel(this)
        pomodoroRepository = InjectorUtils.providePomodoroRepository()

        pomodoroModel!!.currentTime.observe(this, Observer { routeUpdate(it) })
        pomodoroModel!!.isRunning.observe(this, Observer { updateState(it) })
        pomodoroModel!!.sessionStart.observe(this, Observer { updateSession(it) })

        pomodoroModel!!.timeIsDone.observe(this, Observer { updateStateOfTaskDoneOrNot(it) })

        pomodoroModel!!.refreshTimers()

        if(intervalTime==-1.toDouble()){

            pomodoroModel!!.intervalTimeOrgiginal.value= Double.MAX_VALUE;

        }else{

            pomodoroModel!!.intervalTimeOrgiginal.value= intervalTime;

        }

        pomodoroModel!!.intervalTimeOrgiginal.observe(this, Observer{ updateCurrentEndTime(it) })

        pomodoroModel!!.milliSecondsLeft.observe(this, Observer { updateCurrentLeftTimeSession(it,pomodoroModel!!) })

        //Chech How many times PomodoroService is created
        //Create only one instance to call many time
        //And each time just call startCommand() and don't go through onCreate()
//        Toast.makeText(applicationContext,"This function is used to create PomodoroService "+pomodoroModel!!.intervalTimeOrgiginal,Toast.LENGTH_SHORT).show()

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

//        Toast.makeText(applicationContext, intervalTime.toString(),Toast.LENGTH_SHORT).show();
        //Get interval time
        Log.e("If we put this code:","PomodoroModel!!.intervalTimeOrgiginal= intervalTime; here instartCommand" +
                "==> reset intervalTime of PomodoroModel each time when click pauseButton --> Wrong")
//        PomodoroModel!!.intervalTimeOrgiginal= intervalTime;

        var requestingIntent=intent;

        var task=Task(0);

        if(intent!=null) task.fromIntent(intent!!)

        var difference:Double;

        if(task!!.remainingTime>0.toLong()){

            difference=task.remainingTime.toDouble()

        }
        else if(task.startTime==null&&task.endTime==null){

            difference=-1.toDouble()

        }
        else{

            var simpleDateFormat: SimpleDateFormat = SimpleDateFormat("HH:mm");

            var startTime = simpleDateFormat.parse(task.startTime);
            var endTime = simpleDateFormat.parse(task.endTime);

            difference = (endTime.time - startTime.time).toDouble()

        }

        //This code is (useless)
        //Because above (PomodoroModel.intervalTime) is assigned to (PomodoroService.intervalTime)
        intervalTime=difference

//        Toast.makeText(applicationContext,"This is "+ intervalTime.toString(),Toast.LENGTH_SHORT).show()

        super.onStartCommand(intent, flags, startId)

        if (intent == null) {
            pomodoroModel!!.restartTimer()
            if (pomodoroModel!!.isRunning.value!!) {
                startNotification()
            }

        } else {
            if (intent.action != null) {
                when (intent.action) {
                    ACTION_START -> {

                        pomodoroModel!!.playMusic()

                        pomodoroModel!!.startTimer()

                        handleNotification()
                    }
                    ACTION_PAUSE -> {

                        pomodoroModel!!.stopMusic()

                        pomodoroModel!!.pauseTimer()

                        handleNotification()
                    }
                    ACTION_RESET -> {

                        pomodoroModel!!.stopMusic()

                        pomodoroModel!!.resetTimer()

                    }
                    ACTION_STOP -> {

                        pomodoroModel!!.stopMusic()

                        stopNotification()

                        stopSelf()

                    }
                    SCREEN_OFF -> screenOff()
                }
            }
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        return null
    }

    override fun onUnbind(intent: Intent): Boolean {
        connected = false
        if (session!!) {
            startNotification()
        }
        return true
    }

    override fun onRebind(intent: Intent) {
        super.onRebind(intent)
        connected = true
        stopNotification()
    }

    //Update immediately (corresponding value attribute) of PomoroModel class
    private fun routeUpdate(time: String) {

        if (notification!!) {

            notifUtil!!.updateNotification(time, pomodoroModel!!.currentTime.toString())

        }

        pomodoroRepository!!.setTime(time)

    }

    private fun updateState(state: Boolean?) {
        pomodoroRepository!!.state=state
    }

    private fun updateSession(state: Boolean?) {
        session = state
        pomodoroRepository!!.setSessionStartedData(state)
    }

    fun updateStateOfTaskDoneOrNot(stateIsDone:String){

        pomodoroRepository!!.setDoneOrNot(stateIsDone)

    }

    fun updateCurrentEndTime(currentEndTime:Double){

        if(currentEndTime==Double.MAX_VALUE){

            pomodoroRepository!!.setCurrentEndTime(0)

        }else{

            pomodoroRepository!!.setCurrentEndTime(currentEndTime.toLong())

        }

        Log.e("----------Endtime",currentEndTime.toString())

    }


    fun updateCurrentLeftTimeSession(currentLeftTime:Double, pomodoroModel: PomodoroModel){

        if(pomodoroModel!!.currentState==PomodoroModel.TimerState.WORK) pomodoroRepository!!.setCurrentLeftTimeMili(currentLeftTime.toLong())

    }

    private fun handleNotification() {
        if (notification!!) {
            startNotification()
        }
    }

    private fun startNotification() {
        val pomNotification = notifUtil!!.buildNotification(
                pomodoroModel!!.currentTime.value!!,
                pomodoroModel!!.isRunning.value!!,
                pomodoroModel!!.getCurrentTimer()
        )

        //If your service is started (running through Context.startService(Intent)),
        // then also make this service run in the foreground,
        // supplying the ongoing notification to be shown to the user while in this state.
        startForeground(NotificationUtil.NOTIFICATION_ID, pomNotification)
        notification = true
    }

    private fun stopNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        } else {
            stopForeground(true)
        }
        notification = false
    }

    /**
     * Start notification when sessions has started and user turns screen off
     */
    private fun screenOff() {
        //Note if it is wrong
        if (!(connected)!! && !(notification)!! && session!!) {
            startNotification()
        }
    }

    companion object {

        val SCREEN_OFF = "timer_screen_off"
        val ACTION_START = "START"
        val ACTION_PAUSE = "PAUSE"
        val ACTION_STOP = "STOP"
        val ACTION_RESET = "RESET"

        var intervalTime:Double = 0.0;

        var taskIsDone:Boolean = false;

    }

}