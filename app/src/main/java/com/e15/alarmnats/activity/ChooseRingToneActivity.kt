package com.e15.alarmnats.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View

import com.e15.alarmnats.R

class ChooseRingToneActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_ring_tone)
    }

    fun chooseSpotifyRingtone(v: View) {
        val returnIntent = Intent()
        returnIntent.putExtra("ringtoneType", "spotify")
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }

    fun chooseSystemRingtone(v: View) {
        val returnIntent = Intent()
        returnIntent.putExtra("ringtoneType", "system")
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }
}
