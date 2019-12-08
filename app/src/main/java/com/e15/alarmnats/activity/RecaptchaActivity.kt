package com.e15.alarmnats.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.TextView

import com.e15.alarmnats.R
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.safetynet.SafetyNet

class RecaptchaActivity : AppCompatActivity() {
    private var recaptchaMsg: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recaptcha)
        recaptchaMsg = findViewById(R.id.recaptchaMsq)
        recaptchaMsg!!.text = intent.extras!!.getString("label")
    }

    fun onClickBtnVerify(view: View) {
        SafetyNet.getClient(this).verifyWithRecaptcha(SAFETY_NET_API_SITE_KEY)
                .addOnSuccessListener(this) {
                    Log.d(TAG, "onSuccess")

                    val returnIntent = Intent()
                    setResult(Activity.RESULT_OK, returnIntent)
                    finish()
                }
                .addOnFailureListener(this) { e ->
                    if (e is ApiException) {
                        Log.d(TAG, "Error message: " + CommonStatusCodes.getStatusCodeString(e.statusCode))
                    } else {
                        Log.d(TAG, "Unknown type of error: " + e.message)
                    }
                }
    }

    companion object {
        private val TAG = "RecaptchaActivity"
        private val SAFETY_NET_API_SITE_KEY = "6LfH96IUAAAAAIZkXrkY_AScHlGGdnkhSBOqcIKZ"
    }
}
