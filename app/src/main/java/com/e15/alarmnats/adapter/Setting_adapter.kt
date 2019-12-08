package com.e15.alarmnats.adapter

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.e15.alarmnats.Model.SettingItem
import com.e15.alarmnats.R
import com.e15.alarmnats.activity.ColorSettingActivity
import com.e15.alarmnats.viewholder.ViewHolderSetting
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import java.util.*

class Setting_adapter : RecyclerView.Adapter<ViewHolderSetting> , DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener{
    lateinit var listItemSetting: ArrayList<SettingItem>

    lateinit var context: Context

    var hour:Int = 0

    var minute:Int = 0

    var year:Int = 0

    var month:Int = 0

    var day:Int = 0

    lateinit var activity:Activity

    constructor(context: Context) : super() {
        this.context = context
    }

    constructor(context: Context,activity: Activity):super(){
        this.context=context

        this.activity=activity

        var calendar=Calendar.getInstance()

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

        var holderSettingItem = holder as ViewHolderSetting

        holderSettingItem.imageIcon.setImageResource(listItemSetting.get(position).iconId)

        holderSettingItem.tvtitleSetting.setText(listItemSetting.get(position).title)

        holderSettingItem.tvDetailSetting.setText(listItemSetting.get(position).detailTitle)


        holderSettingItem.rootView.setBackgroundColor(context.resources.getColor(listItemSetting.get(position).backGround))


        when (listItemSetting.get(position).id) {

            1->{
                holderSettingItem.rootView.setOnClickListener(object : View.OnClickListener {

                    override fun onClick(v: View?) {

                        var intent: Intent = Intent(context, ColorSettingActivity::class.java)

                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

                        context.startActivity(intent)

                    }
                })
            }
            2->{
                holderSettingItem.rootView.setOnClickListener(object :View.OnClickListener{
                    override fun onClick(v: View?) {

                        var builder:AlertDialog.Builder=AlertDialog.Builder(activity,R.style.MyDialogTheme)

                        builder.setTitle("Tùy chọn lọc tác vụ")

                        var choices= arrayOf("Lọc dựa trên ngày tạo tác vụ","Lọc dựa trên thời gian tạo tác vụ","Lọc từ thời điểm hiện tại")

                        builder.setItems(choices,object:DialogInterface.OnClickListener{
                            override fun onClick(dialog: DialogInterface?, which: Int) {

                                when(which){

                                    0->{

                                        dateSet()

                                    }

                                    1->{

                                        timeSet()

                                    }

                                    2->{

                                        var editor=context.getSharedPreferences("CalendarPre",Activity.MODE_PRIVATE).edit()

                                        var calendar=Calendar.getInstance()

                                        var hour=calendar.get(Calendar.HOUR_OF_DAY)

                                        var minute=calendar.get(Calendar.MINUTE)

                                        //Apply for time filter option
                                        editor.putInt("HourPre",hour)

                                        editor.putInt("MinutePre",minute)

                                        //Clear for date filter option

                                        editor.putInt("YearPre",-1)

                                        editor.putInt("MonthPre",-1)

                                        editor.putInt("DayPre",-1)

                                        editor.putInt("choice",1)

                                        editor.commit()

                                    }

                                }

                            }
                        })

                        var dialog:AlertDialog=builder.create()

                        dialog.show()

                    }
                })
            }

        }

    }

    fun timeSet(){

        val timePickerDialog=TimePickerDialog.newInstance(this, hour,minute,false)

        timePickerDialog.isThemeDark=true

        timePickerDialog.show(activity.fragmentManager,"TimePickerDialog")

    }

    fun dateSet(){

        var datePickerDialog=DatePickerDialog.newInstance(this,year,month,day)

        datePickerDialog.isThemeDark=true

        datePickerDialog.show(activity.fragmentManager,"DatePickerDialog")

    }

    override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {

        this.year=year

        this.month=monthOfYear+1

        this.day=dayOfMonth

        var editor=context.getSharedPreferences("CalendarPre",Activity.MODE_PRIVATE).edit()

        //Apply for date filter option
        editor.putInt("YearPre",year)

        editor.putInt("MonthPre",monthOfYear+1)

        editor.putInt("DayPre",dayOfMonth)

        //Clear for time filter option
        editor.putInt("HourPre",-1)

        editor.putInt("MinutePre",-1)

        editor.putInt("choice",0)

        editor.commit()

    }

    override fun onTimeSet(view: RadialPickerLayout?, hourOfDay: Int, minute: Int, second: Int) {

        this.hour=hourOfDay

        this.minute=minute

        var editor=context.getSharedPreferences("CalendarPre",Activity.MODE_PRIVATE).edit()

        //Apply for time filter option
        editor.putInt("HourPre",hourOfDay+1)

        editor.putInt("MinutePre",minute)

        //Clear for date filter option

        editor.putInt("YearPre",-1)

        editor.putInt("MonthPre",-1)

        editor.putInt("DayPre",-1)

        editor.putInt("choice",1)

        editor.commit()

    }
}
