package com.e15.alarmnats.activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

import com.e15.alarmnats.R

import java.util.Random

class MathTestActivity : AppCompatActivity() {
    internal lateinit var math_answer: EditText
    internal lateinit var math_question: TextView
    internal lateinit var mathMsg: TextView
    internal lateinit var button: Button
    internal var x = 0
    internal var y = 0
    internal var z = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_math_test)

        math_answer = findViewById(R.id.math_answer)
        math_question = findViewById(R.id.math_question)
        button = findViewById(R.id.button)
        mathMsg = findViewById(R.id.mathMsg)

        mathMsg.text = intent.extras!!.getString("label")

        val randomGenerator = Random()
        x = randomGenerator.nextInt(500) + 100
        y = randomGenerator.nextInt(500) + 100
        z = randomGenerator.nextInt(5000) + 100

        math_question.text = "$x x $y + $z = "
    }

    fun submitBtnClick(view: View) {
        try {
            val user_answer = Integer.parseInt(math_answer.text.toString())
            if (user_answer == x * y + z) {
                val returnIntent = Intent()
                setResult(Activity.RESULT_OK, returnIntent)
                finish()
            } else {
                throw NumberFormatException()
            }
        } catch (e: NumberFormatException) {
            Toast.makeText(this, "Wrong answer!!", Toast.LENGTH_SHORT).show()
        }

    }

}
