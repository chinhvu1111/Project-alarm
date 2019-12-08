package com.e15.alarmnats.Model

import android.app.Activity
import android.content.Context
import android.media.MediaPlayer

class AlarmSound: Activity {

    lateinit var mp:MediaPlayer;

    lateinit var context:Context;

    var track:Int = 0;

    constructor(context: Context) : super() {
        this.context = context
    }

    fun playRingTone(){

        mp= MediaPlayer.create(context, track)

        mp.start()

    }

    fun stopRingTone(){

        if(::mp.isInitialized==false) return

        mp.stop()

        mp.release()

        return;

    }

    fun chooseTrack(id:Int){

        track=id

    }

}