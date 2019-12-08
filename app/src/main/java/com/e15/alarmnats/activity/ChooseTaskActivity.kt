package com.e15.alarmnats.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View

import com.e15.alarmnats.R

class ChooseTaskActivity : AppCompatActivity() {
    internal lateinit var returnIntent: Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_task)
        title = "Choose Task"
        returnIntent = Intent()
    }

    fun chooseDefault(view: View) {
        returnIntent.putExtra("task", getString(R.string.default_question))
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }

    fun chooseRecaptcha(view: View) {
        returnIntent.putExtra("task", getString(R.string.recaptcha_question))
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }

    fun chooseQR(view: View) {
        returnIntent.putExtra("task", getString(R.string.qr_question))
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }

    fun chooseMath(view: View) {
        returnIntent.putExtra("task", getString(R.string.math_question))
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }
}
