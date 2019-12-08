package com.e15.alarmnats.Database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

import com.e15.alarmnats.Model.Alarm

import java.util.ArrayList

class AlarmDbHelper private constructor(private val mCtx: Context) : SQLiteOpenHelper(mCtx, DATABASE_NAME, null, DATABASE_VERSION) {

    private var db: SQLiteDatabase? = null

    val allAlarms: List<Alarm>
        get() {
            val alarmList = ArrayList<Alarm>()

            db = readableDatabase

            val c = db!!.rawQuery("SELECT * FROM " + AlarmContract.AlarmTable.TABLE_NAME, null)

            if (c.moveToFirst()) {
                do {
                    val alarm = Alarm()

                    alarm.alarmTime = c.getString(c.getColumnIndex(AlarmContract.AlarmTable.COLUMN_ALARM_TIME))
                    alarm.alarmTimeInMillis = c.getInt(c.getColumnIndex(AlarmContract.AlarmTable.COLUMN_ALARM_IN_MILLIES)).toLong()
                    alarm.isAlarmStatus = if (c.getInt(c.getColumnIndex(AlarmContract.AlarmTable.COLUMN_ALARM_STATUS)) != 0) true else false
                    alarm.ringtoneName = c.getString(c.getColumnIndex(AlarmContract.AlarmTable.COLUMN_RINGTONE_NAME))
                    alarm.ringtoneUri = c.getString(c.getColumnIndex(AlarmContract.AlarmTable.COLUMN_RINGTONE_URI))
                    alarm.label = c.getString(c.getColumnIndex(AlarmContract.AlarmTable.COLUMN_LABEL))
                    alarm.flag = c.getInt(c.getColumnIndex(AlarmContract.AlarmTable.COLUMN_NAME_FLAG))

                    alarm.question = c.getString(c.getColumnIndex(AlarmContract.AlarmTable.COLUMN_QUESTION))
                    alarm.answer = c.getString(c.getColumnIndex(AlarmContract.AlarmTable.COLUMN_ANSWER))

                    alarmList.add(alarm)

                } while (c.moveToNext())
            }

            c.close()

            return alarmList
        }

    override fun onCreate(db: SQLiteDatabase) {
        this.db = db

        val SQL_CREATE_ALARM_TABLE = "CREATE TABLE " +
                AlarmContract.AlarmTable.TABLE_NAME + " ( " +
                AlarmContract.AlarmTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                AlarmContract.AlarmTable.COLUMN_ALARM_TIME + " TEXT, " +
                AlarmContract.AlarmTable.COLUMN_ALARM_IN_MILLIES + " INTEGER, " +
                AlarmContract.AlarmTable.COLUMN_ALARM_STATUS + " INTEGER, " +
                AlarmContract.AlarmTable.COLUMN_RINGTONE_NAME + " TEXT, " +
                AlarmContract.AlarmTable.COLUMN_RINGTONE_URI + " TEXT, " +
                AlarmContract.AlarmTable.COLUMN_LABEL + " TEXT, " +
                AlarmContract.AlarmTable.COLUMN_NAME_FLAG + " INTEGER, " +

                AlarmContract.AlarmTable.COLUMN_QUESTION + " TEXT, " +
                AlarmContract.AlarmTable.COLUMN_ANSWER + " TEXT" +
                ")"

        db.execSQL(SQL_CREATE_ALARM_TABLE)
    }

    fun addAlarm(alarm: Alarm) {
        val cv = ContentValues()
        cv.put(AlarmContract.AlarmTable.COLUMN_ALARM_TIME, alarm.alarmTime)
        cv.put(AlarmContract.AlarmTable.COLUMN_ALARM_IN_MILLIES, alarm.alarmTimeInMillis)
        cv.put(AlarmContract.AlarmTable.COLUMN_ALARM_STATUS, if (alarm.isAlarmStatus) 1 else 0)
        cv.put(AlarmContract.AlarmTable.COLUMN_RINGTONE_NAME, alarm.ringtoneName)
        cv.put(AlarmContract.AlarmTable.COLUMN_RINGTONE_URI, alarm.ringtoneUri.toString())
        cv.put(AlarmContract.AlarmTable.COLUMN_LABEL, alarm.label)
        cv.put(AlarmContract.AlarmTable.COLUMN_NAME_FLAG, alarm.flag)

        cv.put(AlarmContract.AlarmTable.COLUMN_QUESTION, alarm.question)
        cv.put(AlarmContract.AlarmTable.COLUMN_ANSWER, alarm.answer)

        db!!.insert(AlarmContract.AlarmTable.TABLE_NAME, null, cv)
    }

    fun getAlarm(flag: Int?): Alarm {

        // Filter results WHERE "title" = 'My Title'
        val selection = AlarmContract.AlarmTable.COLUMN_NAME_FLAG + " = ?"
        val selectionArgs = arrayOf(flag!!.toString())

        val c = db!!.query(
                AlarmContract.AlarmTable.TABLE_NAME, null, // The array of columns to return (pass null to get all)
                selection, // The columns for the WHERE clause
                selectionArgs, null, null, null// The sort order
        )// The table to query
        // The values for the WHERE clause
        // don't group the rows
        // don't filter by row groups

        val alarms = ArrayList<Alarm>()
        while (c.moveToNext()) {
            val alarm = Alarm(
                    c.getInt(c.getColumnIndexOrThrow(AlarmContract.AlarmTable._ID)),
                    c.getString(c.getColumnIndexOrThrow(AlarmContract.AlarmTable.COLUMN_ALARM_TIME)),
                    c.getLong(c.getColumnIndexOrThrow(AlarmContract.AlarmTable.COLUMN_ALARM_IN_MILLIES)),
                    c.getInt(c.getColumnIndexOrThrow(AlarmContract.AlarmTable.COLUMN_ALARM_STATUS)) != 0,
                    c.getString(c.getColumnIndexOrThrow(AlarmContract.AlarmTable.COLUMN_RINGTONE_NAME)),
                    c.getString(c.getColumnIndexOrThrow(AlarmContract.AlarmTable.COLUMN_RINGTONE_URI)),
                    c.getString(c.getColumnIndexOrThrow(AlarmContract.AlarmTable.COLUMN_LABEL)),
                    c.getInt(c.getColumnIndexOrThrow(AlarmContract.AlarmTable.COLUMN_NAME_FLAG)),
                    c.getString(c.getColumnIndexOrThrow(AlarmContract.AlarmTable.COLUMN_QUESTION)),
                    c.getString(c.getColumnIndexOrThrow(AlarmContract.AlarmTable.COLUMN_ANSWER))
            )

            alarms.add(alarm)
        }
        c.close()
        return alarms[0]
    }

    fun updateStatus(flag: Int?, status: Int): Int {
        // SET
        val values = ContentValues()
        values.put(AlarmContract.AlarmTable.COLUMN_ALARM_STATUS, status)

        // WHERE
        val selection = AlarmContract.AlarmTable.COLUMN_NAME_FLAG + " = ?"
        val selectionArgs = arrayOf(flag!!.toString())

        return db!!.update(
                AlarmContract.AlarmTable.TABLE_NAME,
                values,
                selection,
                selectionArgs)
    }

    fun deleteAlarm(flag: Int?): Boolean {
        Log.d("dbhelper", "delete method")

        val where = AlarmContract.AlarmTable.COLUMN_NAME_FLAG + " = ?"
        val whereAgs = arrayOf(flag!!.toString())

        return db!!.delete(AlarmContract.AlarmTable.TABLE_NAME, where, whereAgs) > 0
    }

    fun updateAlarm(alarm: Alarm) {
        val cv = ContentValues()
        cv.put(AlarmContract.AlarmTable.COLUMN_ALARM_TIME, alarm.alarmTime)
        cv.put(AlarmContract.AlarmTable.COLUMN_ALARM_IN_MILLIES, alarm.alarmTimeInMillis)
        cv.put(AlarmContract.AlarmTable.COLUMN_ALARM_STATUS, if (alarm.isAlarmStatus) 1 else 0)
        cv.put(AlarmContract.AlarmTable.COLUMN_RINGTONE_NAME, alarm.ringtoneName)
        cv.put(AlarmContract.AlarmTable.COLUMN_RINGTONE_URI, alarm.ringtoneUri)
        cv.put(AlarmContract.AlarmTable.COLUMN_LABEL, alarm.label)
        cv.put(AlarmContract.AlarmTable.COLUMN_QUESTION, alarm.question)
        cv.put(AlarmContract.AlarmTable.COLUMN_ANSWER, alarm.answer)

        val where = AlarmContract.AlarmTable.COLUMN_NAME_FLAG + " = ?"
        val whereAgs = arrayOf(alarm.flag!!.toString())

        db!!.update(AlarmContract.AlarmTable.TABLE_NAME, cv, where, whereAgs)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS " + AlarmContract.AlarmTable.TABLE_NAME)
        onCreate(db)
    }

    companion object {

        private var mInstance: AlarmDbHelper? = null

        val DATABASE_NAME = "alarm.db"
        val DATABASE_VERSION = 1

        fun getInstance(ctx: Context): AlarmDbHelper {
            if (mInstance == null) {
                mInstance = AlarmDbHelper(ctx)
                println("new instance!!")
            }
            return mInstance as AlarmDbHelper
        }
    }
}
