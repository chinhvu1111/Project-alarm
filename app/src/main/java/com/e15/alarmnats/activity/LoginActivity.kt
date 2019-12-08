package com.e15.alarmnats.activity

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.e15.alarmnats.Main_AlarmActivity
import com.e15.alarmnats.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    lateinit var lnEmail: RelativeLayout

    lateinit var auth: FirebaseAuth

    lateinit var inputEmail: EditText

    lateinit var inPassword: EditText

    lateinit var btnLogin: Button

    lateinit var tvSignup:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        lnEmail = findViewById(R.id.lnEmail)

        inputEmail = findViewById(R.id.inputEmail)

        inPassword = findViewById(R.id.inPassword)

        btnLogin = findViewById(R.id.btnLogin)

        tvSignup=findViewById(R.id.tvSignup)

    }

    override fun onStart() {
        super.onStart()

        auth = FirebaseAuth.getInstance()

        btnLogin.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {

                var email = inputEmail.text.toString()

                var password = inPassword.text.toString()

                if (email.isEmpty()) {

                    inputEmail.setText("Không được để trống Email!")

                    inputEmail.requestFocus()

                } else if (password.isEmpty()) {

                    inPassword.setText("Không được để trống Password!")

                    inPassword.requestFocus()

                } else if (!password.isEmpty() && !email.isEmpty()) {

                    auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(object : OnCompleteListener<AuthResult> {
                        override fun onComplete(p0: Task<AuthResult>) {

                            if (p0.isSuccessful) {

                                var user = auth.currentUser

                                var intent = Intent(this@LoginActivity, Main_AlarmActivity::class.java)

                                var editor=applicationContext.getSharedPreferences("accountLogin",Activity.MODE_PRIVATE).edit()

                                editor.putString("username",user!!.displayName)

                                editor.putString("Email",user.email)

                                editor.commit()
//                                intent.putExtra("username", user!!.displayName)

                                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)

                                Toast.makeText(applicationContext, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show()

                                startActivity(intent)

                            } else {

                                Toast.makeText(applicationContext, "Đăng nhập thất bại!", Toast.LENGTH_SHORT).show()

                            }
                        }
                    })
                }

            }

        })

        tvSignup.setOnClickListener(object :View.OnClickListener{
            override fun onClick(v: View?) {

                var intent=Intent(this@LoginActivity,SignUpActivity::class.java)

                intent.flags=Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP

                startActivity(intent)

            }
        })

    }

}
