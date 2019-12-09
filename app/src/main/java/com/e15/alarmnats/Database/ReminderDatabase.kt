package com.e15.alarmnats.Database

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.e15.alarmnats.Model.Category
import com.e15.alarmnats.Model.Event
import com.e15.alarmnats.R
import com.e15.alarmnats.utils.AlarmReceiver
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ReminderDatabase(private val context: Context) : SQLiteOpenHelper(context, DB_NAME, null, 1) {

    private var db: SQLiteDatabase? = null


    //Get all categories
    val allCategory: ArrayList<Category>
        get() {
            val categories = ArrayList<Category>()

            //Create and/or open a database that will be used for reading and writing.
            // The first time this is called, the database will be opened and onCreate,
            // onUpgrade and/or onOpen will be called.
            db = this.writableDatabase

            val c = db!!.rawQuery("select * from $TB_CATEGORY", null)

            c.moveToFirst()

            while (!c.isAfterLast) {

                val category = Category()

                //Updating get categoryId
                category.hasIdCategory=c.getString(c.getColumnIndex("hashId"))

                category.title=c.getString(c.getColumnIndex("Title"))

                category.color=c.getString(c.getColumnIndex("Color"))

                category.type= Category.CATEGORY_TYPE

                category.hashIdUser=c.getString(c.getColumnIndex("hashIdUser"))

                categories.add(category)

                c.moveToNext()

            }
            db!!.close()

            Collections.sort(categories) { a, b -> a.title.compareTo(b.title) }

            return categories
        }

    val allEvents: ArrayList<Event>
        get() {
            val events = ArrayList<Event>()
            val categories = allCategory
            db = this.writableDatabase
            val c = db!!.rawQuery("select * from $TV_EVENT", null)

            c.moveToFirst()
            while (!c.isAfterLast) {
                val event = Event(0)
                event.hashId=c.getString(c.getColumnIndex("hashId"))
                event.title=c.getString(c.getColumnIndex("Title"))
                event.description=c.getString(c.getColumnIndex("Description"))
                event.place=c.getString(c.getColumnIndex("Place"))
                event.startTime=c.getString(c.getColumnIndex("StartTime"))
                event.endTime=c.getString(c.getColumnIndex("EndTime"))
                event.enabled=c.getInt(c.getColumnIndex("Enabled"))
                event.date=c.getString(c.getColumnIndex("Date"))
                event.notify=c.getString(c.getColumnIndex("Notify"))
                event.type= Event.EVENT_TYPE
                event.repeatType=c.getString(c.getColumnIndex("RepeatType"))
                event.repeatCount=c.getString(c.getColumnIndex("RepeatNumber"))
                event.repeatMode=c.getString(c.getColumnIndex("RepeatMode"))
                event.isDone=c.getInt(c.getColumnIndex("isDone"));
                val cat = c.getString(c.getColumnIndex("Category"))
                event.category=cat
                event.isEdit=false
                event.isShow=false
                event.parentId=c.getString(c.getColumnIndex("parentId"))
                event.levelRecusion=c.getInt(c.getColumnIndex("recursiveLevel"))
                event.remainingTime=c.getLong(c.getColumnIndex("remainingTime"))
                event.requestCode=c.getInt(c.getColumnIndex("requestCode"))
                event.hashIdUser=c.getString(c.getColumnIndex("hashIdUser"))
                for (category in categories) {
                    if (event.category.equals(category.title)) {
                        event.color=category.color

                        //Put code here solve case
                        // that event has'n category (category is not in database)
                        events.add(event)

                    }
                }

                c.moveToNext()
            }
            db!!.close()
            Collections.sort(events) { a, b -> a.title!!.compareTo(b.title!!) }
            return events
        }

    override fun onCreate(db: SQLiteDatabase) {

        val categoryTable = "CREATE TABLE IF NOT EXISTS " + TB_CATEGORY + "(hashId TEXT PRIMARY KEY," +
                "Title TEXT,Color TEXT,hashIdUser TEXT)"

        db.execSQL(categoryTable)

        val eventTable = "CREATE TABLE IF NOT EXISTS " + TV_EVENT + "(hashId TEXT," +
                "Title TEXT,Description TEXT,Place TEXT,StartTime TEXT, EndTime TEXT, Enabled INTEGER,Date TEXT,Category TEXT,Notify TEXT, RepeatType TEXT" +
                ", RepeatNumber TEXT, RepeatMode TEXT, isDone INTEGER, isUrgent INTEGER, isImportant INTEGER, remainingTime LONG, parentId TEXT, recursiveLevel INTEGER," +
                "hashIdUser TEXT, " +
                "requestCode INTEGER PRIMARY KEY AUTOINCREMENT)"

        db.execSQL(eventTable)

        Toast.makeText(context,"Create database successfully!",Toast.LENGTH_SHORT).show()

    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TB_CATEGORY")
        db.execSQL("DROP TABLE IF EXISTS $TV_EVENT")
        onCreate(db)
    }


    fun getEventsByCategory(name: String): List<Event> {
        var events = ArrayList<Event>()
        db = this.writableDatabase
        var c = db!!.rawQuery("select * from $TV_EVENT where Category=?", arrayOf(name))
        c.moveToFirst()
        while (!c.isAfterLast) {
            var event = Event(0)
            event.hashId=c.getString(c.getColumnIndex("hashId"))
            event.title=c.getString(c.getColumnIndex("Title"))
            event.description=c.getString(c.getColumnIndex("Description"))
            event.place=c.getString(c.getColumnIndex("Place"))
            event.startTime=c.getString(c.getColumnIndex("StartTime"))
            event.endTime=c.getString(c.getColumnIndex("EndTime"))
            event.enabled=c.getInt(c.getColumnIndex("Enabled"))
            event.date=c.getString(c.getColumnIndex("Date"))
            event.notify=c.getString(c.getColumnIndex("Notify"))
            event.repeatType=c.getString(c.getColumnIndex("RepeatType"))
            event.repeatCount=c.getString(c.getColumnIndex("RepeatNumber"))
            event.repeatMode=c.getString(c.getColumnIndex("RepeatMode"))
            event.type= Event.EVENT_TYPE
            var cat = c.getString(c.getColumnIndex("Category"))
            event.category=cat
            event.isDone=c.getInt(c.getColumnIndex("isDone"))

            //This here can be affect to (global logic) so at present we (dont need add <this line>)

//            event.isUrgent=c.getInt(c.getColumnIndex("isUrgent"))==1
//            event.isImportant=c.getInt(c.getColumnIndex("isImportant"))==1
            event.remainingTime=c.getLong(c.getColumnIndex("remainingTime"))
            event.parentId=c.getString(c.getColumnIndex("parentId"))
            event.levelRecusion=c.getInt(c.getColumnIndex("recursiveLevel"))
            event.requestCode=c.getInt(c.getColumnIndex("requestCode"))
            event.hashIdUser=c.getString(c.getColumnIndex("hashIdUser"))
            events.add(event)
            c.moveToNext()
        }
        db!!.close()
        return events
    }


    fun createNewEvent(event: Event): Int {

        db = this.writableDatabase
        val cv = ContentValues()
        cv.put("hashId",event.hashId)
        cv.put("Title", event.title)
        cv.put("Description", event.description)
        cv.put("Place", event.place)
        cv.put("StartTime", event.startTime)
        cv.put("EndTime",event.endTime)
        cv.put("Enabled",event.enabled)
        cv.put("Date", event.date)
        cv.put("Category", event.category)
        cv.put("Notify", event.notify)
        cv.put("RepeatType", event.repeatType)
        cv.put("RepeatNumber", event.repeatCount)
        cv.put("RepeatMode", event.repeatMode)
        cv.put("isUrgent",event.isUrgent)
        cv.put("isImportant",event.isImportant)
        cv.put("isDone",event.isDone)
        cv.put("remainingTime",event.remainingTime)
        cv.put("parentId",event.parentId)
        cv.put("recursiveLevel",event.levelRecusion)
        cv.put("hashIdUser",event.hashIdUser)
        val id = db!!.insert(TV_EVENT, null, cv)
        db!!.close()
        return id.toInt()
    }


    fun createNewCategory(category:Category): Boolean {

        var hashId=category.hasIdCategory

        var title=category.title

        var color=category.color

        var hashIdUser=category.hashIdUser

        db = this.writableDatabase
        //check if there is already existing category
        val c = db!!.rawQuery("select * from $TB_CATEGORY where Title=?", arrayOf(title))

        if (c.count == 0) {
            val cv = ContentValues()
            cv.put("hashId",hashId)
            cv.put("Title", title)
            cv.put("Color", color)
            cv.put("hashIdUser",hashIdUser)
            val id = db!!.insert(TB_CATEGORY, null, cv)
            db!!.close()
            return true
        } else {
            return false
        }
    }

    fun updateCategory(hashId: String, title: String, color: String, oldTitle: String): Boolean {
        db = this.writableDatabase

        val c = db!!.rawQuery("select * from $TB_CATEGORY where Title=?", arrayOf(title))

        if (c.count == 0 || c.count == 1 && title.toLowerCase() == oldTitle.toLowerCase()) {
            val cv = ContentValues()
            cv.put("Title", title)
            cv.put("Color", color)
            db!!.update(TB_CATEGORY, cv, "hashId=?", arrayOf(hashId))
            db!!.close()
            return true
        } else {
            return false
        }
    }

    fun removeCategory(hashId: String, name: String) {
        db = this.writableDatabase
        db!!.delete(TB_CATEGORY, "hashId=?", arrayOf(hashId))
        removeEventsByCategory(name)
        db!!.close()
    }


    fun removeEventsByCategory(categoryName: String) {
        val c = db!!.rawQuery("select * from $TV_EVENT where Category=?", arrayOf(categoryName))
        if (c.count != 0) {
            c.moveToFirst()
            while (!c.isAfterLast) {
                val hashId = c.getString(c.getColumnIndex("hashId"))
                var requestCode=c.getInt(c.getColumnIndex("requestCode"))
                db!!.delete(TV_EVENT, "hashId=?", arrayOf(hashId.toString()))

                //cancel by using request code
                AlarmReceiver().cancelAlarm(context, requestCode)
                c.moveToNext()
            }
        }

    }

    fun updateEvent(event: Event): Int {
        db = this.writableDatabase
        val cv = ContentValues()
        cv.put("Title", event.title)
        cv.put("Description", event.description)
        cv.put("Place", event.place)
        cv.put("StartTime", event.startTime)
        cv.put("EndTime",event.endTime)
        cv.put("Enabled",event.enabled)
        cv.put("Date", event.date)
        cv.put("Category", event.category)
        cv.put("Notify", event.notify)
        cv.put("RepeatType", event.repeatType)
        cv.put("RepeatNumber", event.repeatCount)
        cv.put("RepeatMode", event.repeatMode)
        cv.put("isDone",event.isDone)
        cv.put("remainingTime",event.remainingTime)
        cv.put("parentId",event.parentId)
        cv.put("recursiveLevel",event.levelRecusion)
        val row = db!!.update(TV_EVENT, cv, "hashId=?", arrayOf(event.hashId.toString()))
        db!!.close()
        return row
    }

    fun updateItem(event: Event): Int {
        db = this.writableDatabase
        val cv = ContentValues()
        cv.put("Title", event.title)
        cv.put("Description", event.description)
        cv.put("Place", event.place)
        cv.put("StartTime", event.startTime)
        cv.put("EndTime",event.endTime)
        cv.put("Enabled",event.enabled)
        cv.put("Date", event.date)
        cv.put("Category", event.category)
        cv.put("Notify", event.notify)
        cv.put("RepeatType", event.repeatType)
        cv.put("RepeatNumber", event.repeatCount)
        cv.put("RepeatMode", event.repeatMode)
        cv.put("isDone",event.isDone)
        cv.put("remainingTime",event.remainingTime)
        cv.put("recursiveLevel",event.levelRecusion)
        val row = db!!.update(TV_EVENT, cv, "hashId=?", arrayOf(event.hashId.toString()))
        db!!.close()
        return row
    }


    fun removeEvent(hashId: String) {
        db = this.writableDatabase
        db!!.delete(TV_EVENT, "hashId=?", arrayOf(hashId))
        db!!.close()
    }

    fun getEventId(hashId: String): Event? {
        var event: Event? = null
        db = this.writableDatabase
        val c = db!!.rawQuery("select * from $TV_EVENT where hashId=?", arrayOf(hashId))
        if (c.count == 1) {
            c.moveToFirst()
            event = Event(0)
            event!!.hashId=c.getString(c.getColumnIndex("hashId"))
            event!!.title=c.getString(c.getColumnIndex("Title"))
            event!!.description=c.getString(c.getColumnIndex("Description"))
            event!!.place=c.getString(c.getColumnIndex("Place"))
            event!!.startTime=c.getString(c.getColumnIndex("StartTime"))
            event.endTime=c.getString(c.getColumnIndex("EndTime"))
            event.enabled=c.getInt(c.getColumnIndex("Enabled"))
            event!!.date=c.getString(c.getColumnIndex("Date"))
            event!!.notify=c.getString(c.getColumnIndex("Notify"))
            event!!.type= Event.EVENT_TYPE
            val cat = c.getString(c.getColumnIndex("Category"))
            event!!.category=cat
            event!!.repeatType=c.getString(c.getColumnIndex("RepeatType"))
            event!!.repeatCount=c.getString(c.getColumnIndex("RepeatNumber"))
            event!!.repeatMode=c.getString(c.getColumnIndex("RepeatMode"))
            event.isDone=c.getInt(c.getColumnIndex("isDone"));
            event.remainingTime=c.getLong(c.getColumnIndex("remainingTime"))
            event.parentId=c.getString(c.getColumnIndex("parentId"))
            event.levelRecusion=c.getInt(c.getColumnIndex("recursiveLevel"))
            event.requestCode=c.getInt(c.getColumnIndex("requestCode"))
            event.hashIdUser=c.getString(c.getColumnIndex("hashIdUser"))
        }
        db!!.close()
        return event
    }

    fun getItemByCategory(name: String): List<Event> {
        val events = ArrayList<Event>()
        val categories = allCategory
        db = this.writableDatabase
        val c = db!!.rawQuery("select * from $TV_EVENT where Category=?", arrayOf(name))

        c.moveToFirst()
        while (!c.isAfterLast) {
            val event = Event(0)
            event.hashId=c.getString(c.getColumnIndex("hashId"))
            event.title=c.getString(c.getColumnIndex("Title"))
            event.description=c.getString(c.getColumnIndex("Description"))
            event.place=c.getString(c.getColumnIndex("Place"))
            event.startTime=c.getString(c.getColumnIndex("StartTime"))
            event.endTime=c.getString(c.getColumnIndex("EndTime"))
            event.enabled=c.getInt(c.getColumnIndex("Enabled"))
            event.date=c.getString(c.getColumnIndex("Date"))
            event.notify=c.getString(c.getColumnIndex("Notify"))
            event.type= Event.EVENT_TYPE
            event.repeatType=c.getString(c.getColumnIndex("RepeatType"))
            event.repeatCount=c.getString(c.getColumnIndex("RepeatNumber"))
            event.repeatMode=c.getString(c.getColumnIndex("RepeatMode"))
            event.isDone=c.getInt(c.getColumnIndex("isDone"));
            event.remainingTime=c.getLong(c.getColumnIndex("remainingTime"))
            val cat = c.getString(c.getColumnIndex("Category"))
            event.category=cat
            event.isEdit=false
            event.isShow=false
            event.parentId=c.getString(c.getColumnIndex("parentId"))
            event.levelRecusion=c.getInt(c.getColumnIndex("recursiveLevel"))
            event.requestCode=c.getInt(c.getColumnIndex("requestCode"))
            event.hashIdUser=c.getString(c.getColumnIndex("hashIdUser"))
            for (category in categories) {
                if (event.category.equals(category.title)) {
                    event.color=category.color
                }
            }

            events.add(event)
            c.moveToNext()
        }
        db!!.close()
        return events
    }

    companion object {

        private val DB_NAME = "Remind"
        private val TB_CATEGORY = "Category"
        private val TV_EVENT = "Event"
    }

    fun getEvents(isUrgent:Int, isImportant:Int,calendar:Calendar):MutableLiveData<ArrayList<Event>>{
        val events = ArrayList<Event>()
        val categories = allCategory
        db = this.writableDatabase
        val c = db!!.rawQuery("select * from $TV_EVENT", null)

        c.moveToFirst()
        while (!c.isAfterLast) {

            var u=c.getInt(c.getColumnIndex("isUrgent"))
            var i=c.getInt(c.getColumnIndex("isImportant"))

            if(u!=isUrgent||i!=isImportant) {

                c.moveToNext()

                continue
            }

            val event = Event(0)
            event.hashId=c.getString(c.getColumnIndex("hashId"))
            event.title=c.getString(c.getColumnIndex("Title"))
            event.description=c.getString(c.getColumnIndex("Description"))
            event.place=c.getString(c.getColumnIndex("Place"))
            event.startTime=c.getString(c.getColumnIndex("StartTime"))
            event.endTime=c.getString(c.getColumnIndex("EndTime"))
            event.enabled=c.getInt(c.getColumnIndex("Enabled"))
            event.date=c.getString(c.getColumnIndex("Date"))
            event.notify=c.getString(c.getColumnIndex("Notify"))
            event.type= Event.EVENT_TYPE
            event.repeatType=c.getString(c.getColumnIndex("RepeatType"))
            event.repeatCount=c.getString(c.getColumnIndex("RepeatNumber"))
            event.repeatMode=c.getString(c.getColumnIndex("RepeatMode"))
            event.remainingTime=c.getLong(c.getColumnIndex("remainingTime"))
            event.isDone=c.getInt(c.getColumnIndex("isDone"));
            event.requestCode=c.getInt(c.getColumnIndex("requestCode"))
            event.hashIdUser=c.getString(c.getColumnIndex("hashIdUser"))

            if(event.isDone==1){

                c.moveToNext()

                continue

            }

            val cat = c.getString(c.getColumnIndex("Category"))
            event.category=cat
            event.isShow=false
            for (category in categories) {
                if (event.category.equals(category.title)) {
                }
            }

            event.isUrgent=u==1

            event.isImportant=i==1

            var actions=context.resources.getStringArray(R.array.action_type_events)

            if(u==0&&i==0){

                event.level=actions[0]

//                TaskBaseOnCustomCategoryFragment.listEvent.add(e)

            }
            if(u==0&&i==1){

                event.level=actions[1]

//                TaskBaseOnCustomCategoryFragment.listEvent1.add(e)

            }
            if(u==1&&i==0){

                event.level=actions[2]

//                TaskBaseOnCustomCategoryFragment.listEvent2.add(e)

            }
            if(u==1&&i==1){

                event.level=actions[3]

//                TaskBaseOnCustomCategoryFragment.listEvent3.add(e)

            }

            event.parentId=c.getString(c.getColumnIndex("parentId"))

            event.levelRecusion=c.getInt(c.getColumnIndex("recursiveLevel"))

            events.add(event)
            c.moveToNext()
        }
        db!!.close()

        Collections.sort(events) { a, b -> a.title!!.compareTo(b.title!!) }

        var m: MutableLiveData<ArrayList<Event>> =MutableLiveData()

        var tempEvent=ArrayList<Event>()

        var sharedPreferences=context.getSharedPreferences("CalendarPre",Activity.MODE_PRIVATE)

        var choice=sharedPreferences.getInt("choice",-1)

        var x=calendar.get(Calendar.YEAR)
        var y=calendar.get(Calendar.MONTH)
        var z=calendar.get(Calendar.DAY_OF_MONTH)

        //Filtering all events base on event
        for(e in events){

            var calendarEvent=Calendar.getInstance()

            if(choice==0){
                var monthEvent=Integer.parseInt(e.date!!.split("/").get(0))-1
                var dayEvent=Integer.parseInt(e.date!!.split("/").get(1))
                var yearEvent=Integer.parseInt(e.date!!.split("/").get(2))

                calendarEvent.set(Calendar.YEAR,yearEvent)
                calendarEvent.set(Calendar.MONTH,monthEvent)
                calendarEvent.set(Calendar.DAY_OF_MONTH,dayEvent)

            }else if(choice==1){

                var hourEvent=Integer.parseInt(e.startTime!!.split(":").get(0))
                var minuteEvent=Integer.parseInt(e.startTime!!.split(":").get(1))

                var compareHour=calendar.get(Calendar.HOUR_OF_DAY)

                var compareMinute=calendar.get(Calendar.MINUTE)

                if(hourEvent>compareHour){

                    tempEvent.add(e)

                    continue
                }else if(hourEvent==compareHour&&minuteEvent>=compareMinute){

                    tempEvent.add(e)

                    continue

                }else{

                    continue

                }

            }

            if(calendarEvent.after(calendar)) tempEvent.add(e)

        }

        m.value=tempEvent

        return m
    }

    val allEventsCustom: ArrayList<Event>
        get() {
            val events = ArrayList<Event>()
            val categories = allCategory
            db = this.writableDatabase
            val c = db!!.rawQuery("select * from $TV_EVENT", null)

            c.moveToFirst()
            while (!c.isAfterLast) {
                val event = Event(0)
                event.hashId=c.getString(c.getColumnIndex("hashId"))
                event.title=c.getString(c.getColumnIndex("Title"))
                event.description=c.getString(c.getColumnIndex("Description"))
                event.place=c.getString(c.getColumnIndex("Place"))
                event.startTime=c.getString(c.getColumnIndex("StartTime"))
                event.endTime=c.getString(c.getColumnIndex("EndTime"))
                event.enabled=c.getInt(c.getColumnIndex("Enabled"))
                event.date=c.getString(c.getColumnIndex("Date"))
                event.notify=c.getString(c.getColumnIndex("Notify"))
                event.type= Event.EVENT_TYPE
                event.repeatType=c.getString(c.getColumnIndex("RepeatType"))
                event.repeatCount=c.getString(c.getColumnIndex("RepeatNumber"))
                event.repeatMode=c.getString(c.getColumnIndex("RepeatMode"))
                event.isDone=c.getInt(c.getColumnIndex("isDone"));
                val cat = c.getString(c.getColumnIndex("Category"))
                event.category=cat
                event.isShow=false
                event.remainingTime=c.getLong(c.getColumnIndex("remainingTime"))
                event.hashIdUser=c.getString(c.getColumnIndex("hashIdUser"))
                for (category in categories) {
                    if (event.category.equals(category.title)) {
                    }
                }
                var u=c.getInt(c.getColumnIndex("isUrgent"))
                var i=c.getInt(c.getColumnIndex("isImportant"))
                event.isUrgent=u==1

                event.isImportant=i==1

                var actions=context.resources.getStringArray(R.array.action_type_events)

                if(u==1&&i==1){

                    event.level=actions[0]

//                TaskBaseOnCustomCategoryFragment.listEvent.add(e)

                }
                if(u==1&&i==0){

                    event.level=actions[1]

//                TaskBaseOnCustomCategoryFragment.listEvent1.add(e)

                }
                if(u==0&&i==1){

                    event.level=actions[2]

//                TaskBaseOnCustomCategoryFragment.listEvent2.add(e)

                }
                if(u==0&&i==0){

                    event.level=actions[3]

//                TaskBaseOnCustomCategoryFragment.listEvent3.add(e)

                }

                event.parentId=c.getString(c.getColumnIndex("parentId"))

                event.levelRecusion=c.getInt(c.getColumnIndex("recursiveLevel"))
                event.requestCode=c.getInt(c.getColumnIndex("requestCode"))

                events.add(event)
                c.moveToNext()
            }
            db!!.close()
            Collections.sort(events) { a, b -> a.title!!.compareTo(b.title!!) }
            return events
        }

}