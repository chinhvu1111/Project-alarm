package com.e15.alarmnats.activity

import android.app.Activity
import android.app.FragmentTransaction
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import com.e15.alarmnats.Model.AlarmItem
import com.e15.alarmnats.R
import com.e15.alarmnats.fragment.AddAlarmFragment
import com.spotify.sdk.android.authentication.AuthenticationClient
import com.spotify.sdk.android.authentication.AuthenticationRequest
import com.spotify.sdk.android.authentication.AuthenticationResponse

class SearchSongActivity : AppCompatActivity(), AddAlarmFragment.AddFragmentListener {

    override fun deleteClicked(item: AlarmItem?) {

    }

    override fun updateAlarm(oldAlarm: AlarmItem?, newAlarm: AlarmItem) {
    }

    private val CLIENT_ID = "848eaa9b8e114c4eb674481982b0ed5a"
    private val REDIRECT_URI = "alarmnats://callback"

    private var alarmItem: AlarmItem? = null
    private var token: String? = null

    fun authSpotify() {

        val builder = AuthenticationRequest.Builder(
                CLIENT_ID,
                AuthenticationResponse.Type.TOKEN,
                REDIRECT_URI)

        builder.setScopes(arrayOf("streaming"))
        val request = builder.build()

        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            val response = AuthenticationClient.getResponse(resultCode, intent)

            if (response.type == AuthenticationResponse.Type.TOKEN) {
                token = response.accessToken

                // saves auth token locally for use in AlarmActivity
                val prefs = this.getSharedPreferences(getString(R.string.tag_sharedprefs), Context.MODE_PRIVATE)
                prefs.edit()
                        .putString(getString(R.string.tag_sharedpref_token), token)
                        .apply()

            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_song)
        authSpotify()

        val addFragment = AddAlarmFragment()
        supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, addFragment)
                .setTransition(androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack("addFragment")
                .commit()
    }

    override fun saveClicked(item: AlarmItem) {
        alarmItem = item
        val intent = Intent()
        intent.putExtra("AlarmItem", alarmItem)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

//    override fun deleteClicked(item: AlarmItem) {
//
//    }
//
//    override fun updateAlarm(oldAlarm: AlarmItem, newAlarm: AlarmItem) {
//
//    }

    companion object {
        private val CONTENT_VIEW_ID = 10101010

        private val REQUEST_CODE = 1337
    }
}
