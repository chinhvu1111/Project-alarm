package com.e15.alarmnats

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.DrawableCompat
import com.e15.alarmnats.activity.*
import com.e15.alarmnats.activity.TaskManagementActivity

class Main_AlarmActivity : AppCompatActivity() {

    lateinit var layoutRemindItem: RelativeLayout;

    lateinit var layoutScheduleItem: RelativeLayout;

    lateinit var layoutTaskManagement: RelativeLayout;

    lateinit var layoutTaskAlarm: RelativeLayout;

    lateinit var layoutChooseMusics: RelativeLayout;

    lateinit var layoutPomodoro: RelativeLayout;

    lateinit var layoutSetting: RelativeLayout;

    lateinit var layoutGroup:RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_alarm)

        layoutRemindItem = findViewById(R.id.layoutRemindItem)

        layoutScheduleItem = findViewById(R.id.layoutScheduleItem)

        layoutTaskManagement = findViewById(R.id.layoutTaskManagement)

        layoutTaskAlarm = findViewById(R.id.layoutTaskAlarm)

        layoutChooseMusics = findViewById(R.id.layoutChooseMusics)

        layoutPomodoro = findViewById(R.id.layoutPomodoro)

        layoutSetting = findViewById(R.id.layoutSetting)

        layoutGroup=findViewById(R.id.layoutGroup)

        var unwrapperDrawable1 = AppCompatResources.getDrawable(applicationContext, R.drawable.background_item_main_application)

        var wrapperDrawable1 = DrawableCompat.wrap(unwrapperDrawable1!!)

        DrawableCompat.setTint(wrapperDrawable1, resources.getColor(R.color.colorGrapeFruitDark))

        layoutRemindItem.background = wrapperDrawable1

        var unwrapperDrawable2 = AppCompatResources.getDrawable(applicationContext, R.drawable.background_item_main_application)

        var wrapperDrawable2 = DrawableCompat.wrap(unwrapperDrawable2!!)

        DrawableCompat.setTint(wrapperDrawable2, resources.getColor(R.color.colorBitterSweetDark))

        layoutScheduleItem.background = wrapperDrawable2

        var unwrapperDrawable3 = AppCompatResources.getDrawable(applicationContext, R.drawable.background_item_main_application)

        var wrapperDrawable3 = DrawableCompat.wrap(unwrapperDrawable3!!)

        DrawableCompat.setTint(wrapperDrawable3, resources.getColor(R.color.colorFlowerDark))

        layoutTaskManagement.background = wrapperDrawable3

        var unwrapperDrawable4 = AppCompatResources.getDrawable(applicationContext, R.drawable.background_item_main_application)

        var wrapperDrawable4 = DrawableCompat.wrap(unwrapperDrawable4!!)

        DrawableCompat.setTint(wrapperDrawable4, resources.getColor(R.color.colorGrassDark))

        layoutTaskAlarm.background = wrapperDrawable4

        var unwrapperDrawable5 = AppCompatResources.getDrawable(applicationContext, R.drawable.background_item_main_application)

        var wrapperDrawable5 = DrawableCompat.wrap(unwrapperDrawable5!!)

        DrawableCompat.setTint(wrapperDrawable5, resources.getColor(R.color.colorBlueJeansDark))

        layoutChooseMusics.background = wrapperDrawable5

        var unwrapperDrawable6 = AppCompatResources.getDrawable(applicationContext, R.drawable.background_item_main_application)

        var wrapperDrawable6 = DrawableCompat.wrap(unwrapperDrawable6!!)

        DrawableCompat.setTint(wrapperDrawable6, resources.getColor(R.color.colorLavanderDark))

        layoutPomodoro.background = wrapperDrawable6

        var unwrapperDrawable7 = AppCompatResources.getDrawable(applicationContext, R.drawable.background_item_main_application)

        var wrapperDrawable7 = DrawableCompat.wrap(unwrapperDrawable7!!)

        DrawableCompat.setTint(wrapperDrawable7, resources.getColor(R.color.colorPinkRoseDark))

        layoutSetting.background = wrapperDrawable7

        var unwrapperDrawable8 = AppCompatResources.getDrawable(applicationContext, R.drawable.background_item_main_application)

        var wrapperDrawable8 = DrawableCompat.wrap(unwrapperDrawable8!!)

        DrawableCompat.setTint(wrapperDrawable8, resources.getColor(R.color.colorMintDark))

        layoutGroup.background = wrapperDrawable8

    }

    fun navigateToAlarm(view: View) {

        val intent = Intent(applicationContext, ClockActivity::class.java)

        startActivity(intent)

    }

    fun navigateToSetting(view: View) {

        val intent = Intent(applicationContext, SettingActivity::class.java)

        startActivity(intent)

    }

    fun chooseRingTone(view: View) {

        val intent = Intent(applicationContext, Alarm_choose_ringtone::class.java)

        startActivity(intent)

    }

    fun navigateToReminder(view: View) {

        var intent = Intent(applicationContext, TaskManagementActivity::class.java)

        startActivity(intent)

    }

    fun displaySchedule(view: View) {

        var intent = Intent(applicationContext, DisplaySchedule::class.java)

        startActivity(intent)

    }

    fun navigateToEisenHower(view: View) {

        var intent = Intent(applicationContext, EisenHowerActivity::class.java)

        startActivity(intent)

    }

    fun navigateToPomodoro(view: View) {

        var intent = Intent(applicationContext, TimerActivity::class.java)

        intent.flags=Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP

        intent.putExtra("fromClass","Main_AlarmActivity")

        startActivity(intent)

    }

    fun navigateToGroup(view:View){

        var intent = Intent(applicationContext, GroupTask::class.java)

        intent.flags=Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP

        intent.putExtra("fromClass","Main_AlarmActivity")

        startActivity(intent)

    }

}
