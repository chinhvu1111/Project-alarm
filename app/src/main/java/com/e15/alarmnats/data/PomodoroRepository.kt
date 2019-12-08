package com.e15.alarmnats.data

import androidx.lifecycle.MutableLiveData

class PomodoroRepository private constructor(private val pomodoroDao: PomodoroDao) {

    val timeData: MutableLiveData<String>
        get() = pomodoroDao.timeData

    val stateData: MutableLiveData<Boolean>
        get() = pomodoroDao.currState

    var taskIsDone: MutableLiveData<String>
        get() = pomodoroDao.taskIsDoneData
        set(value) {
            taskIsDone=value
        }

    var currEndTime:MutableLiveData<Long>
    get()= pomodoroDao.currentEndTime
    set(value){
        currEndTime=value
    }

    var currentLeftTimeMili:MutableLiveData<Long>
    get()=pomodoroDao.currentLeftTimeMiliData
    set(value){
        currentLeftTimeMili=value
    }

    //For changing value of (<currentTime> attribute) of (PomodoroDAO) class
    val currentTime: String
        get() = pomodoroDao.time!!

    var state: Boolean?
        get() = pomodoroDao.state
        set(state) {
            pomodoroDao.state = state
        }

    val sessionStartedData: MutableLiveData<Boolean>
        get() = pomodoroDao.sessionStartedData

    val sessionStarted: Boolean?
        get() = pomodoroDao.sessionStarted

    //update (current time) of UI
    //Remember that if you want to (change value) of (<timedata> attribute) of
    //                                                                          (PomodoroDAO) class or (PomodoroRepository) class
    //You must to (change value) of (<time> attribute) of (PomodoroDAO)
    fun setTime(time: String) {
        pomodoroDao.time = time
    }

    fun setSessionStartedData(state: Boolean?) {
        pomodoroDao.sessionStarted = state
    }

    fun setDoneOrNot(taskisDone: String) {

        pomodoroDao.taskIsDone = taskisDone

    }

    fun setCurrentEndTime(currentEndTime:Long){

        pomodoroDao.endTime=currentEndTime

    }

    fun setCurrentLeftTimeMili(currentLeftTimeMili:Long){

        pomodoroDao.currentLeftTimeMili=currentLeftTimeMili

    }

    companion object {

        private var instance: PomodoroRepository? = null

        fun getInstance(pomodoroDao: PomodoroDao): PomodoroRepository? {

            if (instance == null) {
                synchronized(PomodoroRepository::class.java) {
                    if (instance == null) {
                        instance = PomodoroRepository(pomodoroDao)
                    }
                }
            }
            return instance
        }
    }
}