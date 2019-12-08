package com.e15.alarmnats.Model

import org.json.JSONException
import org.json.JSONObject

import java.io.Serializable

/**
 * Created by mikael on 2017-06-05.
 */

class AlarmItem : Cloneable, Serializable {
    var trackUri: String? = null
    var imageUrl: String? = null
    var name: String? = null
    var artist: String? = null
    var hour: Int = 0
    var minute: Int = 0
    private var active: Boolean = false
    var alarmID: Int = 0
        private set

    var json: String? = null

    /**
     * Formats time text to hh:mm
     */
    val formatedTime: String
        get() {

            val hourPrefix = if (hour < 10) "0" else ""
            val minutePrefix = if (minute < 10) "0" else ""

            return "$hourPrefix$hour:$minutePrefix$minute"

        }

    constructor() {

    }

    constructor(trackUri: String, imageUrl: String, name: String, artist: String,
                hour: Int, minute: Int, active: Boolean, alarmID: Int) {
        this.trackUri = trackUri
        this.imageUrl = imageUrl
        this.name = name
        this.artist = artist
        this.hour = hour
        this.minute = minute
        this.active = active
        this.alarmID = alarmID

        try {
            jsonify()
        } catch (e: JSONException) {

        }

    }

    constructor(trackUri: String, name: String) {
        this.trackUri = trackUri
        this.name = name
    }

    // generates json from all class attributes
    @Throws(JSONException::class)
    fun jsonify() {

        val jsonObj = JSONObject()
        jsonObj.accumulate("trackUri", trackUri)
        jsonObj.accumulate("imageUrl", imageUrl)
        jsonObj.accumulate("name", name)
        jsonObj.accumulate("artist", artist)
        jsonObj.accumulate("hour", hour)
        jsonObj.accumulate("minute", minute)
        jsonObj.accumulate("active", active)
        jsonObj.accumulate("alarmID", alarmID)

        this.json = jsonObj.toString()
    }

    // clones this instance
    public override fun clone(): AlarmItem {

        val alarmItem = AlarmItem(this!!.trackUri!!, this!!.imageUrl!!,
                this!!.name!!, this!!.artist!!, hour, minute, active, alarmID)
        alarmItem.json = this.json

        return alarmItem
    }

    companion object {

        // build from string
        fun buildFromString(s: String): AlarmItem {
            val item = AlarmItem()
            val json: JSONObject

            try {
                json = JSONObject(s)
                item.trackUri = json.getString("trackUri")
                item.imageUrl = json.getString("imageUrl")
                item.name = json.getString("name")
                item.artist = json.getString("artist")
                item.hour = Integer.parseInt(json.getString("hour"))
                item.minute = Integer.parseInt(json.getString("minute"))
                item.active = json.getBoolean("active")
                item.alarmID = Integer.parseInt(json.getString("alarmID"))
                item.json = json.toString()

            } catch (e: JSONException) {
                e.printStackTrace()
            }

            return item

        }
    }

}
