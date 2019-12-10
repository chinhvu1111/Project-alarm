package com.e15.alarmnats.adapter

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.e15.alarmnats.Main_AlarmActivity
import com.e15.alarmnats.Model.SettingItem
import com.e15.alarmnats.R
import com.e15.alarmnats.activity.ColorSettingActivity
import com.e15.alarmnats.activity.LoginActivity
import com.e15.alarmnats.activity.ProfileUserActivity
import com.e15.alarmnats.viewholder.ViewHolderSetting
import com.google.firebase.auth.FirebaseAuth
import java.util.*

class Setting_general_adapter: RecyclerView.Adapter<ViewHolderSetting> {
    lateinit var listItemSetting: ArrayList<SettingItem>

    lateinit var context: Context

    var hour:Int = 0

    var minute:Int = 0

    var year:Int = 0

    var month:Int = 0

    var day:Int = 0

    lateinit var auth: FirebaseAuth

    lateinit var activity: Activity

    constructor(context: Context) : super() {
        this.context = context
    }

    constructor(context: Context, activity: Activity):super(){
        this.context=context

        this.activity=activity

        var calendar= Calendar.getInstance()

        hour=calendar.get(Calendar.HOUR_OF_DAY)

        minute=calendar.get(Calendar.MINUTE)

        year=calendar.get(Calendar.YEAR)

        month=calendar.get(Calendar.MONTH)

        day=calendar.get(Calendar.DAY_OF_MONTH)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderSetting {

        var view = LayoutInflater.from(parent.context).inflate(R.layout.item_setting, parent, false)

        var viewHolderSetting = ViewHolderSetting(view)

        return viewHolderSetting

    }

    override fun getItemCount(): Int {

        return listItemSetting.size

    }

    override fun onBindViewHolder(holder: ViewHolderSetting, position: Int) {

        auth=FirebaseAuth.getInstance()

        var holderSettingItem = holder as ViewHolderSetting

        holderSettingItem.imageIcon.setImageResource(listItemSetting.get(position).iconId)

        holderSettingItem.tvtitleSetting.setText(listItemSetting.get(position).title)

        holderSettingItem.tvDetailSetting.setText(listItemSetting.get(position).detailTitle)


        holderSettingItem.rootView.setBackgroundColor(context.resources.getColor(listItemSetting.get(position).backGround))


        when (listItemSetting.get(position).id) {

            1->{
                holderSettingItem.rootView.setOnClickListener(object : View.OnClickListener {

                    override fun onClick(v: View?) {

//                        var intent: Intent = Intent(context, LoginActivity::class.java)
//
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//
//                        context.startActivity(intent)

                    }
                })
            }

            2->{
                holderSettingItem.rootView.setOnClickListener(object : View.OnClickListener {

                    override fun onClick(v: View?) {

                        var currentUser=auth.currentUser

                        var intent: Intent = Intent(context, LoginActivity::class.java)

                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

                        context.startActivity(intent)


                    }
                })
            }

            3->{
                holderSettingItem.rootView.setOnClickListener(object : View.OnClickListener {

                    override fun onClick(v: View?) {

                        var intent: Intent = Intent(context, ProfileUserActivity::class.java)

                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

                        context.startActivity(intent)

                    }
                })
            }

            4->{
                holderSettingItem.rootView.setOnClickListener(object : View.OnClickListener {

                    override fun onClick(v: View?) {

                        var mFireBaseUser=auth.currentUser

                        var editor=context.getSharedPreferences("accountLogin",Activity.MODE_PRIVATE).edit()

                        editor.putString("username","")

                        editor.putString("Email","")

                        editor.commit()

                        if(mFireBaseUser!=null){

                            FirebaseAuth.getInstance().signOut()

                            var intent: Intent = Intent(context, Main_AlarmActivity::class.java)

                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

                            context.startActivity(intent)

                            Toast.makeText(context,"Đăng xuất thành công!",Toast.LENGTH_SHORT).show()

                        }else{

                            var intent: Intent = Intent(context, LoginActivity::class.java)

                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

                            context.startActivity(intent)

                        }


                    }
                })
            }

        }

    }
}