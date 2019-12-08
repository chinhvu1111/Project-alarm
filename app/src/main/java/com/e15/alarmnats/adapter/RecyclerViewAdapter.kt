package com.e15.alarmnats.adapter

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.Switch
import android.widget.TextView

import com.e15.alarmnats.activity.AlarmListActivity
import com.e15.alarmnats.activity.ClockActivity
import com.e15.alarmnats.R

import java.util.ArrayList

class RecyclerViewAdapter(mAlarmTimes: ArrayList<String>,
                          mAlarmTimesInMillis: ArrayList<Long>,
                          mAlarmStatuses: ArrayList<Boolean>,
                          mRingtoneNames: ArrayList<String>,
                          mRingtoneUris: ArrayList<String>,
                          mLabels: ArrayList<String>,
                          mQuestions: ArrayList<String>,
                          mFlags: ArrayList<Int>,
                          private val mContext: Context,
                          private val mFragment: AlarmListActivity) : RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

    internal var alarmManager: AlarmManager? = null

    // lists to keep alarm details
    private var mAlarmTimes = ArrayList<String>()
    private var mAlarmTimesInMillis = ArrayList<Long>()
    private var mAlarmStatuses = ArrayList<Boolean>()
    private var mRingtoneNames = ArrayList<String>()
    private var mRingtoneUris = ArrayList<String>()
    private var mLabels = ArrayList<String>()
    private var mQuestions = ArrayList<String>()
    private var mFlags = ArrayList<Int>()

    init {
        this.mAlarmTimes = mAlarmTimes
        this.mAlarmTimesInMillis = mAlarmTimesInMillis
        this.mAlarmStatuses = mAlarmStatuses
        this.mRingtoneNames = mRingtoneNames
        this.mRingtoneUris = mRingtoneUris
        this.mLabels = mLabels
        this.mQuestions = mQuestions
        this.mFlags = mFlags
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.layout_alarm_item, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {

        viewHolder.alarmTime.text = String.format("%02d:%02d",
                Integer.parseInt(mAlarmTimes[i].split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]),
                Integer.parseInt(mAlarmTimes[i].split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]))
        viewHolder.alarmSwitch.isChecked = mAlarmStatuses[i]
        viewHolder.alarmLabel.text = mLabels[i]
        viewHolder.dismisQuestion.text = mQuestions[i]

        if (mAlarmStatuses[i])
            viewHolder.parentLayout.setBackgroundColor(mContext.resources.getColor(R.color.switchOn))
        else
            viewHolder.parentLayout.setBackgroundColor(mContext.resources.getColor(R.color.switchOff))

        viewHolder.parentLayout.background=viewHolder.parentLayout.resources.getDrawable(R.drawable.background_box);

        val timeInMillis = mAlarmTimesInMillis[i]
        val ringtoneUri = Uri.parse(mRingtoneUris[i])
        val flag = mFlags[i]

        viewHolder.deleteButton.setOnClickListener {
            if (mContext is ClockActivity) {
                Log.d("delete", "delete button clicked")
                mFragment.deleteAlarm(mFlags[i], i)
            }
        }

        viewHolder.alarmSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                mFragment.enableExistingAlarm(mFlags[i])
                viewHolder.parentLayout.setBackgroundColor(mContext.resources.getColor(R.color.switchOn))

                viewHolder.parentLayout.background=viewHolder.parentLayout.resources.getDrawable(R.drawable.background_box);

            } else {
                mFragment.cancelAlarm(mFlags[i], true)// change status when switching on/off
                viewHolder.parentLayout.setBackgroundColor(mContext.resources.getColor(R.color.switchOff))

                viewHolder.parentLayout.background=viewHolder.parentLayout.resources.getDrawable(R.drawable.background_box);

            }
        }

        viewHolder.editAlarmButton.setOnClickListener {
            if (mContext is ClockActivity) {
                Log.d("edit", "edit button clicked")
                mFragment.editAlarm(mFlags[i])
            }
        }

        /*TODO: get time form timepicker
        * TODO: get ringtone
        * TODO: do all the things from option_display activity*/

    }

    override fun getItemCount(): Int {
        return mAlarmTimes.size
    }

    fun onRecAdapterActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        Log.d("MyAdapter", "onRecAdapterActivityResult")
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        internal var alarmTime: TextView
        internal var editAlarmButton: Button
        internal var deleteButton: Button
        internal var alarmLabel: TextView
        internal var dismisQuestion: TextView
        internal var alarmSwitch: Switch
        internal var parentLayout: RelativeLayout

        init {

            alarmTime = itemView.findViewById(R.id.text_view_alarm_time)
            editAlarmButton = itemView.findViewById(R.id.button_edit_alarm)
            deleteButton = itemView.findViewById(R.id.button_delete)
            alarmLabel = itemView.findViewById(R.id.text_view_label)
            dismisQuestion = itemView.findViewById(R.id.dismiss_method)
            alarmSwitch = itemView.findViewById(R.id.switch_alarm)
            parentLayout = itemView.findViewById(R.id.layout_parent)

        }
    }

    companion object {

        private val RINGTONE_REQUEST_CODE = 1
    }
}
