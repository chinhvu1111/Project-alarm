package com.e15.alarmnats.activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.e15.alarmnats.Main_AlarmActivity
import com.e15.alarmnats.Model.User
import com.e15.alarmnats.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignUpActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var btnSignUp: Button

    lateinit var inputEmail: EditText

    lateinit var inPassword: EditText

    lateinit var auth: FirebaseAuth

    lateinit var userDatabase:DatabaseReference

    lateinit var firebaseDatabase:FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        btnSignUp = findViewById(R.id.btnSignUp)

        btnSignUp.setOnClickListener(this)

        inputEmail = findViewById(R.id.inputEmailSn)

        inPassword = findViewById(R.id.inPasswordSn)

        firebaseDatabase= FirebaseDatabase.getInstance()

        userDatabase=firebaseDatabase.getReference("User")

        inPassword.setOnTouchListener(object:View.OnTouchListener{
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {

                if(inPassword.text.toString().equals("Không được để trống Password!")){

                    inPassword.setTextColor(resources.getColor(R.color.white))

                    //Changing the InputType
                    inPassword.transformationMethod=PasswordTransformationMethod.getInstance()

                    inPassword.setText("")

                }

                //If return false then we can touch to enter text
                return false

            }
        })

        inputEmail.setOnTouchListener(object:View.OnTouchListener{
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {

                if(inputEmail.text.toString().equals("Không được để trống Email!")){

                    inputEmail.setTextColor(resources.getColor(R.color.white))

                    inputEmail.setText("")

                }

                //If return false then we can touch to enter text
                return false

            }
        })

        auth = FirebaseAuth.getInstance()

    }

    override fun onClick(v: View?) {

        when (v!!.id) {

            R.id.btnSignUp -> {

                var email = inputEmail.text.toString()

                var password = inPassword.text.toString()

                if (email.isEmpty()) {

                    inputEmail.setTextColor(resources.getColor(R.color.red))

                    inputEmail.setText("Không được để trống Email!")

                    inputEmail.requestFocus()

                } else if (password.isEmpty()) {

                    inPassword.setTextColor(resources.getColor(R.color.red))

                    inPassword.inputType= InputType.TYPE_CLASS_TEXT

                    inPassword.setText("Không được để trống Password!")

                    inPassword.requestFocus()

                } else if (password.length<6) {

                    Toast.makeText(applicationContext, "Mật khẩu phải trên 6 ký tự yêu cầu nhập lại!", Toast.LENGTH_SHORT).show()

                } else if (!password.isEmpty() && !email.isEmpty()) {

                    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(object : OnCompleteListener<AuthResult> {
                        override fun onComplete(p0: Task<AuthResult>) {
                            if (p0.isSuccessful) {

                                var idUser=userDatabase.push().key

                                var user=User(idUser!!,email)

                                userDatabase.child(idUser).setValue(user)

                                Toast.makeText(applicationContext, "Tạo tài khoản thành công!", Toast.LENGTH_SHORT).show()

                                var intent = Intent(this@SignUpActivity, LoginActivity::class.java)

                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)

                                startActivity(intent)

                            } else {

                                Toast.makeText(applicationContext, "Tạo tài khoản thất bại!", Toast.LENGTH_SHORT).show()

                            }
                        }
                    })
                }

            }

        }

    }

}

