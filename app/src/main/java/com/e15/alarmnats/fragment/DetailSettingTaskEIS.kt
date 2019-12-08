package com.e15.alarmnats.fragment

import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.e15.alarmnats.Database.ReminderDatabase
import com.e15.alarmnats.Model.Category
import com.e15.alarmnats.Model.Event
import com.e15.alarmnats.Model.EventFb
import com.e15.alarmnats.R
import com.e15.alarmnats.utils.Utils
import com.e15.alarmnats.view.ColorCircle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import java.util.*

class DetailSettingTaskEIS: Fragment(), DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener {
    companion object {

        var contexts: Context? =null

    }

    var isNotify = true
    var repeatCount: String? = "5"
    var repeatType: String? = "Nhỏ nhất"

    val milMinute = 60000L
    val milHour = 3600000L
//    var item: Item? = null

    var categoryColorIcon: ColorCircle? = null
    var tvAddOrEdit: TextView? = null
    var tvHour: TextView? = null
    var tvMinute: TextView? = null
    var tvDay: TextView? = null
    var tvMonth: TextView? = null
    var tvYear: TextView? = null
    var eventTitle: EditText? = null
    var eventPlace: EditText? = null
    var eventDetail: EditText? = null
    var startHour: Int = 0
    var startMinute: Int = 0
    var endHour:Int=-1;
    var endMinute:Int=-1;
    var day: Int = 0
    var month: Int = 0
    var year: Int = 0
//    var dbHandler: ReminderDatabase? = null
//    var categoryFragment: CategoryFragment? = null
//    var allEventsFragment: AllEventsFragment? = null
//    var receiver: AlarmReceiver? = null

    lateinit var category:String;

    lateinit var rbnStartTime: RadioButton;
    lateinit var rbnEndTime: RadioButton;

    lateinit var tvCategoryTitle: TextView

    lateinit var btnSaveEvent: Button

    lateinit var dateLayout: LinearLayout

    lateinit var setTime: RelativeLayout

    lateinit var listStatesUrgent:Array<String>
    lateinit var listStatesImportant:Array<String>

    lateinit var cvisUrgent: CardView;

    lateinit var cvisImportant: CardView;

    lateinit var tvisUrgent: TextView

    lateinit var tvisImportant: TextView

    var resultState= intArrayOf(0,0)

    var finalResult=0;

    lateinit var mdbHandler:ReminderDatabase

    lateinit var firebaseDatabase:FirebaseDatabase

    lateinit var databaseEvents:DatabaseReference

    lateinit var mhandler:Handler

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        var view= inflater.inflate(R.layout.fragment_detail_adding_task, container, false)

        var now=Calendar.getInstance()

        startHour = now.get(Calendar.HOUR_OF_DAY)
        startMinute = now.get(Calendar.MINUTE)
//
////            endHour = now.get(Calendar.HOUR_OF_DAY)
////            endMinute = now.get(Calendar.MINUTE)

        day = now.get(Calendar.DATE)
        month = now.get(Calendar.MONTH)+1
        year = now.get(Calendar.YEAR)

        var sharedPreferences: SharedPreferences =contexts!!.getSharedPreferences("choiceCategory", Context.MODE_PRIVATE)

        category=sharedPreferences.getString("choice","")

        //-----------Setting choices view

        tvisUrgent=view.findViewById(R.id.isUrgent)

        tvisImportant=view.findViewById(R.id.isImportant)

        listStatesUrgent=resources.getStringArray(R.array.isUrgent)
        listStatesImportant=resources.getStringArray(R.array.isImportant)

        var checkUrgentState= booleanArrayOf(false, false)

        cvisUrgent=view.findViewById(R.id.cvisUrgent)

        cvisUrgent.setOnClickListener(object: View.OnClickListener{

            override fun onClick(v: View?) {

                var builder: AlertDialog.Builder = AlertDialog.Builder(activity!!,R.style.MyDialogTheme)

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

                var isUrgent:String="Khẩn cấp"

                builder.setSingleChoiceItems(listStatesUrgent,-1, DialogInterface.OnClickListener { dialog, which ->

                    when(which){

                        0->{

                            isUrgent="Khẩn cấp"

                            for(i in 0..resultState.size-1){

                                resultState[i]=0

                            }

                        }
                        1->{

                            isUrgent="Không khẩn cấp"

                            for(i in 0..resultState.size-1){

                                resultState[i]=1

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

                var mdialog=builder.create()

                mdialog.show()

            }

        })

        cvisImportant=view.findViewById(R.id.cvisImportant)

        cvisImportant.setOnClickListener(object: View.OnClickListener{

            override fun onClick(v: View?) {

                var builder: AlertDialog.Builder = AlertDialog.Builder(activity!!,R.style.MyDialogTheme)

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

                var isImportant:String="Quan trọng"

                builder.setSingleChoiceItems(listStatesImportant,-1, DialogInterface.OnClickListener { dialog, which ->

                    when(which){

                        0->{

                            isImportant="Quan trọng"

                            finalResult=0

                        }
                        1->{

                            isImportant="Không quan trọng"

                            finalResult=1

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

                var mdialog=builder.create()

                mdialog.show()

            }

        })

        //-------------------------

        tvCategoryTitle = view.findViewById<View>(R.id.tvCategoryTitle) as TextView
        eventTitle = view.findViewById<View>(R.id.eventTitle) as EditText
        eventPlace = view.findViewById<View>(R.id.eventPlace) as EditText
        eventDetail = view.findViewById<View>(R.id.eventDetail) as EditText
        tvHour = view.findViewById<View>(R.id.hour) as TextView
        tvMinute = view.findViewById<View>(R.id.minute) as TextView
        tvDay = view.findViewById<View>(R.id.day) as TextView
        tvMonth = view.findViewById<View>(R.id.month) as TextView
        tvYear = view.findViewById<View>(R.id.year) as TextView

        tvHour!!.setText(startHour.toString())
        tvMinute!!.setText(startMinute.toString())
        tvDay!!.setText(day.toString())
        tvMinute!!.setText(month.toString())
        tvYear!!.setText(year.toString())

        rbnStartTime=view.findViewById(R.id.startTime)
        rbnEndTime=view.findViewById(R.id.endTime)

        rbnEndTime.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {

                Toast.makeText(contexts,"EndTime", Toast.LENGTH_SHORT).show()

//                val endTime = item!!.endTime!!.trim().split(":")
//
//                endHour=Integer.parseInt(endTime[0])
//                endMinute=Integer.parseInt(endTime[1])
//
//                tvHour!!.setText((if (endHour < 12) endHour else endHour - 12).toString())
//                tvMinute!!.setText((if (endMinute > 9) endMinute else "0$endMinute").toString())

            }
        })

        rbnStartTime.setOnClickListener(object : View.OnClickListener{

            override fun onClick(v: View?) {

                Toast.makeText(contexts,"StartTime", Toast.LENGTH_SHORT).show()

////                if(item!!.startTime==null) return;
//
//                val startTime=item!!.startTime!!.trim().split(":");
//
//                startHour=Integer.parseInt(startTime[0])
//                startMinute=Integer.parseInt(startTime[1])
//
//                tvHour!!.setText((if(startHour<12) startHour else startHour-12).toString())
//
//                tvMinute!!.setText((if(startMinute>9) startMinute else "$startMinute").toString())

            }
        })

        //-------------------------

        tvCategoryTitle=view.findViewById(R.id.tvCategoryTitle)

        tvCategoryTitle.setText(category)

        btnSaveEvent=view.findViewById(R.id.btnSaveEvent)

        btnSaveEvent.setOnClickListener(object: View.OnClickListener{

            override fun onClick(v: View?) {

                saveEvent()

            }
        })

        dateLayout=view.findViewById(R.id.dateLayout);

        dateLayout.setOnClickListener(object: View.OnClickListener{

            override fun onClick(v: View?) {
                setDate()
            }

        })

        setTime=view.findViewById(R.id.setTimeLayout)

        setTime.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {

                setTime();

            }
        })

        mdbHandler=ReminderDatabase(activity!!.applicationContext)

        firebaseDatabase= FirebaseDatabase.getInstance()

        databaseEvents=firebaseDatabase.getReference("Events")

        return view

    }

    fun setDate() {
        val datePickerDialog = DatePickerDialog.newInstance(
                this,
                year,
                month,
                day
        )

        datePickerDialog.setThemeDark(true)
        datePickerDialog.show(activity!!.fragmentManager, "DatePickerDialog")
    }

    //click time picker
    fun setTime() {
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
        timePickerDialog.show(activity!!.fragmentManager, "TimePickerDialog")
    }

    //This function is overrided from the (DatePickerDialog)
    //It is used to set all attributes involved (Date)
    override fun onDateSet(view: DatePickerDialog, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        month = monthOfYear+1
        day = dayOfMonth
        this.year = year
        tvDay!!.setText(day.toString())

        Log.e("-------------",month.toString())

        tvMonth!!.setText(Utils.months[month-1])
        tvYear!!.setText(year.toString())
    }


    //this function overrided from (TimePickerDialog)
    //This function is used to set all attributes involved (Time)
    override fun onTimeSet(view: RadialPickerLayout, hourOfDay: Int, minute: Int, second: Int) {

        if(rbnStartTime.isChecked){

            startHour = hourOfDay

            this.startMinute = minute

            tvHour!!.setText((if (startHour < 12) startHour else startHour - 12).toString())

            tvMinute!!.setText((if (startMinute > 9) startMinute else "0$startMinute").toString())

        }else{
            endHour = hourOfDay

            this.endMinute = minute

            tvHour!!.setText((if (endHour < 12) endHour else endHour - 12).toString())

            tvMinute!!.setText((if (endMinute > 9) endMinute else "0$endMinute").toString())
        }

    }

    fun saveEvent(){

        var title = eventTitle!!.getText().toString().trim { it <= ' ' }
        val place = eventPlace!!.getText().toString().trim { it <= ' ' }
        val detail = eventDetail!!.getText().toString().trim { it <= ' ' }

        if (title != "" && title.length != 0) {
            title = title.substring(0, 1).toUpperCase() + title.substring(1)

            //Saving new event

            if(endMinute==-1){

                Toast.makeText(context,"Please choose end time", Toast.LENGTH_SHORT).show()

                return

            }

            val e = Event(title, detail, place, category,
                    if (startMinute > 9) "$startHour:$startMinute" else "$startHour:0$startMinute",
                    if (endMinute > 9) "$endHour:$endMinute" else "$endHour:0$endMinute",
                    "$month/$day/$year", Event.EVENT_TYPE, "off", "off",
                    this!!.repeatType, this!!.repeatCount,0)

            e.remainingTime=-1

//            if(TaskBaseOnCustomCategoryFragment.listEvent==null){
//                TaskBaseOnCustomCategoryFragment.listEvent=ArrayList()
//            }

            Toast.makeText(activity!!.applicationContext,finalResult.toString()+""+resultState[finalResult], Toast.LENGTH_SHORT).show()

            var actions=resources.getStringArray(R.array.action_type_events)

            if(finalResult==0&&resultState[finalResult]==0){

                e.isUrgent=true
                e.isImportant=true

                e.level=actions[0]

                Log.e("---------Actions",e.level)

//                TaskBaseOnCustomCategoryFragment.listEvent.add(e)

            }
            if(finalResult==0&&resultState[finalResult]==1){

                e.isUrgent=false

                e.isImportant=true

                e.level=actions[1]

//                TaskBaseOnCustomCategoryFragment.listEvent1.add(e)

            }
            if(finalResult==1&&resultState[finalResult]==0){

                e.isUrgent=true
                e.isImportant=false

                e.level=actions[2]

//                TaskBaseOnCustomCategoryFragment.listEvent2.add(e)

            }
            if(finalResult==1&&resultState[finalResult]==1){

                e.isUrgent=false
                e.isImportant=false

                e.level=actions[3]

//                TaskBaseOnCustomCategoryFragment.listEvent3.add(e)

            }

            AlertDialog.Builder(activity!!, R.style.MyDialogTheme).setMessage("Bạn có muốn lưu tác vụ vào cơ sở dữ liệu?"
            ).setPositiveButton("Có", DialogInterface.OnClickListener { dialog, which ->

                updatingRealtimedatabase(e)

                listenerRealtimeDatabase()

                mdbHandler.createNewEvent(e)

                Toast.makeText(activity!!.applicationContext,"Thêm tác vụ thành công!",Toast.LENGTH_SHORT).show()

//                activity!!.recreate()

                var flag:Boolean=false
                for(cat in mdbHandler.allCategory){

                    if(category.toLowerCase().equals(cat.title.toLowerCase())) flag=true

                }

                //Using (firebase database)
                var categoryDatabase=firebaseDatabase.getReference("Category")

                var hashIdCategory=categoryDatabase.push().key

                var category=Category(hashIdCategory!!,category,resources.getStringArray(R.array.colors).get(Random().nextInt(7)),0)

                categoryDatabase.child(hashIdCategory!!).setValue(category)

                Toast.makeText(activity!!.applicationContext,"Cập nhập thành công cơ sỏ dữ liệu thời gian thực", Toast.LENGTH_SHORT).show()

                //Adding hashIdCategory
                //Creating category from EisenHower if not having category (Creating category system)
                if(!flag) mdbHandler.createNewCategory(category)

            }).setNegativeButton("Không", null).show()

            var isUrgent:String=tvisUrgent.text.toString()
            var isImportant:String=tvisImportant.text.toString()

            if(startHour>endHour||startHour==endHour&&startMinute>=endMinute){

                Toast.makeText(contexts,"Entering end time incorrectly, please re-enter!", Toast.LENGTH_SHORT).show()

                return

            }

//            val row = dbHandler!!.createNewEvent(e)

//            Log.i("Row", row.toString() + "")

            Toast.makeText(contexts, "Event saved", Toast.LENGTH_SHORT).show()

//            categoryFragment!!.refresh(Item(e.title!!, "", Event.EVENT_TYPE, false))

            //Then hidden slide

            eventTitle!!.setText("")
            eventPlace!!.setText("")
            eventDetail!!.setText("")

            //show message when event title empty
        }else{

            Toast.makeText(contexts,"Please enter the title", Toast.LENGTH_SHORT).show()

        }

    }

    fun updatingRealtimedatabase(event: Event){

        Thread(Runnable {

            //Nested listener event into returned Thread
            var currentUser= FirebaseAuth.getInstance().currentUser

            var databaseUser=FirebaseDatabase.getInstance().getReference("User").orderByChild("email").equalTo(currentUser!!.email)

            databaseEvents=FirebaseDatabase.getInstance().getReference("Events")

            //The generated (key of Events)
            var idRt=databaseEvents.push().key

            event.hashId=idRt!!

            databaseUser.addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {

                    var eventFb= EventFb(event)

                    //Getting key of event
                    eventFb.hashId=idRt!!

                    var data=p0.children.iterator().next()

                    //Getting (the key of user)
                    eventFb.hashIdUser=data.key!!

                    var message= Message()

                    message.what=0

                    mhandler.sendMessage(message)

                    databaseEvents.child(idRt!!).setValue(eventFb)

                }

                override fun onCancelled(p0: DatabaseError) {

                }

            })
        }).start();

    }

    fun listenerRealtimeDatabase(){

        mhandler=object: Handler(Looper.getMainLooper()){
            override fun handleMessage(msg: Message) {
                when(msg.what){
                    0->{
                        Toast.makeText(activity!!.applicationContext,"Cập nhập xong cơ sở dữ liệu thời gian thực!",Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    }

}
