package com.e15.alarmnats.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView

import com.e15.alarmnats.R
import com.spotify.sdk.android.authentication.AuthenticationClient
import com.spotify.sdk.android.authentication.AuthenticationRequest
import com.spotify.sdk.android.authentication.AuthenticationResponse
import com.spotify.sdk.android.player.Config
import com.spotify.sdk.android.player.ConnectionStateCallback
import com.spotify.sdk.android.player.Player
import com.spotify.sdk.android.player.PlayerNotificationCallback
import com.spotify.sdk.android.player.PlayerState
import com.spotify.sdk.android.player.Spotify

import java.net.InetAddress

class AlarmFiredActivity : AppCompatActivity(), ConnectionStateCallback, PlayerNotificationCallback {
    private val CLIENT_ID = "848eaa9b8e114c4eb674481982b0ed5a"
    private val REDIRECT_URI = "alarmnats://callback"
    private var numOfAuthTries = 0

    internal var mediaPlayer: MediaPlayer? = null
    private var spotifyPlayer: Player? = null
    private var ringtone: String? = null
    private var question: String? = null
    private var answer: String? = null
    private var label: String? = null
    internal lateinit var ringtoneUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_fired)

        question = intent.extras!!.getString("question")
        answer = intent.extras!!.getString("answer")
        label = intent.extras!!.getString("label")

        val decorView = window.decorView
        decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                // Set the content to appear under the system bars so that the
                // content doesn't resize when the system bars hide and show.
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                // Hide the nav bar and status bar
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)

        //        dbHelper = AlarmDbHelper.getInstance(this);

        ringtone = intent.extras!!.getString("ringtone")

        Log.d("fired", ringtone!!)

        ringtoneUri = Uri.parse(ringtone)
        if (ringtone!!.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0] == "spotify") {
            checkAvailable("spotify.com")
        } else {
            systemRing()
        }
    }

    private fun systemRing() {
        mediaPlayer = MediaPlayer()
        try {
            mediaPlayer!!.setDataSource(this, ringtoneUri)
            val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
            if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
                mediaPlayer!!.setAudioStreamType(AudioManager.STREAM_ALARM)
                //Sets the player to be (looping) or (non-looping).
                mediaPlayer!!.isLooping = true
                mediaPlayer!!.prepare()
                mediaPlayer!!.start()
            }

            startQuestion()

        } catch (e: Exception) {
            Log.e("media exception", e.toString())
        }

    }

    private fun checkAvailable(url: String) {
        val checkIntenet = Task()
        checkIntenet.execute(url)

        val handler = Handler()
        handler.postDelayed({
            if (checkIntenet.status == AsyncTask.Status.RUNNING)
                checkIntenet.onPostExecute(url + "_no")
        }, 1000)
    }

    private fun startQuestion() {
        if (question == getString(R.string.qr_question)) {
            val intent = Intent(this, QRscanActivity::class.java)
            intent.putExtra("isSettingNewAlarm", false)
            intent.putExtra("answer", answer)
            intent.putExtra("label", label)
            startActivityForResult(intent, AlarmListActivity.SCAN_QR_CODE_INTENT_REQUEST_CODE)
        } else if (question == getString(R.string.math_question)) {
            val intent = Intent(this, MathTestActivity::class.java)
            intent.putExtra("label", label)
            startActivityForResult(intent, AlarmListActivity.MATH_TEST_INTENT_REQUEST_CODE)
        } else if (question == getString(R.string.recaptcha_question)) {
            checkAvailable("google.com")
        } else {
            // Default
            val textView = findViewById<TextView>(R.id.defaultMsg)
            textView.text = label

            val dismissBtn = findViewById<View>(R.id.button_dismiss) as Button
            dismissBtn.setOnClickListener { finishActivity() }
        }
    }

    // authorizes user towards Spotify, this is called if initPlayer fails to fetch the player
    fun authSpotify() {

        if (numOfAuthTries > 20) {
            Log.e(TAG, "Tried to auth too many times")
            return
        }

        numOfAuthTries++

        val builder = AuthenticationRequest.Builder(
                CLIENT_ID,
                AuthenticationResponse.Type.TOKEN,
                REDIRECT_URI)

        builder.setScopes(arrayOf("streaming"))
        val request = builder.build()

        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE) {

                val response = AuthenticationClient.getResponse(resultCode, intent)

                if (response.type == AuthenticationResponse.Type.TOKEN) {

                    val config = Config(this, response.accessToken, CLIENT_ID)

                    Spotify.getPlayer(config, this@AlarmFiredActivity, object : Player.InitializationObserver {
                        override fun onInitialized(player: Player) {
                            Log.d(TAG, "onInitialized")

                            spotifyPlayer = player
                            spotifyPlayer!!.addConnectionStateCallback(this@AlarmFiredActivity)
                            spotifyPlayer!!.addPlayerNotificationCallback(this@AlarmFiredActivity)
                            println("auth complete")
                        }

                        override fun onError(throwable: Throwable) {
                            Log.e(TAG, "Could not initialize player: " + throwable.message)
                            authSpotify()
                        }
                    })

                }
            } else if (requestCode == AlarmListActivity.SCAN_QR_CODE_INTENT_REQUEST_CODE
                    || requestCode == AlarmListActivity.MATH_TEST_INTENT_REQUEST_CODE
                    || requestCode == AlarmListActivity.VERIFY_CAPTCHA_INTENT_REQUEST_CODE) {
                finishActivity()
            }
        } else
            startQuestion()
    }

    private fun finishActivity() {
        if (mediaPlayer != null)
            mediaPlayer!!.stop()
        if (spotifyPlayer != null)
            spotifyPlayer!!.pause()

        finish()
    }

    override fun onLoggedIn() {
        // Plays song as soon as auth is done and user is logged in
        println("now playing")
        spotifyPlayer!!.play(ringtone)
        startQuestion()
    }

    override fun onLoggedOut() {

    }

    override fun onLoginFailed(throwable: Throwable) {
        Log.e(TAG, "onLoginFailed: " + throwable.message)
        authSpotify()
    }

    override fun onTemporaryError() {
        Log.e(TAG, "onTemporaryError: " + System.currentTimeMillis() + "")
        Log.d(TAG, "retrying to auth..")
        authSpotify()
    }

    override fun onConnectionMessage(s: String) {

    }

    override fun onPlaybackEvent(eventType: PlayerNotificationCallback.EventType, playerState: PlayerState) {

    }

    override fun onPlaybackError(errorType: PlayerNotificationCallback.ErrorType, s: String) {

    }

    inner class Task : AsyncTask<String, Void, String>() {
        override fun doInBackground(vararg params: String): String {
            println("start my task")
            var result: String
            try {
                val ipAddr = InetAddress.getByName(params[0])
                if (!ipAddr.equals("") ) {
                    result = params[0] + "_yes"
                } else {
                    result = params[0] + "_no"
                }
            } catch (e: Exception) {
                result = params[0] + "_no"
            }

            return result
        }

        public override fun onPostExecute(result: String) {
            when (result) {
                "spotify.com_yes" -> {
                    println("yes spotify")
                    authSpotify()
                }
                "spotify.com_no" -> {
                    println("no spotify")
                    ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                    systemRing()
                }
                "google.com_yes" -> {
                    println("yes google")
                    val intent = Intent(this@AlarmFiredActivity, RecaptchaActivity::class.java)
                    intent.putExtra("label", label)
                    startActivityForResult(intent, AlarmListActivity.VERIFY_CAPTCHA_INTENT_REQUEST_CODE)
                }
                "google.com_no" -> {
                    println("no google")
                    // Default
                    val textView = findViewById<TextView>(R.id.defaultMsg)
                    textView.text = label

                    val dismissBtn = findViewById<View>(R.id.button_dismiss) as Button
                    dismissBtn.setOnClickListener { finishActivity() }
                }
                else -> {
                    throw NumberFormatException()
                }
            }
        }
    }

    companion object {
        //    private AlarmDbHelper dbHelper;
        private val TAG = "AlarmFiredActivity"

        private val REQUEST_CODE = 1337
    }
}
