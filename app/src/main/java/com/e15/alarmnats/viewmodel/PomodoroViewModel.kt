package com.e15.alarmnats.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.e15.alarmnats.data.PomodoroRepository
import com.e15.alarmnats.service.PomodoroService

class PomodoroViewModel(pomodoroRepository: PomodoroRepository) : ViewModel() {

    //This attribute represents to time value now.
    val currentTime: LiveData<String>
    val stateData: LiveData<Boolean>
    val sessionData: LiveData<Boolean>

    var tastIsDone:LiveData<String>

    var currentEndTime:LiveData<Long>

    var currentLeftTimeMiliStr:LiveData<String>

    var currentLeftTimeMiliLong:LiveData<Long>

    //attributes represents state button is clicked
    private val stopButtonClickable: MutableLiveData<Boolean>

    //
    private val playPauseButtonClickable: MutableLiveData<Boolean>

    //State visible/ invisible of button stop
    private val stopButtonVisibility: MutableLiveData<Boolean>
    //animation state --> button moves when you click either (start button) or (pause button)
    var animationState: Boolean = false

    val timerRunning: Boolean?
        get() = if (stateData.value != null) {
            stateData.value
        } else false


    init {
        //Class Transformations
        //Transformation methods for LiveData.
        //These methods permit (functional composition) and delegation of (LiveData instances).
        // The transformations are calculated lazily, and will (run only) when the returned (LiveData is observed).
        // (Lifecycle behavior) is propagated from the (input source LiveData) to the returned one.

        //function map
        //Returns a LiveData mapped from the (input source LiveData)
        // by applying mapFunction to each value set on source.

        //This line is used to (map pair of values) from (pomodoroRepository) to (PomodoroViewModel)
        currentTime = Transformations.map(pomodoroRepository.timeData) { time -> time }
        stateData = Transformations.map(pomodoroRepository.stateData) { state -> state }
        sessionData = Transformations.map(pomodoroRepository.sessionStartedData) { state ->
            animationState = state
            state
        }
        tastIsDone=Transformations.map(pomodoroRepository.taskIsDone){done->done}

        currentEndTime=Transformations.map(pomodoroRepository.currEndTime){endtime->endtime.toLong()}

        //Interval time we have run to until now
        currentLeftTimeMiliStr=Transformations.map(pomodoroRepository.currentLeftTimeMili)
        {leftTimeMili ->"Còn lại "+(currentEndTime.value!!+leftTimeMili)/60/1000+" phút "+(currentEndTime.value!!+leftTimeMili)/1000%60 }

        currentLeftTimeMiliLong=Transformations.map(pomodoroRepository.currentLeftTimeMili)
        {leftTimeMili ->(currentEndTime.value!!+leftTimeMili) }

        stopButtonVisibility = MutableLiveData()
        playPauseButtonClickable = MutableLiveData()
        stopButtonClickable = MutableLiveData()
        initDefaults()
    }

    //Default values
    private fun initDefaults() {
        //Pause button is clicked --> play
        playPauseButtonClickable.setValue(true)

        //stop button on the right of the screen appear that means the clock is running
        stopButtonVisibility.setValue(false)

        //Stop button is clicked --> animation appears in the screen
        stopButtonClickable.setValue(false)
        animationState = false
    }

    fun getPlayPauseButtonClickable(): LiveData<Boolean> {
        return playPauseButtonClickable
    }

    fun setPlayPauseButtonClickable(state: Boolean) {
        playPauseButtonClickable.setValue(state)
    }

    fun getStopButtonClickable(): LiveData<Boolean> {
        return stopButtonClickable
    }

    fun setStopButtonClickable(state: Boolean) {
        stopButtonClickable.setValue(state)
    }

    fun getStopButtonVisibility(): LiveData<Boolean> {
        return stopButtonVisibility
    }

    fun setStopButtonVisibility(state: Boolean) {
        stopButtonVisibility.setValue(state)
    }

}