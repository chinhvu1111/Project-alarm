package com.e15.alarmnats.activity

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.graphics.ColorUtils
import androidx.fragment.app.FragmentTransaction
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem
import com.e15.alarmnats.Database.ReminderDatabase
import com.e15.alarmnats.R
import com.e15.alarmnats.fragment.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class EisenHowerActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var important: TextView;

    lateinit var notimportant: TextView;

    lateinit var bottomNavigation: AHBottomNavigation;

    lateinit var fab: FloatingActionButton;

    lateinit var dbHandler:ReminderDatabase

    lateinit var firebaseDatabase:FirebaseDatabase

    lateinit var eventDatabase:DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eisen_hower)

        fab = findViewById(R.id.fab_add)

        fab.setOnClickListener(this)

        firebaseDatabase= FirebaseDatabase.getInstance()

        eventDatabase=firebaseDatabase.getReference("Events")

        initUI()

        var fragmentTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()

        fragmentTransaction.replace(
                R.id.navigationTab,
                DisplayClassifyTasksFragment()
        ).addToBackStack("name")

        fragmentTransaction.commit()
    }

    override fun onResume() {

        Log.e("-----------", "The activity is resumed!")

        super.onResume()
    }

    fun initUI() {

        dbHandler= ReminderDatabase(applicationContext)

        var resutIntent=intent;

        var id=resutIntent.getStringExtra("id");

        var remainingTimer:Long=resutIntent.getLongExtra("remainTime",0)

        var isDone=resutIntent.getBooleanExtra("done",false)

        if(id!=null){

            var backEvent=dbHandler!!.getEventId(id)

            backEvent?.remainingTime=remainingTimer

            if(isDone&&remainingTimer==0.toLong()){

                backEvent!!.isDone=1;

                eventDatabase.ref.child("$id/remainingTime").setValue(backEvent.remainingTime)
                eventDatabase.ref.child("$id/done").setValue(backEvent.isDone)

                dbHandler!!.updateEvent(backEvent!!);

                Toast.makeText(applicationContext,"Tác vụ hoàn thành!", Toast.LENGTH_SHORT).show()

//            allEventsFragment!!.refreshEvents()

//            categoryFragment!!.refresh(Item(backEvent.title!!, "", Event.EVENT_TYPE, false))

            }else{

                eventDatabase.ref.child("$id/remainingTime").setValue(backEvent!!.remainingTime)

                dbHandler!!.updateEvent(backEvent!!);

                Toast.makeText(applicationContext, "Cập nhập xong tác vụ còn lại ${splitToComponentTimes(remainingTimer / 1000)}!", Toast.LENGTH_SHORT).show()

            }

        }

        bottomNavigation = findViewById(R.id.bottom_navigation)

        var home = AHBottomNavigationItem(R.string.home, R.drawable.ic_action_name, R.color.home)
        var noteAdd = AHBottomNavigationItem(R.string.displayDone, R.drawable.ic_done_black_24dp, R.color.addNote)
        var search = AHBottomNavigationItem(R.string.search, R.drawable.ic_search, R.color.search)
        var setting = AHBottomNavigationItem(R.string.setting, R.drawable.ic_settings_applications, R.color.setting)

        bottomNavigation.addItem(home)
        bottomNavigation.addItem(noteAdd)
        bottomNavigation.addItem(search)
        bottomNavigation.addItem(setting)

        bottomNavigation.setDefaultBackgroundColor(Color.parseColor("#FEFEFE"));

        bottomNavigation.isBehaviorTranslationEnabled = false

        bottomNavigation.accentColor = Color.parseColor("#F63D2B")

        bottomNavigation.isForceTint = true

        bottomNavigation.isTranslucentNavigationEnabled = true

        bottomNavigation.titleState = AHBottomNavigation.TitleState.SHOW_WHEN_ACTIVE
//        bottomNavigation.titleState=AHBottomNavigation.TitleState.ALWAYS_SHOW
//        bottomNavigation.titleState=AHBottomNavigation.TitleState.ALWAYS_HIDE

        bottomNavigation.isColored = true

        bottomNavigation.setCurrentItem(0)

        bottomNavigation.setNotificationBackgroundColor(Color.parseColor("#F63D2B"))

        //Adding and remove notification for each item
//        bottomNavigation.setNotification("1",3)

//        var notification=AHNotification.Builder().setText("1")

        bottomNavigation.setOnTabSelectedListener(object : AHBottomNavigation.OnTabSelectedListener {
            override fun onTabSelected(position: Int, wasSelected: Boolean): Boolean {

                when (position) {

                    0 -> {
                        var fragmentTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()

                        fragmentTransaction.replace(
                                R.id.navigationTab,
                                DisplayClassifyTasksFragment()
                        )

//                        fragmentTransaction.addToBackStack("")

                        fragmentTransaction.commit()

                    }

                    1->{
                        var fragmentTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()

                        fragmentTransaction.replace(
                                R.id.navigationTab,
                                DisplayDoneTaskFragment()
                        )

//                        fragmentTransaction.addToBackStack("")

                        fragmentTransaction.commit()
                    }

                    2->{
                        var fragmentTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()

                        fragmentTransaction.replace(
                                R.id.navigationTab,
                                SearchTaskFragment()
                        )

//                        fragmentTransaction.addToBackStack("")

                        fragmentTransaction.commit()

                    }

                    3->{

                        var fragmentTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()

                        fragmentTransaction.replace(
                                R.id.navigationTab,
                                Setting_general_EIS_Fragment()
                        )

//                        fragmentTransaction.addToBackStack("")

                        fragmentTransaction.commit()

                    }

                }

                return true
            }
        })

        bottomNavigation.setOnNavigationPositionListener(object : AHBottomNavigation.OnNavigationPositionListener {

            override fun onPositionChange(y: Int) {

                Toast.makeText(applicationContext, "This is tasks", Toast.LENGTH_SHORT).show()

            }

        })

    }

    fun splitToComponentTimes(biggy: Long): String {

        val longVal = biggy
        val hours = longVal / 3600
        var remainder = longVal - hours * 3600
        val mins = remainder / 60
        remainder = remainder - mins * 60
        val secs = remainder

        return "$hours giờ $mins phút và $secs giây"

    }

    fun donow(view: View) {

        Toast.makeText(applicationContext, "donow click!", Toast.LENGTH_SHORT).show()

    }

    fun dolater(view: View) {

    }

    fun arrange(view: View) {

    }

    fun help(view: View) {

    }

    fun ignore(view: View) {

    }

    fun randomColor(): Int {
        var TEMP_HSL = floatArrayOf(0.toFloat(), 0.toFloat(), 0.toFloat());

        var hsl = TEMP_HSL;

        hsl[0] = (Math.random() * 360).toFloat()
        hsl[1] = (40 + Math.random() * 60).toFloat()
        hsl[2] = (40 + Math.random() * 60).toFloat()
        return ColorUtils.HSLToColor(hsl)

    }

    fun showBottomSheet() {

        ActionBottomDialogFragment.contexts = applicationContext

        ActionBottomDialogFragment().show(supportFragmentManager, "Chinhvu")

    }

    override fun onClick(v: View?) {

        when (v!!.id) {

            R.id.fab_add -> {

                showBottomSheet()

            }

        }

    }

    override fun onPause() {

        Log.e("-----------", "The activity is paused!")

        super.onPause()
    }
}
