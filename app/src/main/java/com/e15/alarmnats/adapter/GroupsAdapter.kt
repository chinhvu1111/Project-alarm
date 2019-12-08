package com.e15.alarmnats.adapter

import android.Manifest
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.e15.alarmnats.Model.Group
import com.e15.alarmnats.R
import com.e15.alarmnats.viewholder.ViewHolderGroup
import java.io.InputStream
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.Activity
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.os.Build
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import android.os.Build.VERSION.SDK_INT
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class GroupsAdapter : RecyclerView.Adapter<ViewHolderGroup> {

    lateinit var auth:FirebaseAuth

    var context: Context

    var listGroup: ArrayList<Group>

    val MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123

    constructor(context: Context, listGroup: ArrayList<Group>) {

        this.context = context

        this.listGroup = listGroup

        auth=FirebaseAuth.getInstance()

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderGroup {

        var view = LayoutInflater.from(context).inflate(R.layout.item_group, parent, false)

        var viewHolderGroup = ViewHolderGroup(view)

        return viewHolderGroup

    }

    override fun getItemCount(): Int {
        return listGroup.size
    }

    fun showDialog(msg: String, context: Context,
                   permission: String) {
        val alertBuilder = AlertDialog.Builder(context)
        alertBuilder.setCancelable(true)
        alertBuilder.setTitle("Permission necessary")
        alertBuilder.setMessage("$msg permission is necessary")
        alertBuilder.setPositiveButton(android.R.string.yes,
                object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface, which: Int) {
                        ActivityCompat.requestPermissions(context as Activity,
                                arrayOf(permission),
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE)
                    }
                })
        val alert = alertBuilder.create()
        alert.show()
    }

    fun checkPermissionREAD_EXTERNAL_STORAGE(
            context: Context): Boolean {
        val currentAPIVersion = Build.VERSION.SDK_INT
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context,
                            Manifest.permission.READ_EXTERNAL_STORAGE) !== PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                                context as Activity,
                                Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    showDialog("External storage", context,
                            Manifest.permission.READ_EXTERNAL_STORAGE)

                } else {
                    ActivityCompat
                            .requestPermissions(
                                    context as Activity,
                                    arrayOf<String>(Manifest.permission.READ_EXTERNAL_STORAGE),
                                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE)
                }
                return false
            } else {
                return true
            }

        } else {
            return true
        }
    }

    override fun onBindViewHolder(holder: ViewHolderGroup, position: Int) {

        //set image for group
        var imgIconGroup = holder.imgIconGroup

        if (checkPermissionREAD_EXTERNAL_STORAGE(context)) {

//            var imageUri = Uri.parse(listGroup.get(position).path)
//
//            var imageStream = context.contentResolver.openInputStream(imageUri)
//
//            var yourAvatar = BitmapFactory.decodeStream(imageStream)
//
//            var bm = Bitmap.createScaledBitmap(yourAvatar, 100, 100, true)
//
//            imgIconGroup.setImageBitmap(bm)

        }

        //Set name of group
        var tvNameGroup = holder.tvNameGroup

        tvNameGroup.text = listGroup.get(position).name

        var tvNumberOfMembers = holder.tvNumberOfMembers

        tvNumberOfMembers.text = listGroup.get(position).numberOfMember.toString()

        var tvHour = holder.tvHour

        var btnJoin = holder.btnJoin

        btnJoin.setOnClickListener((object : View.OnClickListener {
            override fun onClick(v: View?) {

                AlertDialog.Builder(context, R.style.MyDialogTheme).setMessage("Bạn có muốn tham gia nhóm này không?").setPositiveButton("Có",
                        DialogInterface.OnClickListener { dialog, which ->

//                            Toast.makeText(context,auth.currentUser!!.uid,Toast.LENGTH_SHORT).show()

                            var currentUser=auth.currentUser

                            var firebaseDatabase=FirebaseDatabase.getInstance()

                            var userDatabase=firebaseDatabase.getReference("User")

                            //Get user has (current email)
                            var query=userDatabase.orderByChild("email").equalTo(currentUser!!.email)

                            var idCurrentUser:String=""

                            var idGroup=listGroup.get(position).hashId

                            //When data is changed
                            query.addListenerForSingleValueEvent(object :ValueEventListener{

                                override fun onDataChange(p0: DataSnapshot) {

                                    //This code is used to get (idCurrentUser)
                                    var groupDataSnapShot=p0.children.iterator().next()

                                    idCurrentUser=groupDataSnapShot.key!!

                                    //Start from path("/User")
                                    //When data changign then we update again
                                    //Adding (new group) for (current User)
                                    userDatabase.ref.child("/$idCurrentUser/Group/$idGroup").setValue(true)

                                    Toast.makeText(context,groupDataSnapShot.key,Toast.LENGTH_SHORT).show()

                                    var groupDatabase=firebaseDatabase.getReference("Group")
//
                                    //Adding new User for (current position of listGroups)
                                    groupDatabase.ref.child("/$idGroup/User/$idCurrentUser").setValue(true)

//                                    userDatabase.ref.child("$idCurrentUser/Group/")

                                }

                                override fun onCancelled(p0: DatabaseError) {

                                }

                            })

                            //Warning:Fail
//                            var querySecond=userDatabase.orderByChild("$idCurrentUser").equalTo("true")
//
//                            querySecond.addValueEventListener(object:ValueEventListener{
//                                override fun onDataChange(p0: DataSnapshot) {
//
//                                    var groupDatabase=firebaseDatabase.getReference("Group")
//
//                                    //Adding new User for (current position of listGroups)
//                                    groupDatabase.ref.child("/$idGroup/User/$idCurrentUser").setValue(true)
//
//                                }
//
//                                override fun onCancelled(p0: DatabaseError) {
//
//                                }
//
//                            })


                }).setNegativeButton("Không", null).show()

            }
        }))

    }

}