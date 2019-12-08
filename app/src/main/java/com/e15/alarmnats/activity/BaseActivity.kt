package com.e15.alarmnats.activity

import android.graphics.RectF
import android.os.Bundle
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.alamkanak.weekview.DateTimeInterpreter
import com.alamkanak.weekview.MonthLoader
import com.alamkanak.weekview.WeekView
import com.alamkanak.weekview.WeekViewEvent
import com.e15.alarmnats.R
import com.google.android.material.navigation.NavigationView
import java.text.SimpleDateFormat
import java.util.*

abstract class BaseActivity : AppCompatActivity(), WeekView.EventClickListener,
        MonthLoader.MonthChangeListener,
        WeekView.EventLongPressListener,
        WeekView.EmptyViewLongPressListener, NavigationView.OnNavigationItemSelectedListener {
    private var mWeekViewType = TYPE_THREE_DAY_VIEW

    lateinit var drawerLayout:DrawerLayout

    lateinit var navigationView:NavigationView

    lateinit var toolbar:Toolbar

    lateinit var mToggle:ActionBarDrawerToggle

    var flag=false

    //WeeView is type of layout( (Main layout contains (all) of days)
    //Then we add (layout) of library to the (activity_base.xml)
    var weekView: WeekView? = null
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)

        drawerLayout=findViewById(R.id.drawLayout)

        navigationView=findViewById(R.id.nav_view)

        navigationView.setNavigationItemSelectedListener(this)

        navigationView.itemIconTintList=null

        toolbar=findViewById(R.id.toolbar)

        //Construct a new ActionBarDrawerToggle.
        //The given Activity will be linked to the specified DrawerLayout and
        // its Actionbar's Up button will be set to a custom drawable.
        //This drawable shows a Hamburger icon when drawer is closed and an arrow when drawer is open.
        // It animates between these two states as the drawer opens.
        //String resources must be provided to describe the open/close drawer actions for accessibility services.
        mToggle=ActionBarDrawerToggle(this, drawerLayout,R.string.open,R.string.close)

        //Synchronize the state of the drawer indicator/affordance with the linked DrawerLayout
        mToggle.syncState()
        //Set a Toolbar to act as the ActionBar for this Activity window.
        //When set to a non-null value the getActionBar() method will
        // return an ActionBar object that can be used to control the given toolbar as
        // if it were a traditional window decor action bar. The toolbar's menu
        // will be populated with the Activity's options menu and the navigation button
        // will be wired through the standard home menu select action.
        setSupportActionBar(toolbar)

        //Retrieve a reference to this activity's ActionBar.
        var actionBar:ActionBar= this!!.supportActionBar!!

        //Set whether home should be displayed as an "up" affordance.
        // Set this to true if selecting "home" returns up by a single level
        // in your UI rather than back to the top level or front page.
        //To set several display options at once, see the setDisplayOptions methods.
        actionBar.setDisplayHomeAsUpEnabled(true)

        actionBar.setHomeButtonEnabled(true)

        actionBar.setHomeAsUpIndicator(R.drawable.baseline_menu_black_18dp)

//        actionBar.anv

//        drawerLayout.addDrawerListener(mToggle)


        // Get a reference for the week view in the layout.
        weekView = findViewById<View>(R.id.weekView) as WeekView

        // Show a toast message about the touched event.
        weekView!!.setOnEventClickListener(this)

        // The week view has infinite scrolling horizontally. We have to provide the events of a
        // month every time the month changes on the week view.
        weekView!!.monthChangeListener = this

        // Set long press listener for events.
        weekView!!.eventLongPressListener = this

        // Set long press listener for empty view
        weekView!!.emptyViewLongPressListener = this

        // Set up a (date time interpreter) to interpret how the date and time will be formatted in
        // the week view. This is optional.
        setupDateTimeInterpreter(false)

    }

    /**
     * Set up a date time interpreter which will show short date values when in week view and long
     * date values otherwise.
     * @param shortDate True if the date values should be short.
     */
    private fun setupDateTimeInterpreter(shortDate: Boolean) {
        weekView!!.dateTimeInterpreter = object : DateTimeInterpreter {
            override fun interpretDate(date: Calendar): String {
                val weekdayNameFormat = SimpleDateFormat("EEE", Locale.getDefault())
                var weekday = weekdayNameFormat.format(date.time)
                val format = SimpleDateFormat(" M/d", Locale.getDefault())

                // All android api level do not have a standard way of getting the first letter of
                // the week day name. Hence we get the first char programmatically.
                // Details: http://stackoverflow.com/questions/16959502/get-one-letter-abbreviation-of-week-day-of-a-date-in-java#answer-16959657
                if (shortDate)
                    weekday = weekday[0].toString()
                return weekday.toUpperCase() + format.format(date.time)
            }

            override fun interpretTime(hour: Int): String {
                return if (hour > 11) (hour - 12).toString() + " PM" else if (hour == 0) "12 AM" else "$hour AM"
            }
        }
    }

    protected fun getEventTitle(time: Calendar): String {
        return String.format("Event of %02d:%02d %s/%d", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), time.get(Calendar.MONTH) + 1, time.get(Calendar.DAY_OF_MONTH))
    }

    override fun onEventClick(event: WeekViewEvent, eventRect: RectF) {
        Toast.makeText(this, "Clicked " + event.name, Toast.LENGTH_SHORT).show()
    }

    override fun onEventLongPress(event: WeekViewEvent, eventRect: RectF) {
        Toast.makeText(this, "Long pressed event: " + event.name, Toast.LENGTH_SHORT).show()
    }

    override fun onEmptyViewLongPress(time: Calendar) {
        Toast.makeText(this, "Empty view long pressed: " + getEventTitle(time), Toast.LENGTH_SHORT).show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){

            android.R.id.home->{

                if(!flag){

                    flag=true
                    drawerLayout.openDrawer(GravityCompat.START)

                }else{

                    flag=false
                    drawerLayout.closeDrawer(GravityCompat.START)

                }

                return true

            }

        }

        return super.onOptionsItemSelected(item);

    }

    override fun onNavigationItemSelected(p0: MenuItem): Boolean {

        when(p0.itemId){

            R.id.action_today -> {
                weekView!!.goToToday()
            }
            R.id.action_day_view -> {
                if (mWeekViewType != TYPE_DAY_VIEW) {
                    p0.isChecked = !p0.isChecked
                    mWeekViewType = TYPE_DAY_VIEW
                    weekView!!.numberOfVisibleDays = 1

                    // Lets change some dimensions to best fit the view.
                    weekView!!.columnGap = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, resources.displayMetrics).toInt()
                    weekView!!.textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12f, resources.displayMetrics).toInt()
                    weekView!!.eventTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12f, resources.displayMetrics).toInt()
                }
            }
            R.id.action_three_day_view -> {
                if (mWeekViewType != TYPE_THREE_DAY_VIEW) {
                    p0.isChecked = !p0.isChecked
                    mWeekViewType = TYPE_THREE_DAY_VIEW
                    weekView!!.numberOfVisibleDays = 3

                    // Lets change some dimensions to best fit the view.
                    weekView!!.columnGap = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, resources.displayMetrics).toInt()
                    weekView!!.textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12f, resources.displayMetrics).toInt()
                    weekView!!.eventTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12f, resources.displayMetrics).toInt()
                }
            }
            R.id.action_week_view -> {
                if (mWeekViewType != TYPE_WEEK_VIEW) {
                    p0.isChecked = !p0.isChecked
                    mWeekViewType = TYPE_WEEK_VIEW
                    weekView!!.numberOfVisibleDays = 7

                    // Lets change some dimensions to best fit the view.
                    weekView!!.columnGap = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2f, resources.displayMetrics).toInt()
                    weekView!!.textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10f, resources.displayMetrics).toInt()
                    weekView!!.eventTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10f, resources.displayMetrics).toInt()
                }
            }

        }

        p0.setChecked(true)

        drawerLayout.closeDrawer(GravityCompat.START)

        return true;

    }

    companion object {

        //Display shedule base on three types
        //Type is (the number of day)
        private val TYPE_DAY_VIEW = 1
        private val TYPE_THREE_DAY_VIEW = 2
        private val TYPE_WEEK_VIEW = 3
    }

}