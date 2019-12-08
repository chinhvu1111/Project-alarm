package com.e15.alarmnats.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.core.view.isVisible
import com.e15.alarmnats.Model.Group
import com.e15.alarmnats.Model.User
import com.e15.alarmnats.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_creating_group.*
import java.io.File
import java.io.FileInputStream

class CreatingGroupActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var edtNameGroup: EditText

    lateinit var btnAddingGroup: Button

    lateinit var lnCamera: RelativeLayout

    lateinit var imgPicture: ImageView

    lateinit var imgChoosingPicture: ImageView

    lateinit var lnLoadProgressBarAddingGroup: RelativeLayout

    lateinit var firebase: FirebaseDatabase

    lateinit var mHandler: Handler

    lateinit var path: String

    lateinit var nameGroup: String

    lateinit var auth: FirebaseAuth

    var PICK_IMAGE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_creating_group)

        edtNameGroup = findViewById(R.id.edtNameGroup)

        btnAddingGroup = findViewById(R.id.btnAddingGroup)

        lnCamera = findViewById(R.id.lnCamera)

        imgPicture = findViewById(R.id.imgPicture)

        imgChoosingPicture = findViewById(R.id.imgChoosingPicture)

        lnLoadProgressBarAddingGroup = findViewById(R.id.lnLoadProgressBarAddingGroup)

        lnLoadProgressBarAddingGroup.visibility = View.GONE

        imgChoosingPicture.isVisible = true

        btnAddingGroup.setOnClickListener(this)

        lnCamera.setOnClickListener(this)

        firebase = FirebaseDatabase.getInstance()

        auth = FirebaseAuth.getInstance()

        path = ""

        nameGroup = ""

    }

    override fun onClick(v: View?) {

        var name = edtNameGroup.text.toString()

        if (name.length > 20) {

            Toast.makeText(applicationContext, "Tên nhóm quá dài xin mời nhập lại!", Toast.LENGTH_SHORT).show()

            return;

        }

        when (v!!.id) {

            R.id.lnCamera -> {

                var intent = Intent()

                intent.setType("image/*")

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

                    intent = Intent(Intent.ACTION_OPEN_DOCUMENT, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

                } else {

                    intent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

                }


                //Builds a new ACTION_CHOOSER Intent that wraps the given target intent, also optionally supplying a title.
                // If the target intent has specified
                startActivityForResult(Intent.createChooser(intent, "Chọn ảnh"), PICK_IMAGE)

            }

            R.id.btnAddingGroup -> {

                nameGroup = edtNameGroup.text.toString()

                if (nameGroup.equals("")) {

                    Toast.makeText(applicationContext, "Tên group không được để trống!", Toast.LENGTH_SHORT).show()

                    return

                } else if (name.length > 20) {

                    Toast.makeText(applicationContext, "Tên group quá dài mời nhập lại!", Toast.LENGTH_SHORT).show()

                    return

                } else if (path.equals("")) {

                    Toast.makeText(applicationContext, "Hãy chọn hình đại diện cho nhóm!", Toast.LENGTH_SHORT).show()

                    return

                }

                lnLoadProgressBarAddingGroup.visibility = View.VISIBLE

                //Adding data into database
                addingDB()

                //listen to return value from addingDB
                listenerAddingDB()

            }

        }

    }

    fun addingDB() {

        Thread(Runnable {

            //Creating group
            var databaseGroup = firebase.getReference("Group")

            var idGroup = databaseGroup.push().key

            var group = Group(idGroup!!, nameGroup, 1, path)

            databaseGroup.child(idGroup!!).setValue(group)

            //Creating user and the group is nested in user
            var databaseUser = firebase.getReference("User")

            var idUser: String = ""

            var currentUser = auth.currentUser

            if (currentUser == null) {

                Toast.makeText(applicationContext, "Bạn chưa đăng nhập xin mời đăng nhập để tạo nhóm!", Toast.LENGTH_SHORT).show()

                return@Runnable

            }

            var queryForGettingUserId = databaseUser.orderByChild("email").equalTo(currentUser!!.email)

            queryForGettingUserId.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {

                    if (!p0.children.iterator().hasNext()) {

                        Toast.makeText(applicationContext, "Bạn chưa đăng nhập để xem danh sách nhóm", Toast.LENGTH_SHORT).show()

                        return

                    }

                    idUser = p0.children.iterator().next().key.toString()

                    var email = intent.getStringExtra("Email")

                    //Note:Path is arbitrary (id/user/ || /user (both methods) are same
                    //Create path child at the same references
                    databaseUser.ref.child("/$idUser/Group/$idGroup").setValue(true)

                    //This here relation is Many to Many
                    //We has two way to store value
                    //Updating list of user of group at the same time\
                    databaseGroup.ref.child("/$idGroup/User/$idUser").setValue(true)
//
//            var databaseGroupHasUsers=firebase.getReference("groupHasUsers")
//
//            databaseGroupHasUsers.child(idGroup).ref.child("/$idUser").setValue(true)

                    var message = Message()

                    message.what = 0

                    mHandler.sendMessage(message)

                }

            })

        }).start()

    }

    fun listenerAddingDB() {

        mHandler = object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {

                when (msg.what) {

                    0 -> {

                        lnLoadProgressBarAddingGroup.visibility = View.GONE

                        Toast.makeText(applicationContext, "Cập nhập xong cơ sở dữ liệu thời gian thực!", Toast.LENGTH_SHORT).show()

                        setResult(Activity.RESULT_OK)

                        finish()

                    }

                }

            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data!!.data != null) {

            var selectedImage = data!!.data

            path = selectedImage.toString()

            imgPicture.isVisible = false

            imgChoosingPicture.isVisible = true

            val imageStream = contentResolver.openInputStream(selectedImage)
            val yourSelectedImage = BitmapFactory.decodeStream(imageStream)

            //BitmapFactory
            //Creates (Bitmap objects) from various sources, including (files), (streams), and (byte-arrays).
            imgChoosingPicture.setImageBitmap(yourSelectedImage)

        }

    }

}
