package com.e15.alarmnats.activity

import android.annotation.SuppressLint
import android.graphics.Color
import android.util.Log
import android.widget.Toast
import com.alamkanak.weekview.WeekViewEvent
import com.e15.alarmnats.Database.ReminderDatabase
import java.util.*

class DisplaySchedule : BaseActivity() {

    //This method is(executed) when changing either (month) or (year)

    //Very important interface, it's the base to load events in the calendar.
    // This method is called three times: once to load the previous month,
    // once to load the next month and once to load the current month.
    // That's why you can have three times the same event at the same place
    // if you mess up with the configuration
    //
    //Params:
    //newYear – : year of the events required by the view.
    //newMonth – : month of the events required by
    // the view 1 based (not like JAVA API) --> January = 1 and December = 12.

    companion object {
        var id: Long = 0;
    }

    @SuppressLint("ResourceType")
    override fun onMonthChange(newYear: Int, newMonth: Int): List<WeekViewEvent> {
        // Populate the week view with some events.

        Toast.makeText(applicationContext, "$newMonth $newYear", Toast.LENGTH_SHORT).show()

        val events = ArrayList<WeekViewEvent>()

        var dbHandler: ReminderDatabase = ReminderDatabase(applicationContext);

        var eventsList = dbHandler.allEvents;

        for (item in eventsList) {

            var startTimeCalendar = Calendar.getInstance()

            var startTime = item.startTime!!.trim().split(":");

            var startHour = Integer.parseInt(startTime[0]);
            var startMinute = Integer.parseInt(startTime[1]);

            startTimeCalendar.set(Calendar.HOUR_OF_DAY, startHour)
            startTimeCalendar.set(Calendar.MINUTE, startMinute)

            var date = item.date!!.trim().split("/")

            startTimeCalendar.set(Calendar.MONTH, Integer.parseInt(date[0])-1)
            startTimeCalendar.set(Calendar.YEAR, Integer.parseInt(date[2]))

            Log.e("--------------- month ", date[0])
            Log.e("--------------- day ", date[1])
            Log.e("--------------- year ", date[2])

            if(Integer.parseInt(date[0])-1==newMonth&&Integer.parseInt(date[2])==newYear){

                Log.e("--------------- month ", date[0])
                Log.e("--------------- day ", date[1])
                Log.e("--------------- year ", date[2])

                startTimeCalendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(date[1]))

                var endTime = item.endTime!!.trim().split(":");

                var endHour = Integer.parseInt(endTime[0]);
                var endMinute = Integer.parseInt(endTime[1]);

                startTimeCalendar.set(Calendar.HOUR_OF_DAY, startHour)
                startTimeCalendar.set(Calendar.MINUTE, startMinute)

                var endTimeCalendar = startTimeCalendar.clone() as Calendar

                endTimeCalendar.set(Calendar.HOUR_OF_DAY,endHour)
//            endTimeCalendar.add(Calendar.HOUR, 3);
                endTimeCalendar.set(Calendar.MINUTE, endMinute)
                endTimeCalendar.set(Calendar.MONTH, Integer.parseInt(date[0])-1)

                var event = WeekViewEvent(++id, getEventTitle(startTimeCalendar), startTimeCalendar, endTimeCalendar)

                event.color = Color.parseColor(item.color)

//            startTimeCalendar.set(Calendar.HOUR_OF_DAY, 3);
//            startTimeCalendar.set(Calendar.MINUTE, 0);
//            startTimeCalendar.set(Calendar.MONTH, newMonth - 1);
//            startTimeCalendar.set(Calendar.YEAR, newYear);
//            endTimeCalendar=startTimeCalendar.clone() as Calendar
//            //We need to use HOUR representing to morning or afternoon
//            endTimeCalendar.add(Calendar.HOUR, 1);
//            endTimeCalendar.set(Calendar.MONTH, newMonth - 1);
//
//            //Event adding to list events
//            //It includes(id, title, startTime, endTime, color:String.color)
//            event = WeekViewEvent(1, getEventTitle(startTimeCalendar), startTimeCalendar, endTimeCalendar);
//            event.setColor(getResources().getColor(R.color.event_color_01));
//            events.add(event);

                events.add(event)

            }

        }

//        events.addAll(tempEvents)
//
//        tempEvents.clear()

        //The Calendar class is an abstract class that provides methods
        // for converting between a (specific instant) in time and a set of calendar fields
        // such as YEAR, MONTH, DAY_OF_MONTH, HOUR, and so on,
        // and for manipulating the calendar fields, such as getting the date of the next week.
        // An instant in time can be represented by a millisecond value
        // that is an offset from the Epoch, January 1, 1970 00:00:00.000 GMT (Gregorian).
        return events;

    }

//    @RequiresApi(Build.VERSION_CODES.O)
//    override fun addEvent(view: View) {
//
//        val intent = Intent(this, AddEvent::class.java)
//
//        intent.putExtra("date","")
//
//        startActivityForResult(intent, NEW_ACTIVITY)
//
//    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//
//        if (requestCode == NEW_ACTIVITY && resultCode == Activity.RESULT_OK) {
//
//            Toast.makeText(applicationContext, "Return result", Toast.LENGTH_SHORT).show()
//
//            val START_HOUR_OF_DAY = data!!.getIntExtra("START_HOUR_OF_DAY", 1)
//
//            Log.e("------START_HOUR_OF_DAY", START_HOUR_OF_DAY.toString())
//
//            val START_MINUTE = data.getIntExtra("START_MINUTE", 1)
//
//            Log.e("------START_MINUTE", START_MINUTE.toString())
//
//            val END_HOUR = data.getIntExtra("END_HOUR", 1)
//
//            Log.e("------END_HOUR", END_HOUR.toString())
//
//            val END_MINUTE = data.getIntExtra("END_MINUTE", 1)
//
//            Log.e("------END_MINUTE", END_MINUTE.toString())
//
//            val MONTH = data.getIntExtra("MONTH", 1)
//
//            Log.e("------MONTH", MONTH.toString())
//
//            val YEAR = data.getIntExtra("YEAR", 2019)
//
//            Log.e("------YEAR", YEAR.toString())
//
//            val startTime = Calendar.getInstance()
//
//            startTime.set(Calendar.HOUR_OF_DAY, START_HOUR_OF_DAY)
//
//            startTime.set(Calendar.MINUTE, START_MINUTE)
//
//            startTime.set(Calendar.MONTH, MONTH)
//
//            startTime.set(Calendar.YEAR, YEAR)
//
//            val endTime = startTime.clone() as Calendar
//
//            endTime.set(Calendar.HOUR_OF_DAY, END_HOUR)
//
//            endTime.set(Calendar.MINUTE, END_MINUTE)
//
//            val event = WeekViewEvent(1, getEventTitle(startTime), startTime, endTime)
//
//            event.color = resources.getColor(R.color.event_color_01)
//
//            tempEvents.add(event)
//
//            val startTime1 = Calendar.getInstance()
//            //We need to use HOUR_OF_DAY representing to 24- hour clock,
//            // its always is used to represent to evening
//            startTime1.set(Calendar.HOUR_OF_DAY, 3)
//            startTime1.set(Calendar.MINUTE, 0)
//            startTime1.set(Calendar.MONTH, 9)
//            startTime1.set(Calendar.YEAR, 2019)
//            val endTime1 = startTime1.clone() as Calendar
//            //We need to use HOUR representing to morning or afternoon
//            endTime1.add(Calendar.HOUR, 1)
//            endTime1.set(Calendar.MONTH, 9)
//
//            //Event adding to list events
//            //It includes(id, title, startTime, endTime, color:String.color)
//            val event1 = WeekViewEvent(1, getEventTitle(startTime1), startTime1, endTime1)
//            event1.color = resources.getColor(R.color.event_color_01)
//            tempEvents.add(event1)
//
//            recreate()
//
//            //            this.onMonthChange(2019,10);
//
//        }
//
//        super.onActivityResult(requestCode, resultCode, data)
//
//    }
}
