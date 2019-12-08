package com.e15.alarmnats.data

import androidx.lifecycle.MutableLiveData

class PomodoroDao {

    val timeData: MutableLiveData<String>
    val currState: MutableLiveData<Boolean>
    val sessionStartedData: MutableLiveData<Boolean>
    var taskIsDoneData:MutableLiveData<String>
    var currentEndTime:MutableLiveData<Long>
    var currentLeftTimeMiliData:MutableLiveData<Long>

    var time: String?
        get() = timeData.value
        set(time) {
            timeData.value = time
        }

    var state: Boolean?
        get() = currState.value
        set(state) {
            currState.value = state
        }

    var sessionStarted: Boolean?
        get() = sessionStartedData.value
        set(state) {
            sessionStartedData.value = state
        }

    var taskIsDone:String
    get()= taskIsDoneData.value!!
    set(value){
        taskIsDoneData.value=value
    }

    var endTime:Long
    get()= currentEndTime.value!!
    set(value){
        currentEndTime.value=value
    }

    var currentLeftTimeMili:Long
    get()=currentLeftTimeMiliData.value!!
    set(value){
        currentLeftTimeMiliData.value=value
    }

    init {
        timeData = MutableLiveData()
        currState = MutableLiveData()
        sessionStartedData = MutableLiveData()

        taskIsDoneData= MutableLiveData()

        currentEndTime=MutableLiveData()

        currentLeftTimeMiliData=MutableLiveData()

    }

}