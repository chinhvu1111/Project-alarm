package com.e15.alarmnats.activity

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.core.view.isVisible
import com.e15.alarmnats.Database.ReminderDatabase
import com.e15.alarmnats.Main_AlarmActivity
import com.e15.alarmnats.Model.Category
import com.e15.alarmnats.Model.Event
import com.e15.alarmnats.Model.EventFb
import com.e15.alarmnats.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.FirebaseError
import com.google.firebase.auth.*
import com.google.firebase.database.*

class LoginActivity : AppCompatActivity() {

    lateinit var lnEmail: RelativeLayout

    lateinit var auth: FirebaseAuth

    lateinit var inputEmail: EditText

    lateinit var inPassword: EditText

    lateinit var btnLogin: Button

    lateinit var tvSignup:TextView

    lateinit var firebaseDatabase: FirebaseDatabase

    lateinit var eventDatabase: DatabaseReference

    lateinit var categoryDatabase: DatabaseReference

    lateinit var userDatabase: DatabaseReference

    lateinit var dbHandler: ReminderDatabase

    lateinit var lnLoadProgressBar:RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth= FirebaseAuth.getInstance()

        dbHandler= ReminderDatabase(applicationContext)

        firebaseDatabase = FirebaseDatabase.getInstance()

        eventDatabase = firebaseDatabase.getReference("Events")

        categoryDatabase = firebaseDatabase.getReference("Category")

        userDatabase = firebaseDatabase.getReference("User")

        lnEmail = findViewById(R.id.lnEmail)

        inputEmail = findViewById(R.id.inputEmail)

        inPassword = findViewById(R.id.inPassword)

        btnLogin = findViewById(R.id.btnLogin)

        tvSignup=findViewById(R.id.tvSignup)

        lnLoadProgressBar=findViewById(R.id.lnLoadProgressBarLogin)

        lnLoadProgressBar.isVisible=false

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

                    lnLoadProgressBar.isVisible=true

                    auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(object : OnCompleteListener<AuthResult> {
                        override fun onComplete(p0: Task<AuthResult>) {

                            if (p0.isSuccessful) {

                                var user = auth.currentUser

                                var intent = Intent(this@LoginActivity, Main_AlarmActivity::class.java)

                                //adding db from Realtime database
                                addingDb(user!!)

                                lnLoadProgressBar.isVisible=false

                                var editor=applicationContext.getSharedPreferences("accountLogin",Activity.MODE_PRIVATE).edit()

                                editor.putString("username",user!!.displayName)

                                editor.putString("Email",user.email)

                                editor.commit()
//                                intent.putExtra("username", user!!.displayName)

                                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)

                                Toast.makeText(applicationContext, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show()

                                startActivity(intent)

                            } else {

                                var errorCode=(p0.exception!! as FirebaseAuthException).errorCode

                                when(errorCode){
                                    "ERROR_INVALID_EMAIL"->{

                                        lnLoadProgressBar.isVisible=false

                                        Toast.makeText(applicationContext, "Sai tài khoản gmail!", Toast.LENGTH_SHORT).show()

                                        inputEmail.requestFocus()

                                    }
                                    "ERROR_WRONG_PASSWORD"->{

                                        lnLoadProgressBar.isVisible=false

                                        Toast.makeText(applicationContext, "Sai mật khẩu!", Toast.LENGTH_SHORT).show()

                                        inPassword.requestFocus()

                                    }
                                }

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

    fun addingDb(currentUser:FirebaseUser){

        var sharedPreferences=applicationContext.getSharedPreferences("checkingCreateDb", Context.MODE_PRIVATE)

        var sharedPreferencesAccount=applicationContext.getSharedPreferences("accountLogin", Context.MODE_PRIVATE)

        var beforeEmail=sharedPreferencesAccount.getString("Email","")

        var isAddingDbFromRealtimeDbFirst=sharedPreferences.getBoolean("isAddingDbfromRealtime",false)

        if (!isAddingDbFromRealtimeDbFirst) {

            userDatabase.orderByChild("email").equalTo(currentUser!!.email).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {

                    //Drop db of (current User)/(before user) then adding new database of current User
                    dbHandler.dropDb()

                    var sharedPreferences=applicationContext.getSharedPreferences("checkingCreateDb", Context.MODE_PRIVATE).edit()

                    sharedPreferences.putBoolean("isAddingDbfromRealtime",true)

                    sharedPreferences.commit()

                    var dataUser=p0.children.iterator().next()

                    var idUser=dataUser.key.toString()

                    categoryDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {

                        }

                        override fun onDataChange(p0: DataSnapshot) {

                            for (ca in p0.children) {

                                var category = ca.getValue(Category::class.java)

                                if(category!!.hashIdUser.equals(idUser))dbHandler.createNewCategory(category!!)

                            }

                        }
                    })

                    eventDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {

                        }

                        override fun onDataChange(p0: DataSnapshot) {

                            for (e in p0.children) {

                                var eventFb = e.getValue(EventFb::class.java)

                                var event = Event(eventFb!!, eventFb!!.levelRecusion)

                                if(event.hashIdUser.equals(idUser))dbHandler.createNewEvent(event)

                            }

                        }

                    })

                    Toast.makeText(applicationContext, "Đồng bộ cơ sở dữ liệu thành công!", Toast.LENGTH_SHORT).show()

                }

            })

        }else if(!currentUser.email!!.equals(beforeEmail)){

            userDatabase.orderByChild("email").equalTo(currentUser!!.email).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {

                    //Drop db of (current User)/(before user) then adding new database of current User
                    dbHandler.dropDb()

                    var dataUser=p0.children.iterator().next()

                    var idUser=dataUser.key.toString()

                    var editor=applicationContext.getSharedPreferences("CurrentUserInfo", Context.MODE_PRIVATE).edit()

                    editor.putString("hashidUser",idUser)

                    editor.commit()

                    categoryDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {

                        }

                        override fun onDataChange(p0: DataSnapshot) {

                            for (ca in p0.children) {

                                var category = ca.getValue(Category::class.java)

                                if(category!!.hashIdUser.equals(idUser))dbHandler.createNewCategory(category!!)

                            }

                        }
                    })

                    eventDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {

                        }

                        override fun onDataChange(p0: DataSnapshot) {

                            for (e in p0.children) {

                                var eventFb = e.getValue(EventFb::class.java)

                                var event = Event(eventFb!!, eventFb!!.levelRecusion)

                                if(event.hashIdUser.equals(idUser))dbHandler.createNewEvent(event)

                            }

                        }

                    })

                    Toast.makeText(applicationContext, "Đồng bộ cơ sở dữ liệu thành công!", Toast.LENGTH_SHORT).show()

                }

            })

        }
    }

}
