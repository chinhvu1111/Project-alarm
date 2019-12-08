package com.e15.alarmnats.Model

import android.net.Uri

import java.io.Serializable

class Alarm : Serializable {

    var _id: Int = 0
    var alarmTime: String? = null
    var alarmTimeInMillis: Long = 0
    var isAlarmStatus: Boolean = false
    var ringtoneName: String? = null
    var ringtoneUri: String? = null
    var label: String? = null
    var flag: Int? = null
        set

    var question: String? = null
    var answer: String? = null

    constructor() {}

    constructor(_id: Int, alarmTime: String, alarmTimeInMillis: Long, alarmStatus: Boolean,
                ringtoneName: String, ringtoneUri: String, label: String, flag: Int, question: String, answer: String) {
        this._id = _id //
        this.alarmTime = alarmTime //
        this.alarmTimeInMillis = alarmTimeInMillis
        this.isAlarmStatus = alarmStatus //
        this.ringtoneName = ringtoneName //
        this.ringtoneUri = ringtoneUri //
        this.label = label //
        this.flag = flag //
        this.question = question //
        this.answer = answer //
    }

    fun setFlag(flag: Int) {
        this.flag = flag
    }
}
