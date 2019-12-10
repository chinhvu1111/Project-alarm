package com.e15.alarmnats.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.e15.alarmnats.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_sign_up.*

class ProfileUserActivity : AppCompatActivity(),View.OnClickListener {
    lateinit var edtUsernameProfile:EditText

    lateinit var edtDescriptionProfile:EditText

    lateinit var btnUpdate:Button

    lateinit var firebaseDatabase:FirebaseDatabase

    lateinit var auth:FirebaseAuth

    lateinit var userDatabase:DatabaseReference

    lateinit var currentUser:FirebaseUser

    lateinit var imgEditUserProfile:ImageView

    lateinit var imgEditIntroductionProfile:ImageView

    var currentUserId=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_user)

        edtUsernameProfile=findViewById(R.id.edtUsernameProfile)

        //Can't click
        edtUsernameProfile.isEnabled=false

        edtDescriptionProfile=findViewById(R.id.edtDescriptionProfile)

        //Can't click
        edtDescriptionProfile.isEnabled=false

        btnUpdate=findViewById(R.id.btnUpdate)

        btnUpdate.setOnClickListener(this)

        imgEditUserProfile=findViewById(R.id.imgEditUserProfile)

        imgEditIntroductionProfile=findViewById(R.id.imgEditIntroductionProfile)

        imgEditUserProfile.setOnClickListener(this)

        imgEditIntroductionProfile.setOnClickListener(this)

        firebaseDatabase= FirebaseDatabase.getInstance()

        auth=FirebaseAuth.getInstance()

        currentUser= auth.currentUser!!

        if(currentUser==null){

            var intent=Intent(this, LoginActivity::class.java)

            Toast.makeText(applicationContext,"Bạn chưa đăng nhập!",Toast.LENGTH_SHORT).show()

            startActivity(intent)

            finish()

            return

        }

        userDatabase=firebaseDatabase.getReference("User")

        var query=userDatabase.orderByChild("email").equalTo(currentUser!!.email)

        query.addValueEventListener(object:ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {

                if(p0.hasChildren()){

                    var data=p0.children.iterator().next()

                    ///Getting current user id
                    currentUserId=data.key.toString()

                    var username=data.child("username").value.toString()

                    var description=data.child("description").value.toString()

                    edtUsernameProfile.setText(username)

                    edtDescriptionProfile.setText(description)

                }

            }

        })

    }

    override fun onClick(v: View?) {

        when(v!!.id){

            R.id.imgEditUserProfile->{

                edtUsernameProfile.isEnabled=true

                edtUsernameProfile.requestFocus()

            }

            R.id.imgEditIntroductionProfile->{

                edtDescriptionProfile.isEnabled=true

                edtDescriptionProfile.requestFocus()

            }

            R.id.btnUpdate->{

                var newUsername=edtUsernameProfile.text.toString()

                var newDescription=edtDescriptionProfile.text.toString()

                if(newUsername.isEmpty()){

                    Toast.makeText(applicationContext,"Tên người dùng cập nhập không được để trống",Toast.LENGTH_SHORT).show()

                    return

                }

                userDatabase.child("$currentUserId/username").setValue(newUsername)

                userDatabase.child("$currentUserId/description").setValue(newDescription)

                Toast.makeText(applicationContext,"Cập nhập thành công",Toast.LENGTH_SHORT).show()

            }

        }

    }
}
