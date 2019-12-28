package com.e15.alarmnats.activity

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.core.view.get
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.e15.alarmnats.Database.ReminderDatabase
import com.e15.alarmnats.Model.*
import com.e15.alarmnats.R
import com.e15.alarmnats.adapter.AllEventsAdapter
import com.e15.alarmnats.adapter.EventsAdapter
import com.e15.alarmnats.fragment.AllEventsFragment
import com.e15.alarmnats.fragment.CategoryDialogFragment
import com.e15.alarmnats.fragment.CategoryFragment
import com.e15.alarmnats.fragment.StatTasksFragment
import com.e15.alarmnats.fragment.TaskBaseOnCustomCategoryFragment.Companion.context
import com.e15.alarmnats.utils.AlarmReceiver
import com.e15.alarmnats.utils.Utils
import com.e15.alarmnats.view.ColorCircle
import com.gigamole.navigationtabstrip.NavigationTabStrip
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import kotlinx.android.synthetic.main.activity_group_task.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class TaskManagementActivity : AppCompatActivity(), View.OnClickListener,
        EventsAdapter.HideOrShowListener,
        DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener,
        NotifyInterface,
        AllEventsAdapter.HideOrShowListener, NumberPicker.OnValueChangeListener {
    private var mViewPager: ViewPager? = null

    private var mCenterNavigationTabStrip: NavigationTabStrip? = null

    lateinit var adapterViewPager: FragmentStatePagerAdapter

    lateinit var toolbar: Toolbar

    var slidingUpPanelLayout: SlidingUpPanelLayout? = null

    var flag = false
    var isEdit = false
    var isNotify = true
    var repeatCount: String? = null
    var repeatType: String? = null
    var repeatTime: Long = 0
    val REPEAT_MODE_ON = 0

    val REPEAT_MODE_OFF = 1
    val milMinute = 60000L
    val milHour = 3600000L
    var item: Event? = null

    var categoryColorIcon: ColorCircle? = null
    var tvAddOrEdit: TextView? = null
    var tvCategoryTitle: TextView? = null
    var tvHour: TextView? = null
    var tvMinute: TextView? = null
    var tvDay: TextView? = null
    var tvMonth: TextView? = null
    var tvYear: TextView? = null
    var tv_repeat_count: TextView? = null
    var tv_repeat_type: TextView? = null
    var repeat_control_switch: Switch? = null
    var eventTitle: EditText? = null
    var eventPlace: EditText? = null
    var eventDetail: EditText? = null
    var ic_notify: ImageButton? = null
    var startHour: Int = 0
    var startMinute: Int = 0
    var endHour: Int = -1;
    var endMinute: Int = -1;
    var day: Int = 0
    var month: Int = 0
    var year: Int = 0
    var dbHandler: ReminderDatabase? = null
    var categoryFragment: CategoryFragment? = null
    var allEventsFragment: AllEventsFragment? = null
    lateinit var statEventFragment: StatTasksFragment
    var receiver: AlarmReceiver? = null
    //    lateinit var tvEdit: TextView
//    lateinit var tvAddCategory: TextView
    var isEditMode = false
    lateinit var rbnStartTime: RadioButton;

    lateinit var rbnEndTime: RadioButton;
//    var currentFragment: String? = null
    //
    lateinit var resources1: Resources

    lateinit var cvisUrgent: CardView;

    lateinit var cvisImportant: CardView;

    lateinit var listStatesUrgent: Array<String>

    lateinit var listStatesImportant: Array<String>
    var resultState = intArrayOf(0, 0)

    lateinit var tvisUrgent: TextView

    lateinit var tvisImportant: TextView

    var finalResult = 0;

    var currentParentId:String=""

    var currentlevel: Int = 0

    lateinit var lnTimer: LinearLayout

    lateinit var lnDateTime: LinearLayout

    lateinit var lnSwitchTimer: LinearLayout

    lateinit var rdRemind: RadioButton

    lateinit var rdnonRemind: RadioButton

    lateinit var btnSetIntervalTime: Button

    lateinit var intervaltimeLayout: LinearLayout

    lateinit var intervalhour: TextView

    lateinit var intervalminute: TextView

    var currentCreatingRemainingTimer: Long = -1

    lateinit var itemBeforeUpdating: Event

    lateinit var databaseEvents:DatabaseReference

    lateinit var mhandler:Handler

    lateinit var firebaseDatabase:FirebaseDatabase

    lateinit var eventDatabase:DatabaseReference

    lateinit var databaseUser:DatabaseReference

    lateinit var groupDatabase:DatabaseReference

    var currentUser:FirebaseUser?=null

    lateinit var queryGetIdUser:Query

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_demo_tab)

        //Create instance of database
        dbHandler = ReminderDatabase(applicationContext)

        firebaseDatabase= FirebaseDatabase.getInstance()

        eventDatabase=firebaseDatabase.getReference("Events")

        //Nested listener event into returned Thread
        currentUser= FirebaseAuth.getInstance().currentUser

        if(currentUser==null){

            Toast.makeText(applicationContext,"Bạn chưa đăng nhập",Toast.LENGTH_SHORT).show()

            var intent=Intent(this,LoginActivity::class.java)

            startActivity(intent)

            finish()

            return

        }

        databaseUser=firebaseDatabase.getReference("User")

        databaseEvents=firebaseDatabase.getReference("Events")

        queryGetIdUser=databaseUser.orderByChild("email").equalTo(currentUser?.email)

        groupDatabase=firebaseDatabase.getReference("Group")

        initUI()
        setUI()

        slidingUpPanelLayout = findViewById<View>(R.id.sliding_layout) as SlidingUpPanelLayout
//        btnAll = findViewById<View>(R.id.btnAll) as Button
//        btnCategory = findViewById<View>(R.id.btnCategory) as Button
//        ic_setting = findViewById<View>(R.id.setting_icon) as ImageView
        tvAddOrEdit = findViewById<View>(R.id.addoredit) as TextView
        tvCategoryTitle = findViewById<View>(R.id.tvCategoryTitle) as TextView
        eventTitle = findViewById<View>(R.id.eventTitle) as EditText
        eventPlace = findViewById<View>(R.id.eventPlace) as EditText
        eventDetail = findViewById<View>(R.id.eventDetail) as EditText
        categoryColorIcon = findViewById<View>(R.id.categoryColorIcon) as ColorCircle?
        ic_notify = findViewById<View>(R.id.notifyBtn) as ImageButton
        tvHour = findViewById<View>(R.id.hour) as TextView
        tvMinute = findViewById<View>(R.id.minute) as TextView
        tvDay = findViewById<View>(R.id.day) as TextView
        tvMonth = findViewById<View>(R.id.month) as TextView
        tvYear = findViewById<View>(R.id.year) as TextView
//        tvEdit = findViewById<View>(R.id.tvEdit) as TextView
//        tvAddCategory = findViewById<View>(R.id.tvAddCategory) as TextView
        tv_repeat_count = findViewById<View>(R.id.tv_repeat_count) as TextView
        tv_repeat_type = findViewById<View>(R.id.tv_repeat_type) as TextView
        repeat_control_switch = findViewById<View>(R.id.repeat_control_switch) as Switch

        lnTimer = findViewById(R.id.lnTimer)

        lnDateTime = findViewById(R.id.lnDateTime)

        rbnStartTime = findViewById(R.id.startTime)
        rbnEndTime = findViewById(R.id.endTime)

        rdRemind = findViewById(R.id.rdRemind)

        rdnonRemind = findViewById(R.id.rdnonRemind)

        btnSetIntervalTime = findViewById(R.id.btnSetIntervalTime)

        btnSetIntervalTime.isVisible = false

        intervaltimeLayout = findViewById(R.id.intervaltimeLayout)

        intervaltimeLayout.isVisible = false

        intervalhour = findViewById(R.id.intervalhour)

        intervalminute = findViewById(R.id.intervalminute)

        lnSwitchTimer = findViewById(R.id.lnSwitchTimer)

        btnSetIntervalTime.setOnClickListener(this)

        rdRemind.setOnClickListener(this)

        rdnonRemind.setOnClickListener(this)

        rbnStartTime.setOnClickListener(this)
        rbnEndTime.setOnClickListener(this)

        resources1 = getResources()


        //Recieve class
        receiver = AlarmReceiver()

        //Button (listener)

        //textView (listener)
//        tvEdit.setOnClickListener(ClickListener())
//        tvAddCategory.setOnClickListener(ClickListener())

        //Displays a button with an image (instead of text) that can be pressed or clicked by the user.
        // By default, an ImageButton looks like a regular Button ,
        // with the standard button background that changes color during different button states.
        val ic_close = findViewById<View>(R.id.close_btn) as ImageButton
        ic_close.setOnClickListener {
            //Hide slidingPaneLayout
            slidingUpPanelLayout!!.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN)

            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

            //getCurrentFocus
            //Calls Window.getCurrentFocus on the Window of this Activity
            // to return the currently focused
            imm.hideSoftInputFromWindow(getCurrentFocus()!!.getWindowToken(), 0)

        }

        //Change image of the bell notification
        ic_notify!!.setOnClickListener(View.OnClickListener {
            if (isNotify) {
                ic_notify!!.setImageResource(R.drawable.ic_notifications_off_white_24dp)
                isNotify = false
            } else {
                ic_notify!!.setImageResource(R.drawable.ic_notifications_active_white_24dp)
                isNotify = true
            }
        })


        //Dimm this layout
        slidingUpPanelLayout!!.setFadeOnClickListener(View.OnClickListener {
            //Change panel state to the (given state) with
            slidingUpPanelLayout!!.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN)

        })

        //If it is not hidden, it will hide
        if (slidingUpPanelLayout!!.getPanelState() != SlidingUpPanelLayout.PanelState.HIDDEN) {
            slidingUpPanelLayout!!.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN)
        }

        listStatesUrgent = resources.getStringArray(R.array.isUrgent)
        listStatesImportant = resources.getStringArray(R.array.isImportant)

        tvisUrgent = findViewById(R.id.isUrgent)

        tvisImportant = findViewById(R.id.isImportant)

        cvisUrgent = findViewById(R.id.cvisUrgent)

        cvisUrgent.setOnClickListener(object : View.OnClickListener {

            override fun onClick(v: View?) {

                var builder: AlertDialog.Builder = AlertDialog.Builder(this@TaskManagementActivity, R.style.MyDialogTheme)

                builder.setTitle(R.string.dialog_title)

                //This here we can custom multiple choice or single choice if we want
//                builder.setMultiChoiceItems(listStates,checkUrgentState,DialogInterface.OnMultiChoiceClickListener { dialog, which, isChecked ->
//
//                    if(isChecked){
//
//                        tvisUrgent.setText(listStates.get(which))
//
//                    }else{
//
//                    }
//
//                })

                var isUrgent: String = "Khẩn cấp"

                builder.setSingleChoiceItems(listStatesUrgent, -1, DialogInterface.OnClickListener { dialog, which ->

                    when (which) {

                        0 -> {

                            isUrgent = "Khẩn cấp"

                            for (i in 0..resultState.size - 1) {

                                resultState[i] = 0

                            }

                        }
                        1 -> {

                            isUrgent = "Không khẩn cấp"

                            for (i in 0..resultState.size - 1) {

                                resultState[i] = 1

                            }

                        }

                    }

                })

                builder.setCancelable(false)

                builder.setPositiveButton(R.string.ok_label, DialogInterface.OnClickListener { dialog, which ->

                    tvisUrgent.setText(isUrgent)

                })

                builder.setNegativeButton(R.string.dismiss_label, DialogInterface.OnClickListener { dialog, which ->
                    dialog.dismiss()
                })

                var mdialog = builder.create()

                mdialog.show()

            }

        })

        cvisImportant = findViewById(R.id.cvisImportant)

        cvisImportant.setOnClickListener(object : View.OnClickListener {

            override fun onClick(v: View?) {

                var builder: AlertDialog.Builder = AlertDialog.Builder(this@TaskManagementActivity, R.style.MyDialogTheme)

                builder.setTitle(R.string.dialog_title)

                //This here we can custom multiple choice or single choice if we want
//                builder.setMultiChoiceItems(listStates,checkUrgentState,DialogInterface.OnMultiChoiceClickListener { dialog, which, isChecked ->
//
//                    if(isChecked){
//
//                        tvisUrgent.setText(listStates.get(which))
//
//                    }else{
//
//                    }
//
//                })

                var isImportant: String = "Quan trọng"

                builder.setSingleChoiceItems(listStatesImportant, -1, DialogInterface.OnClickListener { dialog, which ->

                    when (which) {

                        0 -> {

                            isImportant = "Quan trọng"

                            finalResult = 0

                        }
                        1 -> {

                            isImportant = "Không quan trọng"

                            finalResult = 1

                        }

                    }

                })

                builder.setCancelable(false)

                builder.setPositiveButton(R.string.ok_label, DialogInterface.OnClickListener { dialog, which ->

                    tvisImportant.setText(isImportant)

                })

                builder.setNegativeButton(R.string.dismiss_label, DialogInterface.OnClickListener { dialog, which ->
                    dialog.dismiss()
                })

                var mdialog = builder.create()

                mdialog.show()

            }

        })

    }

    inner class MyPagerAdapter : FragmentStatePagerAdapter {

        constructor(fm: FragmentManager) : super(fm)

        override fun getItem(position: Int): Fragment {

            when (position) {
                0 -> {

                    allEventsFragment = AllEventsFragment()

                    return allEventsFragment as AllEventsFragment
                }
                1 -> {

                    categoryFragment = CategoryFragment()

                    return categoryFragment as CategoryFragment

                }
                2 -> {

                    statEventFragment = StatTasksFragment()

                    return statEventFragment

                }
            }


            return Fragment()

        }


        override fun getCount(): Int {
            return 3;
        }

    }

    private fun initUI() {

        toolbar = findViewById(R.id.toolBarMain)

        setSupportActionBar(toolbar)

        if (supportActionBar != null) {
            var actionBar = supportActionBar

            actionBar!!.setTitle("Tác vụ")

        }

//        toolbar.setSubtitle("Test")

        adapterViewPager = MyPagerAdapter(supportFragmentManager)

        mViewPager = findViewById<View>(R.id.vp) as ViewPager

        mViewPager!!.setAdapter(adapterViewPager)
//        mTopNavigationTabStrip = findViewById<View>(R.id.nts_top) as NavigationTabStrip
        mCenterNavigationTabStrip = findViewById<View>(R.id.nts_center) as NavigationTabStrip
//        mBottomNavigationTabStrip = findViewById<View>(R.id.nts_bottom) as NavigationTabStrip

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.menu_task_management, menu)

        return true;

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {

            R.id.searchTask->{
                allEventsFragment!!.showSearchLayout()
            }

            R.id.addCategory -> {

                val dialog = CategoryDialogFragment.getInstance(Category(), false)

                dialog.show(supportFragmentManager, "ColorsDialog")

            }

            R.id.editItem -> {
                if (!isEditMode) {
                    categoryFragment!!.editCategory(true)
                } else if (isEditMode) {
                    categoryFragment!!.editCategory(false)
                }
                isEditMode = !isEditMode
            }

        }

        return false

    }

    private fun setUI() {


//        mViewPager!!.setAdapter(object : PagerAdapter() {
//
//            override fun getCount(): Int {
//                return 3
//            }
//
//            override fun isViewFromObject(view: View, `object`: Any): Boolean {
//                return view == `object`
//            }
//
//            override fun destroyItem(container: View, position: Int, `object`: Any) {
//                (container as ViewPager).removeView(`object` as View)
//            }
//
//            //Create the page for the (given position).
//            // The adapter is responsible for (adding the view) to the (container) given here,
//            // although it only must ensure this is done by the time it returns from finishUpdate(ViewGroup).
//            override fun instantiateItem(container: ViewGroup, position: Int): Any {
//                when(position){
//                    0->{
//                        return AllEventsFragment()
//                    }
//                    1->{
//                        return CategoryFragment();
//
//                    }
//                    2->{
//
//                    }
//                }
//                val view = View(baseContext)
//                container.addView(view)
//                return view
//
//            }
//        })

//        mTopNavigationTabStrip!!.setTabIndex(1, true)
        mCenterNavigationTabStrip!!.setViewPager(mViewPager, 1)
//        mBottomNavigationTabStrip!!.setTabIndex(1, true)

        //        final NavigationTabStrip navigationTabStrip = (NavigationTabStrip) findViewById(R.id.nts);
        //        navigationTabStrip.setTitles("Nav", "Tab", "Strip");
        //        navigationTabStrip.setTabIndex(0, true);
        //        navigationTabStrip.setTitleSize(15);
        //        navigationTabStrip.setStripColor(Color.RED);
        //        navigationTabStrip.setStripWeight(6);
        //        navigationTabStrip.setStripFactor(2);
        //        navigationTabStrip.setStripType(NavigationTabStrip.StripType.LINE);
        //        navigationTabStrip.setStripGravity(NavigationTabStrip.StripGravity.BOTTOM);
        //        navigationTabStrip.setTypeface("fonts/typeface.ttf");
        //        navigationTabStrip.setCornersRadius(3);
        //        navigationTabStrip.setAnimationDuration(300);
        //        navigationTabStrip.setInactiveColor(Color.GRAY);
        //        navigationTabStrip.setActiveColor(Color.WHITE);
        //        navigationTabStrip.setOnPageChangeListener(...);
        //        navigationTabStrip.setOnTabStripSelectedIndexListener(...);

        //Updating done if done
        var resutIntent = intent;

        var id = resutIntent.getStringExtra("id");

        var remainingTimer: Long = resutIntent.getLongExtra("remainTime", 0)

        var isDone = resutIntent.getBooleanExtra("done", false)

        if (id != null) {

            var backEvent = dbHandler!!.getEventId(id)

            backEvent?.remainingTime = remainingTimer

            if (isDone && remainingTimer == 0.toLong()) {

                backEvent!!.isDone = 1;

                eventDatabase.ref.child("$id/remainingTime").setValue(backEvent.remainingTime)
                eventDatabase.ref.child("$id/done").setValue(backEvent.isDone)

                dbHandler!!.updateEvent(backEvent!!);

                Toast.makeText(applicationContext, "Tác vụ hoàn thành!", Toast.LENGTH_SHORT).show()

//            allEventsFragment!!.refreshEvents()

//            categoryFragment!!.refresh(Item(backEvent.title!!, "", Event.EVENT_TYPE, false))

            } else {

                eventDatabase.ref.child("$id/remainingTime").setValue(backEvent!!.remainingTime)

                dbHandler!!.updateEvent(backEvent!!);

                Toast.makeText(applicationContext, "Cập nhập xong tác vụ còn lại ${splitToComponentTimes(remainingTimer / 1000)}!", Toast.LENGTH_SHORT).show()

            }

        }

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

    //show repeat dialog when click repeat interval layout
    fun showRepeatIntervalDialog(view: View) {
        val items = arrayOfNulls<String>(5)
        items[0] = "5 " + getResources().getString(R.string.Min)
        items[1] = "10 " + getResources().getString(R.string.Min)
        items[2] = "15 " + getResources().getString(R.string.Min)
        items[3] = "30 " + getResources().getString(R.string.Min)
        items[4] = "1 " + getResources().getString(R.string.Hour)

        // Create List Dialog
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Choose")
        builder.setItems(items) { dialog, position ->
            val item = items[position]
            val repeat_info = item!!.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            repeatCount = repeat_info[0]
            repeatType = repeat_info[1]
            if (repeatType == resources1.getString(R.string.Min)) {
                repeatTime = Integer.parseInt(repeatCount) * milMinute
            } else if (repeatType == resources1.getString(R.string.Hour)) {
                repeatTime = Integer.parseInt(repeatCount) * milHour
            }

            tv_repeat_count!!.setText(repeatCount)
            tv_repeat_type!!.setText(repeatType)
        }
        val alert = builder.create()
        alert.show()
    }

    internal inner class SearchClickListener : View.OnClickListener {


        override fun onClick(v: View) {
            //show search edittext
            allEventsFragment!!.showSearchLayout()
//            tvEdit.isClickable = false
        }
    }

    //handle events
    override fun onClick(v: View) {
        when (v.id) {

            R.id.endTime -> {

                Toast.makeText(applicationContext, "EndTime", Toast.LENGTH_SHORT).show()

                if (item!!.endTime == null) return;

                //because when clicking we will chech isCheck --> OnTimeSet() --> This here is not important
                //If this here is edit mode
                //then startTime and endTime will be changed when click switch
                if(isEdit){

                    val endTime = item!!.endTime!!.trim().split(":")

                    endHour = Integer.parseInt(endTime[0])
                    endMinute = Integer.parseInt(endTime[1])

                    tvHour!!.setText((if (endHour < 12) endHour else endHour - 12).toString())
                    tvMinute!!.setText((if (endMinute > 9) endMinute else "0$endMinute").toString())

                }

            }
            R.id.startTime -> {

                Toast.makeText(applicationContext, "StartTime", Toast.LENGTH_SHORT).show()

                if (item!!.startTime == null) return;

                //If this here is edit mode
                //then startTime and endTime will be changed when click switch
                if(isEdit){

                    val startTime = item!!.startTime!!.trim().split(":");

                    startHour = Integer.parseInt(startTime[0])
                    startMinute = Integer.parseInt(startTime[1])

                    tvHour!!.setText((if (startHour < 12) startHour else startHour - 12).toString())

                    tvMinute!!.setText((if (startMinute > 9) startMinute else "$startMinute").toString())

                }

            }
            R.id.rdRemind -> {

                var fade_in = AnimationUtils.loadAnimation(applicationContext, R.anim.fade_in)

                var s = AnimationSet(false)

                s.addAnimation(fade_in)

                s.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationRepeat(animation: Animation?) {

                    }

                    override fun onAnimationStart(animation: Animation?) {

                        lnDateTime.isVisible = true

                        lnDateTime.isClickable = true

                        lnTimer.isVisible = true

                        lnTimer.isClickable = true

                        lnSwitchTimer.isVisible = true

                        lnSwitchTimer.isClickable = true

                        //Setting interval time
                        btnSetIntervalTime.isVisible = false

                        btnSetIntervalTime.isClickable = false

                        intervaltimeLayout.isVisible = false

                        intervaltimeLayout.isClickable = false

                        intervalhour.text = "0"

                        intervalminute.text = "0"

                    }

                    override fun onAnimationEnd(animation: Animation?) {

                    }
                })

                s.fillAfter = true

                lnDateTime.startAnimation(s)

                lnTimer.startAnimation(s)

            }
            R.id.rdnonRemind -> {

                currentCreatingRemainingTimer=-1

                var fade_out = AnimationUtils.loadAnimation(applicationContext, R.anim.fade_out)

                var s = AnimationSet(false)

                s.addAnimation(fade_out)

                s.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationRepeat(animation: Animation?) {

                    }

                    override fun onAnimationStart(animation: Animation?) {
                    }

                    override fun onAnimationEnd(animation: Animation?) {

                        lnDateTime.isVisible = false
                        lnDateTime.isClickable = false

                        lnTimer.isVisible = false
                        lnTimer.isClickable = false

                        lnSwitchTimer.isVisible = false
                        lnSwitchTimer.isClickable = false

                        btnSetIntervalTime.isVisible = true
                        btnSetIntervalTime.isClickable = true

                        intervaltimeLayout.isVisible = true
                        intervaltimeLayout.isClickable = true

                    }
                })

                s.fillAfter = true

                lnDateTime.startAnimation(s)

                lnTimer.startAnimation(s)

            }

            R.id.btnSetIntervalTime -> {

                showChoosingDialog()

            }

            else -> {

            }
        }
    }

    override fun onValueChange(picker: NumberPicker?, oldVal: Int, newVal: Int) {

    }

    fun showChoosingDialog() {

        //get the (parent event) of current event
        var event = dbHandler!!.getEventId(currentParentId)

        var d = Dialog(this, R.style.MyDialogTheme)

        d.setTitle("Chọn khoảng thời gian")

        d.setContentView(R.layout.dialog_interval_time)

        var settingIntervalTime = d.findViewById<Button>(R.id.btnSettingIntervalTime)
        var cancelIntervalTime = d.findViewById<Button>(R.id.btnCancel)

        var hourPicker = d.findViewById<NumberPicker>(R.id.hourPicker)

        var minutePicker = d.findViewById<NumberPicker>(R.id.minutePicker)

        if(event!=null){

            hourPicker.maxValue = (event!!.remainingTime / 1000 / 60 / 60).toInt()

            minutePicker.maxValue = (event!!.remainingTime / 1000 / 60).toInt()

        }else{

            hourPicker.maxValue = 23

            minutePicker.maxValue = 59

        }

        var totalTimeofChilds=caculatingAllTimeOfChilds(item!!)

        //If updating then we need get all time (endtime - starttime) of childs
        //To set min value of pioker
        hourPicker.minValue = (totalTimeofChilds/10000/60/60).toInt()

        minutePicker.minValue = ((totalTimeofChilds-hourPicker.minValue*60*60*1000)/60/1000).toInt()

        hourPicker.wrapSelectorWheel = true

        minutePicker.wrapSelectorWheel = true

        settingIntervalTime.setOnClickListener(object : View.OnClickListener {

            override fun onClick(v: View?) {

                intervalhour.text = hourPicker.value.toString()

                intervalminute.text = minutePicker.value.toString()

                var totalTimer = intervalhour.text.toString().toLong() * 60 * 60 * 1000 + intervalminute.text.toString().toLong() * 60 * 1000

                if (totalTimer < 1000 * 60) {

                    Toast.makeText(applicationContext, "Khoảng thời gian quá nhỏ để tạo tác vụ, Xin mời nhập lại!", Toast.LENGTH_SHORT).show()

                    return

                }

//                event.remainingTime-=intervalhour.text.toString().toLong()*60*60*1000-intervalminute.text.toString().toLong()*60*1000

                d.dismiss()

            }
        })

        cancelIntervalTime.setOnClickListener(object : View.OnClickListener {

            override fun onClick(v: View?) {

                d.dismiss()

            }

        })

        d.show()

    }

    //Choose a theme
    fun createSettingDialog() {

        val apptheme = Utils.getThemeStyle(this)

        //Hold Array data in the String xml defined before
        var types_of_themes = resources1.getStringArray(R.array.type_of_theme)

        val builder = AlertDialog.Builder(this)

        builder.setTitle(resources1.getString(R.string.choose_theme))

        //Set a list of items to be displayed in the dialog as the content,
        // you will be notified of the selected item via the supplied listener.
        // The list will have a check mark displayed to the right of the text for the checked item.
        // Clicking on an item in the list will not dismiss the dialog. Clicking on a button will dismiss the dialog.
        builder.setSingleChoiceItems(types_of_themes, apptheme) { dialog, theme ->
            //change theme
            // with theme is parameter correspond to Theme that you want to change
            //infer this activity always read theme
            Utils.updateThemeStyle(theme, this)

            //call recreate (current Activity) to apply changing theme
            this.recreate()
        }
        val alert = builder.create()
        alert.show()
    }

    //This method is called indirectly via the (ViewHolder) of the (EventAdapter)
    //(event listener) from (all events adapter) when you click any (event button)
    override fun setHideOrShow(isEdit: Boolean, item: Event) {
        showEditLayout(item, isEdit)
    }


    override fun deleteEventCallBack() {
        allEventsFragment!!.showCreateEventMessage()
    }

    //event listener from category and event adapter
    override fun setHideOrShow(item: Event, isEdit: Boolean) {
        showEditLayout(item, isEdit)
    }

    override fun continueTask(item: Event) {

        var event: Event = Event(0)

        event = dbHandler!!.getEventId(item.hashId)!!

        val i = Intent(applicationContext, TimerActivity::class.java)

        i.putExtra("id", event.hashId);
        i.putExtra("date", event!!.date);
        i.putExtra("startTime", event.startTime);
        i.putExtra("endTime", event.endTime);
        i.putExtra("title", event.title)
        i.putExtra("description", event.description);
        i.putExtra("enabled", event.enabled);

        i.putExtra("taskIsDone", event.isDone)
        i.putExtra("remainingTime", event.remainingTime)
        i.putExtra("fromClass", "TaskManagement")

        i.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP

        startActivity(i)

        finish()

    }

    //This here is current item that we have (clicked at) before
    //This function we pass item to add, update (must add)
    override fun createNewSubTask(currentitem: Event, level: Int) {

        currentParentId = currentitem.hashId

        currentlevel = level

        //This function relate to Category of task
        //If we pass subtask --> Category = item.category
        //If we pass task(Category) --> Category = item.title
        showEditLayout(currentitem, false)

    }

    fun showEditLayout(item: Event, isEdit: Boolean) {

        this.item = item

        //Set slidingUpPaneLayout expand as you want
        slidingUpPanelLayout!!.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED)

        //Set (sliding enabled flag)
        slidingUpPanelLayout!!.setTouchEnabled(false)

        //clear text in all inputText
        eventTitle!!.setText("")
        eventPlace!!.setText("")
        eventDetail!!.setText("")

        if (isEdit) {

            //We save before item by assigning via createSubTask
            itemBeforeUpdating = item

            //Then update all value of currentItem
            ////If we pass subtask --> Category = item.category
            //If we pass task(Category) --> Category = item.title
            if(currentParentId.equals("")) tvCategoryTitle!!.setText(item.title)
            else{
                tvCategoryTitle!!.setText(item.category)
            }

            tvAddOrEdit!!.setText("Sửa tác vụ")
            eventTitle!!.setHint(item.title)
            eventPlace!!.setHint(item.place)
            eventDetail!!.setHint(item.description)
            categoryColorIcon!!.setColor(Color.parseColor(item.color))

            currentParentId = item.parentId

            currentlevel = item.levelRecusion

            if (item.notify.equals("on")) {
                ic_notify!!.setImageResource(R.drawable.ic_notifications_active_white_24dp)
                isNotify = true
            } else {
                ic_notify!!.setImageResource(R.drawable.ic_notifications_off_white_24dp)
                isNotify = false
            }

            if (item.repeatMode.equals("on")) {
                repeat_control_switch!!.setChecked(true)
            } else if (item.repeatMode.equals("off")) {
                repeat_control_switch!!.setChecked(false)
            }

            tv_repeat_type!!.setText(item.repeatType)
            tv_repeat_count!!.setText(item.repeatCount)

            val startTime = item.startTime!!.trim().split(":")
            val endTime = item.endTime!!.trim().split(":")
            val date = item.date!!.trim().split("/")

            startHour = Integer.parseInt(startTime[0])
            startMinute = Integer.parseInt(startTime[1])

            endHour = Integer.parseInt(endTime[0])
            endMinute = Integer.parseInt(endTime[1])

            day = Integer.parseInt(date[1])
            month = Integer.parseInt(date[0]) - 1
            year = Integer.parseInt(date[2])
            repeatType = item.repeatType
            repeatCount = item.repeatCount


        } else if (!isEdit) {

            //If adding task then init (all default values)
            //Just hold title --> Category

            //Then update all value of currentItem
            ////If we pass subtask --> Category = item.category
            //If we pass task(Category) --> Category = item.title
            if(currentParentId.equals("")) tvCategoryTitle!!.setText(item.title)
            else{
                tvCategoryTitle!!.setText(item.category)
            }
            tvAddOrEdit!!.setText("Tạo tác vụ mới")
            eventTitle!!.setHint("Tiêu đề")
            eventPlace!!.setHint("Địa điểm")
            eventDetail!!.setHint("Mô tả chi tiết")
            repeat_control_switch!!.setChecked(false)
            tv_repeat_type!!.setText(getResources().getString(R.string.Min))
            tv_repeat_count!!.setText("5")
            categoryColorIcon?.setColor(Color.parseColor(item.color))
            ic_notify!!.setImageResource(R.drawable.ic_notifications_active_white_24dp)
            isNotify = true

            //get currentTime
            val now = Calendar.getInstance()

            startHour = now.get(Calendar.HOUR_OF_DAY)
            startMinute = now.get(Calendar.MINUTE)
//
////            endHour = now.get(Calendar.HOUR_OF_DAY)
////            endMinute = now.get(Calendar.MINUTE)

            day = now.get(Calendar.DATE)
            month = now.get(Calendar.MONTH) + 1
            year = now.get(Calendar.YEAR)
            repeatType = getResources().getString(R.string.Min)
            repeatCount = "5"

        }

        tvHour!!.setText((if (startHour < 12) startHour else startHour - 12).toString())
        tvMinute!!.setText((if (startMinute > 9) startMinute else "0$startMinute").toString())
        tvDay!!.setText(day.toString())
        tvMonth!!.setText(Utils.months[month - 1])
        tvYear!!.setText(year.toString())

        flag = true
        this.isEdit = isEdit
    }


    //Show a (adding dialog)
    override fun showEditCategoryDialog(category: Category) {

        //Category object is passed via (Event adapter) base on (position)
        //This code is initized all arguments of bundle fragment
        val dialog = CategoryDialogFragment.getInstance(category, true)

        //Return the (FragmentManager) for interacting with fragments associated with (this activity).
        dialog.show(supportFragmentManager, "ColorsDialog")
    }

    //    If you press back
//    This event can be act as return (old start) of the (current activity)
    override fun onBackPressed() {

        if (isEditMode) {

            categoryFragment!!.editCategory(false)

            isEditMode = !isEditMode

        } else if (flag) {

            slidingUpPanelLayout!!.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN)

            eventTitle!!.setText("")

            eventPlace!!.setText("")

            eventDetail!!.setText("")

            flag = false

        } else {

            finish()

        }
    }

    //click date picker
    fun setDate(v: View) {
        val datePickerDialog = DatePickerDialog.newInstance(
                this,
                year,
                month,
                day
        )

        datePickerDialog.setThemeDark(true)
        datePickerDialog.show(fragmentManager, "DatePickerDialog")
    }

    //click time picker
    fun setTime(v: View) {
        val timePickerDialog = TimePickerDialog.newInstance(
                this,
                startHour,
                startMinute,
                false
        )

        timePickerDialog.setThemeDark(true)
        //Display the dialog, adding the fragment to the (given FragmentManager).
        // This is a convenience for explicitly (creating a transaction),
        // adding the fragment to it with the (given tag), and committing it.
        // This does not add the transaction to the (back stack). When the fragment is dismissed,
        // a new transaction will be executed to remove it from the activity.
        timePickerDialog.show(fragmentManager, "TimePickerDialog")
    }

    //This function is overrided from the (DatePickerDialog)
    //It is used to set all attributes involved (Date)
    override fun onDateSet(view: DatePickerDialog, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        month = monthOfYear + 1
        day = dayOfMonth
        this.year = year
        tvDay!!.setText(day.toString())
        tvMonth!!.setText(Utils.months[month - 1])
        tvYear!!.setText(year.toString())
    }


    //this function overrided from (TimePickerDialog)
    //This function is used to set (all attributes) involved (Time)
    override fun onTimeSet(view: RadialPickerLayout, hourOfDay: Int, minute: Int, second: Int) {

        if (rbnStartTime.isChecked) {

            startHour = hourOfDay

            this.startMinute = minute

            tvHour!!.setText((if (startHour < 12) startHour else startHour - 12).toString())

            tvMinute!!.setText((if (startMinute > 9) startMinute else "0$startMinute").toString())

        } else {
            endHour = hourOfDay

            this.endMinute = minute

            tvHour!!.setText((if (endHour < 12) endHour else endHour - 12).toString())

            tvMinute!!.setText((if (endMinute > 9) endMinute else "0$endMinute").toString())
        }

    }

    //When clicking the save button click in (event add layout)
    fun saveEvent(v: View) {

        var title = eventTitle!!.getText().toString().trim { it <= ' ' }
        val place = eventPlace!!.getText().toString().trim { it <= ' ' }
        val detail = eventDetail!!.getText().toString().trim { it <= ' ' }

        var actions = resources.getStringArray(R.array.action_type_events)

        var tempisUrgent: Boolean = false
        var tempisImportant: Boolean = false
        var tempLevel: String = ""

        if (finalResult == 0 && resultState[finalResult] == 0) {

            tempisUrgent = true
            tempisImportant = true

            tempLevel = actions[0]

        }
        if (finalResult == 0 && resultState[finalResult] == 1) {

            tempisUrgent = false

            tempisImportant = true

            tempLevel = actions[1]

        }
        if (finalResult == 1 && resultState[finalResult] == 0) {

            tempisUrgent = true
            tempisImportant = false

            tempLevel = actions[2]

        }
        if (finalResult == 1 && resultState[finalResult] == 1) {

            tempisUrgent = false
            tempisImportant = false

            tempLevel = actions[3]

        }

        if (title != "" && title.length != 0) {
            title = title.substring(0, 1).toUpperCase() + title.substring(1)

            //Saving new event
            if (!isEdit) {

                //If we choose setting interval time mode
                if (!intervalhour.text.toString().equals("0") || !intervalminute.text.toString().equals("0")) {

                    endHour = startHour + intervalhour.text.toString().toInt()

                    endMinute = startMinute + intervalminute.text.toString().toInt()

                    isNotify = false

                    currentCreatingRemainingTimer = intervalhour.text.toString().toLong() * 60 * 60 * 1000 +
                            intervalminute.text.toString().toLong() * 1000 * 60

                }

                //If endTime>startTime
                if (endMinute == -1 && intervalhour.text.toString().equals("") && intervalminute.text.toString().equals("")) {

                    Toast.makeText(applicationContext, "Xin hãy chọn thời gian kết thúc", Toast.LENGTH_SHORT).show()

                    return

                }

                var parentEvent: Event?

                var difference: Long = 0.toLong()

                //We check here to (except case) new task having level=0
                //And currentParentId=-1
                if (!currentParentId.equals("")) {

                    parentEvent = dbHandler!!.getEventId(currentParentId)

                    difference = (endHour.toLong() - startHour.toLong()) * 60 * 60 * 1000 + (endMinute.toLong() - startMinute.toLong()) * 60 * 1000

                    //If endTime - startTime > remainingTime of (the parent task)
                    if (difference > parentEvent!!.remainingTime) {

                        Toast.makeText(applicationContext, "Thời gian bạn nhập lớn quá thời gian còn lại của tác vụ cha, mời nhập lại!", Toast.LENGTH_SHORT).show()

                        return

                    }

                }

                var category:String=""

                if(!currentParentId.equals("")) category=item!!.category!!
                else{
                    category=item!!.title!!
                }

                val e = Event(title, detail, place, category,
                        if (startMinute > 9) "$startHour:$startMinute" else "$startHour:0$startMinute",
                        if (endMinute > 9) "$endHour:$endMinute" else "$endHour:0$endMinute",
                        "$month/$day/$year", Event.EVENT_TYPE, if (isNotify) "on"
                else "off", if (repeat_control_switch!!.isChecked()) "on" else "off",
                        this!!.repeatType!!, this!!.repeatCount!!, 0)

                e.isUrgent = tempisUrgent
                e.isImportant = tempisImportant
                e.level = tempLevel
                e.parentId = currentParentId
                e.levelRecusion = currentlevel
                e.remainingTime = currentCreatingRemainingTimer

                //If you don't enter endTime
                if (startHour > endHour || startHour == endHour && startMinute >= endMinute && intervalhour.text.toString().equals("") && intervalminute.text.toString().equals("")) {

                    Toast.makeText(applicationContext, "Nhập sai thời gian kết thúc, xin mời nhập lại!", Toast.LENGTH_SHORT).show()

                    return

                }

                //If we create a subtask with (setting interval time mode)
                //We must substract remaining time of parent task
                if (!currentParentId.equals("") && currentCreatingRemainingTimer != -1.toLong()) {

                    var eventParent = dbHandler!!.getEventId(currentParentId)

                    eventParent!!.remainingTime -= currentCreatingRemainingTimer

                    dbHandler!!.updateEvent(eventParent!!)

                    //Updating RemainingTime in Realtime database when creating subTask
                    eventDatabase.child("$currentParentId/remainingTime").setValue(eventParent.remainingTime)

                }

                //If we create subtask with startTime and endTime mode
                ////We must substract remaining time of parent task
                if (!currentParentId.equals("") && currentCreatingRemainingTimer == -1.toLong()) {

                    var eventParent = dbHandler!!.getEventId(currentParentId)

                    eventParent!!.remainingTime -= difference

                    dbHandler!!.updateEvent(eventParent!!)

                    //Updating RemainingTime in Realtime database when creating subTask
                    eventDatabase.child("$currentParentId/remainingTime").setValue(eventParent.remainingTime)

                }

                var shared=applicationContext.getSharedPreferences("AddingByAdmin",Context.MODE_PRIVATE)

                var hashIdUserADdingByAdmin=shared.getString("hashIdAddingByAdmin","")

                //Assigning hashId for new event
                //Using realtime database
                if(hashIdUserADdingByAdmin.equals("")) createEventRealtimedatabase(e)
                else{

                    createEventAddingByAdmin(e,hashIdUserADdingByAdmin)

                    var edit=applicationContext.getSharedPreferences("AddingByAdmin",Context.MODE_PRIVATE).edit()

                    edit.putString("hashIdAddingByAdmin","")

                    edit.commit()

                }

                listenerRealtimeDatabase()

                //Row is the number of tuples that is similar to (requestCode)
//                val row = dbHandler!!.createNewEvent(e)

                //Reset currentCreatingRemainingTimer to 0 value
                currentCreatingRemainingTimer = -1

                //This here reference to another scope
                currentParentId=""

                intervalhour.text="0"
                intervalminute.text="0"

                //Updating event
            } else if (isEdit) {

                //If updating current task haven't run before
//                if(item!!.remainingTime==-1.toLong()){
//
//                    var formatter=SimpleDateFormat("HH:mm")
//
//                    var startTimeArr=formatter.format(item!!.startTime)
//
//                    var endTimeArr=formatter.format(item!!.endTime)
//
//                    var startTime=startTimeArr.trim().split(":")
//
//                    var endTime=endTimeArr.trim().split(":")
//
//                    startHour=Integer.parseInt(startTime[0])
//
//                    startMinute=Integer.parseInt(startTime[1])
//
//                    endHour=Integer.parseInt(endTime[0])
//
//                    endMinute=Integer.parseInt(endTime[1])
//
//                }

                //We must put it in front of initialization of current event --> To update (endTime value), (textview)
                //currentCreatingRemainingTimer=-1
                //If we choose setting interval time mode --> currentCreatingRemainingTimer!=-1
                if (!intervalhour.text.toString().equals("0") || !intervalminute.text.toString().equals("0")) {

                    endHour = startHour + intervalhour.text.toString().toInt()

                    endMinute = startMinute + intervalminute.text.toString().toInt()

                    isNotify = false

                    currentCreatingRemainingTimer = intervalhour.text.toString().toLong() * 60 * 60 * 1000 +
                            intervalminute.text.toString().toLong() * 1000 * 60
                }

                val event = Event(0)
                event.hashId = item!!.hashId
                event.title = title
                event.place = place
                event.description = detail

                event.startTime = if (startMinute > 9) "$startHour:$startMinute" else "$startHour:0$startMinute"
                event.endTime = if (endMinute > 9) "$endHour:$endMinute" else "$endHour:0$endMinute"

                event.date = "$month/$day/$year"
                event.category = item!!.category
                event.notify = if (isNotify) "on" else "off"
                event.repeatMode = if (repeat_control_switch!!.isChecked()) "on" else "off"
                event.repeatType = repeatType
                event.repeatCount = repeatCount

                event.isUrgent = tempisUrgent
                event.isImportant = tempisImportant
                event.level = tempLevel
                event.parentId = currentParentId
                event.levelRecusion = currentlevel
//                event.remainingTime=item!!.remainingTime

                //If we don't enter endTime
                if (startHour > endHour || startHour == endHour && startMinute >= endMinute) {

                    Toast.makeText(applicationContext, "Nhập sai thời gian kết thúc tác vụ xin mời nhập lại!", Toast.LENGTH_SHORT).show()

                    return

                }

                var timerRemainingTimerOfUpdatingTask: Long = 0

                //Checking whether before item have run
                //2 case remainTime=endTime - startTime / remainingTime
                if (itemBeforeUpdating.remainingTime == -1.toLong()) {

                    var formatter = SimpleDateFormat("HH:mm")

                    var startTime = formatter.parse(itemBeforeUpdating.startTime)
                    var endTime = formatter.parse(itemBeforeUpdating.endTime)

                    timerRemainingTimerOfUpdatingTask = endTime.time - startTime.time

                } else {
                    timerRemainingTimerOfUpdatingTask = itemBeforeUpdating.remainingTime
                }

                //We must fix update in two case
                //When task haven't run --> currentCreatingRemainingTimer=-1
                //When task have run --> currentCreatingRemainingTimer>0 ||==0

                //Clicking set (interval time)
                //If this task is a subtask that (has run) for interval time --> (remainingTime!=-1)
                //We will update base on setting (remaining timer mode) --> (currentCreatingRemainingTimer!=-1)
                //For checking remainingTime of previous item(updating item)
                if (!currentParentId.equals("") && currentCreatingRemainingTimer != -1.toLong()) {

                    var totalTimerChilds:Long=0.toLong()

                    if(item!!.hasChildren()){

                        var childs=item!!.children

                        for(ch in childs){

                            var child=ch as Event

                            var formatter = SimpleDateFormat("HH:mm")

                            var startTime = formatter.parse(child.startTime)
                            var endTime = formatter.parse(child.endTime)

                            totalTimerChilds+=endTime.time-startTime.time

                        }

                    }

                    if(currentCreatingRemainingTimer<totalTimerChilds){

                        Toast.makeText(applicationContext, "Thời gian bạn nhập nhỏ hơn tổng thời gian của các tác vụ con, mời nhập lại!", Toast.LENGTH_SHORT).show()

                        return

                    }

                    var eventParent = dbHandler!!.getEventId(currentParentId)

                    eventParent!!.remainingTime -= currentCreatingRemainingTimer - timerRemainingTimerOfUpdatingTask

                    dbHandler!!.updateEvent(eventParent!!)

                    eventDatabase.child("${eventParent.hashId}/remainingTime").setValue(eventParent.remainingTime)

                }

                var parentEvent: Event?

                var difference: Long = 0

                //We choose updating time base on (set StartTime and EndTime)
                //remainingTimeOfParentAndChild --> timer of child that is fixed (end-start)/(remain)
                if (!currentParentId.equals("") && currentCreatingRemainingTimer == -1.toLong()) {

                    //Updating for event having remainTime==-1
                    //It means that this event haven't run
                    event.remainingTime=-1

                    parentEvent = dbHandler!!.getEventId(currentParentId)

                    //Parent task always has remaining time
                    //Because updating when (adding new child)
                    var remainingTimeOfParentAndChild=parentEvent!!.remainingTime+ timerRemainingTimerOfUpdatingTask

                    difference = (endHour.toLong() - startHour.toLong()) * 60 * 60 * 1000 + (endMinute.toLong() - startMinute.toLong()) * 60 * 1000

                    //Subtask even have subtask of subtask
                    //We must to check whether current item has childs
                    var totalTimerChilds:Long=0

                    if(item!!.hasChildren()){

                        var childs=item!!.children

                        for(ch in childs){

                            var child=ch as Event

                            var formatter = SimpleDateFormat("HH:mm")

                            var startTime = formatter.parse(child.startTime)
                            var endTime = formatter.parse(child.endTime)

                            totalTimerChilds+=endTime.time-startTime.time

                        }

                    }

                    if(difference<totalTimerChilds){

                        Toast.makeText(applicationContext, "Thời gian bạn nhập nhỏ hơn tổng thời gian của các tác vụ con, mời nhập lại!", Toast.LENGTH_SHORT).show()

                        return

                    }

                    //If endTime - startTime > remainingTime of (the parent task)
                    if (difference > remainingTimeOfParentAndChild) {

                        Toast.makeText(applicationContext, "Thời gian bạn nhập lớn quá thời gian còn lại của tác vụ cha, mời nhập lại!", Toast.LENGTH_SHORT).show()

                        return

                    }else{

                        //Plus old child and subtract new task
                        parentEvent!!.remainingTime -= currentCreatingRemainingTimer - timerRemainingTimerOfUpdatingTask

                        dbHandler!!.updateEvent(parentEvent!!)

                        eventDatabase.child("${parentEvent.hashId}/remainingTime").setValue(parentEvent.remainingTime)

                    }

                }

                //Check if (level) of this task ==0
                //Check total timer of all childs of this task
                if(currentParentId.equals("")){

                    var totalTimerChilds:Long=0

                    if(item!!.hasChildren()){

                        var childs=item!!.children

                        for(ch in childs){

                            var child=ch as Event

                            var formatter = SimpleDateFormat("HH:mm")

                            var startTime = formatter.parse(child.startTime)
                            var endTime = formatter.parse(child.endTime)

                            totalTimerChilds+=endTime.time-startTime.time

                        }

                    }

                    var currentTimer=(endHour-startHour)*60*60*1000+(endMinute-startMinute)*60*1000

                    if(currentTimer<totalTimerChilds){

                        Toast.makeText(applicationContext, "Thời gian bạn nhập nhỏ hơn tổng thời gian của các tác vụ con, mời nhập lại!", Toast.LENGTH_SHORT).show()

                        return

                    }

                }

                currentCreatingRemainingTimer = -1
                currentParentId=""

                var formatter=SimpleDateFormat("HH:mm")

                //Time without updating
//                var beforeTimeEvent=formatter.parse(item!!.endTime).time-formatter.parse(item!!.startTime).time

                //Time when updating
                var newTimeUpdating:Long=(endHour.toLong()-startHour.toLong())*60*60*1000 + (endMinute.toLong()-startMinute.toLong())*60*1000

                //Updating remaining time of current event to break event into sub event
                event.remainingTime=newTimeUpdating-caculatingAllTimeOfChilds(item!!)

                updatingRealtimedatabase(event,item!!.remainingTime,newTimeUpdating)

                listenerRealtimeDatabase()

                //Updating (general even)
                //All information
//                eventDatabase.child("${event.hashId}").setValue(event)

                intervalhour.text="0"
                intervalminute.text="0"

            }

            //Then hidden slide
            slidingUpPanelLayout!!.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN)
            eventTitle!!.setText("")
            eventPlace!!.setText("")
            eventDetail!!.setText("")

            //show message when event title empty
        } else {
            Utils.showToastMessage("Tiêu đề tác vụ trống, mời nhập lại", applicationContext)
        }

    }

    fun updatingRealtimedatabase(event: Event,beforeTimeEvent:Long, currentTimer:Long){

        Thread(Runnable {

            queryGetIdUser.addListenerForSingleValueEvent(object:ValueEventListener{
                override fun onDataChange(p0: DataSnapshot) {

                    var eventFb=EventFb(event)

                    //Getting key of event
                    eventFb.hashId=event.hashId!!

                    //This is key group
                    var data=p0.children.iterator().next()

                    //Getting (the key of user)
                    eventFb.hashIdUser=data.key!!

                    var message=Message()

                    message.what=0

                    mhandler.sendMessage(message)

                    //Updating hashIdUser for old Event
                    event.hashIdUser=data.key.toString()

                    //-------------------------

                    //Updating of (remaining time) of parent of (current event)
                    if(!event.parentId.equals("")){

                        var parentEventOfCurrentEvent=dbHandler!!.getEventId(event.parentId)

                        parentEventOfCurrentEvent!!.remainingTime+=beforeTimeEvent-currentTimer

                        //Updating parent of (current event) in the SQLite database
                        dbHandler!!.updateEvent(parentEventOfCurrentEvent!!)

                        var eventFbParent=EventFb(parentEventOfCurrentEvent)

                        //Updating parent of (current event) in the realtime database
                        eventDatabase.child("${parentEventOfCurrentEvent.hashId}").setValue(eventFbParent)

                    }

                    //-------------------------

                    eventDatabase.child("${event.hashId}").setValue(eventFb)

                    //Updating event
                    val row = dbHandler!!.updateEvent(event)

                    Utils.showToastMessage("Lưu tác vụ thành công!", applicationContext)

                    categoryFragment!!.refresh(Event(0,event.title!!, "", Event.EVENT_TYPE, false, 0))

                    allEventsFragment!!.refreshEvents()
                    //refresh adapter based on current fragment
//                if (currentFragment == "CategoryFragment") {
//                    categoryFragment!!.refresh(Item(event.title!!, "", Event.EVENT_TYPE, false))
//                } else {
//                    allEventsFragment!!.refreshEvents()
//                }
                    //cancel old alarm
                    receiver!!.cancelAlarm(applicationContext, event.requestCode)

                    //notify alarm
                    if (event.notify.equals("on")) {
                        if (event.repeatMode.equals("on")) {
                            callBroadcastReceiver(event.requestCode, REPEAT_MODE_ON,event.hashId)
                        } else {
                            callBroadcastReceiver(event.requestCode, REPEAT_MODE_OFF,event.hashId)
                        }
                    }

                    Log.i("ID", event.hashId + "  event.getId")
                    Log.i("ID", "$row  row")

                }

                override fun onCancelled(p0: DatabaseError) {

                }

            })
        }).start();

    }

    fun createEventRealtimedatabase(event:Event){
        Thread(Runnable {

            //The generated (key of Events)
            var idRt=databaseEvents.push().key

            event.hashId=idRt!!

            queryGetIdUser.addListenerForSingleValueEvent(object:ValueEventListener{
                override fun onDataChange(p0: DataSnapshot) {

                    var eventFb=EventFb(event)

                    //Getting key of event
                    eventFb.hashId=idRt!!

                    //This is key group
                    var data=p0.children.iterator().next()

                    //Getting (the key of user)
                    eventFb.hashIdUser=data.key!!

                    var message=Message()

                    message.what=0

                    mhandler.sendMessage(message)

                    databaseEvents.child(idRt!!).setValue(eventFb)

                    event.hashIdUser=data.key.toString()

                    val row = dbHandler!!.createNewEvent(event)

                    Log.i("Row", row.toString() + "")

                    Utils.showToastMessage("Lưu tác vụ thành công", applicationContext)

//                    categoryFragment!!.refresh(Event(0,event.title!!, "", Event.EVENT_TYPE, false, 0))

                    allEventsFragment!!.refreshEvents()

                    if (event.notify.equals("on")) {
                        if (event.repeatMode.equals("on")) {

                            callBroadcastReceiver(row, REPEAT_MODE_ON,event.hashId)

                        } else {

                            callBroadcastReceiver(row, REPEAT_MODE_OFF,event.hashId)

                        }

                    }

                }

                override fun onCancelled(p0: DatabaseError) {

                }

            })
        }).start();
    }

    fun listenerRealtimeDatabase(){

        mhandler=object:Handler(Looper.getMainLooper()){
            override fun handleMessage(msg: Message) {
                when(msg.what){
                    0->{
                        Toast.makeText(applicationContext,"Cập nhập xong cơ sở dữ liệu thời gian thực!",Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    }

    fun createEventAddingByAdmin(event:Event,hashIdUserAddingByAdmin:String){

        var idRt=databaseEvents.push().key

        var eventFb=EventFb(event)

        //Getting key of event
        eventFb.hashId=idRt!!

        event.hashId=idRt!!

        //Getting (the key of user)
        eventFb.hashIdUser=hashIdUserAddingByAdmin

        event.hashIdUser=hashIdUserAddingByAdmin

        databaseEvents.child(idRt!!).setValue(eventFb)

        Utils.showToastMessage("Lưu tác vụ thêm bởi admin thành công", applicationContext)

        categoryFragment!!.refresh(Event(0,event.title!!, "", Event.EVENT_TYPE, false, 0))

        allEventsFragment!!.refreshEvents()

    }

    fun caculatingAllTimeOfChilds(event:Event):Long{

        var time:Long=0;

        if(event.hasChildren()){

            for(i in event.children){

                var formatter = SimpleDateFormat("HH:mm")

                var child=i as Event

                var startTime=formatter.parse(child.startTime).time

                var endTime=formatter.parse(child.endTime).time

                time+=endTime-startTime

            }

        }

        return time;

    }

    //After inserting item
    // we need refesh adapter to display all item exists
    override fun onInserted(item: Event) {
//        tvEdit.text = "Edit"
        categoryFragment!!.refresh(item)

    }

    override fun refreshAllEventFragment(){

        allEventsFragment!!.refreshEvents()

    }

    override fun showExistingDialog(title: String, color: String) {
        val builder = AlertDialog.Builder(this, R.style.MyDialogTheme)
        builder.setTitle("Hạng mục tác vụ này đã tồn tại")
                .setMessage("Tạo tác vụ?")
                .setPositiveButton("Tạo") { dialog, which ->
                    dialog.dismiss()
                    setHideOrShow(Event(0,title, color, Category.CATEGORY_TYPE, false, 0), false)
                }
                .setNegativeButton("Hủy") { dialog, which -> dialog.dismiss() }
        val dialog = builder.create()
        dialog.show()
    }


    //this code can be called above
    fun callBroadcastReceiver(row: Int, mode: Int,hashId:String) {
        val startCalendar = Calendar.getInstance()
        startCalendar.set(Calendar.YEAR, year)
        startCalendar.set(Calendar.MONTH, month - 1)
        startCalendar.set(Calendar.DAY_OF_MONTH, day)
        startCalendar.set(Calendar.HOUR_OF_DAY, startHour)
        startCalendar.set(Calendar.MINUTE, startMinute)
        startCalendar.set(Calendar.SECOND, 0)

        val endCalendar = startCalendar.clone() as Calendar
        endCalendar.set(Calendar.YEAR, year)
        endCalendar.set(Calendar.MONTH, month - 1)
        endCalendar.set(Calendar.DAY_OF_MONTH, day)
        endCalendar.set(Calendar.HOUR_OF_DAY, endHour)
        endCalendar.set(Calendar.MINUTE, endMinute)
        endCalendar.set(Calendar.SECOND, 0)

        Log.e("-------------compare", startCalendar.compareTo(endCalendar).toString())

        when (mode) {
            REPEAT_MODE_ON -> receiver!!.setRepeatAlarm(this, startCalendar, endCalendar, row, repeatTime,hashId)
            REPEAT_MODE_OFF -> receiver!!.setAlarm(applicationContext, startCalendar, endCalendar, row,hashId)
        }
    }

}
