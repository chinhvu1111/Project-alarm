package com.e15.alarmnats.activity

import android.app.Activity
import android.content.Context
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
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.mikhaellopez.circularimageview.CircularImageView
import java.util.regex.Pattern
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException

class SignUpActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var btnSignUp: Button

    lateinit var inputEmail: EditText

    lateinit var inPassword: EditText

    lateinit var edtDescription:EditText

    lateinit var edtReTypePassword:EditText

    lateinit var edtUsername:EditText

    lateinit var imgAddAvatar:CircularImageView

    lateinit var auth: FirebaseAuth

    lateinit var oldauth:FirebaseAuth

    lateinit var userDatabase:DatabaseReference

    lateinit var firebaseDatabase:FirebaseDatabase

    lateinit var currentUser:FirebaseUser

    var oldEmail:String?=""

    var oldPassword:String=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        btnSignUp = findViewById(R.id.btnSignUp)

        btnSignUp.setOnClickListener(this)

        inputEmail = findViewById(R.id.inputEmailSn)

        inPassword = findViewById(R.id.inPasswordSn)

        imgAddAvatar=findViewById(R.id.imgAddAvatar)

        edtUsername=findViewById(R.id.edtUsername)

        edtReTypePassword=findViewById(R.id.edtReTypePassword)

        edtDescription=findViewById(R.id.edtDescription)

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

        edtUsername.setOnTouchListener(object:View.OnTouchListener{
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {

                if(edtUsername.text.toString().equals("Không được để trống Email!")){

                    edtUsername.setTextColor(resources.getColor(R.color.white))

                    edtUsername.setText("")

                }

                //If return false then we can touch to enter text
                return false

            }
        })

        auth = FirebaseAuth.getInstance()

        //This is used to sign in immediately
        oldauth=FirebaseAuth.getInstance()

//        currentUser=oldauth.currentUser!!
//
//        var query=userDatabase.orderByChild("email").equalTo(currentUser!!.email)
//
//        oldEmail=currentUser?.email
//
//        query.addListenerForSingleValueEvent(object:ValueEventListener{
//            override fun onCancelled(p0: DatabaseError) {
//
//            }
//
//            override fun onDataChange(p0: DataSnapshot) {
//
//                if(p0.hasChildren()){
//
//                    var data=p0.children.iterator().next()
//
//                    oldPassword=data.child("password").value.toString()
//
//                }
//
//            }
//
//        })

        imgAddAvatar.setOnClickListener(this)

    }

    override fun onClick(v: View?) {

        when (v!!.id) {

            R.id.btnSignUp -> {

                var email = inputEmail.text.toString()

                var password = inPassword.text.toString()

                var username=edtUsername.text.toString()

                var reTypePassword=edtReTypePassword.text.toString()

                var description=edtDescription.text.toString()

                if (email.isEmpty()) {

                    inputEmail.setTextColor(resources.getColor(R.color.red))

                    inputEmail.setText("Không được để trống Email!")

                    inputEmail.requestFocus()

                } else if (password.isEmpty()) {

                    inPassword.setTextColor(resources.getColor(R.color.red))

                    inPassword.inputType= InputType.TYPE_CLASS_TEXT

                    inPassword.setText("Không được để trống Password!")

                    inPassword.requestFocus()

                }else if(username.isEmpty()){

                    Toast.makeText(applicationContext, "Tên người dùng trống xin mời nhập lại!", Toast.LENGTH_SHORT).show()

                    edtUsername.setTextColor(resources.getColor(R.color.red))

                    edtUsername.setText("Không được để trống Username!")

                    edtUsername.requestFocus()

                } else if (password.length<6) {

                    Toast.makeText(applicationContext, "Mật khẩu phải trên 6 ký tự, yêu cầu nhập lại!", Toast.LENGTH_SHORT).show()

                    return

                } else if(!reTypePassword.equals(password)){

                    Toast.makeText(applicationContext, "Mật khẩu không khớp, yêu cầu nhập lại!", Toast.LENGTH_SHORT).show()

                    return

                } else if (!password.isEmpty() && !email.isEmpty()) {

                    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(object : OnCompleteListener<AuthResult> {
                        override fun onComplete(p0: Task<AuthResult>) {
                            if (p0.isSuccessful) {

                                //Generated key of new user
                                var idUser=userDatabase.push().key

                                var user=User(username,description,idUser!!,email,password)

                                userDatabase.child(idUser).setValue(user)

                                Toast.makeText(applicationContext, "Tạo tài khoản thành công!", Toast.LENGTH_SHORT).show()

                                auth.signOut()

                                var editor=applicationContext.getSharedPreferences("accountLogin",Activity.MODE_PRIVATE).edit()

                                editor.putString("username","")

                                editor.putString("Email","")

                                //Delete adding realtime database to SQLite database
                                var sharedPreferences=applicationContext.getSharedPreferences("checkingCreateDb", Context.MODE_PRIVATE).edit()

                                sharedPreferences.putBoolean("isAddingDbfromRealtime",false)

//                                //Sign in immediately
//                                oldauth.signInWithEmailAndPassword(oldEmail!!,oldPassword).addOnCompleteListener(object:OnCompleteListener<AuthResult>{
//                                    override fun onComplete(p0: Task<AuthResult>) {
//
//                                        if(p0.isSuccessful){
//
//                                            Toast.makeText(applicationContext,"Đăng nhập lại thành công",Toast.LENGTH_SHORT).show()
//
//                                        }
//
//                                    }
//
//                                })

                                var intent = Intent(this@SignUpActivity, LoginActivity::class.java)

                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)

                                startActivity(intent)

                            } else {

                                try {
                                    throw p0.exception!!
                                } catch (e: FirebaseAuthWeakPasswordException) {

                                    Toast.makeText(applicationContext,"Mật khẩu yếu mời nhập lại",Toast.LENGTH_SHORT).show()

                                    inPassword.requestFocus()

                                } catch (e: FirebaseAuthInvalidCredentialsException) {

                                    Toast.makeText(applicationContext,"Tài khoản gmail nhập sai định dạng",Toast.LENGTH_SHORT).show()

                                    inputEmail.requestFocus()

                                } catch (e: FirebaseAuthUserCollisionException) {

                                    Toast.makeText(applicationContext,"Tài khoản đã tồn tại mời nhập lại",Toast.LENGTH_SHORT).show()

                                    inputEmail.requestFocus()

                                } catch (e: Exception) {
                                    Toast.makeText(applicationContext,"Tài khoản không tồn tại/Sai mật khẩu mời nhập lại",Toast.LENGTH_SHORT).show()

                                    inputEmail.requestFocus()

                                }


                            }
                        }
                    })
                }

            }

            R.id.imgAddAvatar->{

            }

        }

    }

}

