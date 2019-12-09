package com.e15.alarmnats.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import com.e15.alarmnats.R

class RingtonePlayService: Service() {

    lateinit var media_song:MediaPlayer;

    var startId:Int = 0;

    var isRunning:Boolean = false;

    var mBinder:IBinder=LocalBinder()

    class LocalBinder: Binder(){
        fun getService():RingtonePlayService{

            return RingtonePlayService();

        }
    }

    override fun onCreate() {
        var sharedPreferences=this.getSharedPreferences("alarm_tune", Context.MODE_PRIVATE)

        var uri=sharedPreferences.getInt("tune",0)

        if(uri!=0) media_song= MediaPlayer.create(applicationContext,uri)
        else{

            media_song= MediaPlayer.create(applicationContext,R.raw.silver_flame)

        }
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        media_song.start()

        return START_STICKY

    }

    override fun onDestroy() {

        media_song.stop()

        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}