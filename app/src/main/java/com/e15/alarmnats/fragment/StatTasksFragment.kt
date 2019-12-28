package com.e15.alarmnats.fragment

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.e15.alarmnats.Database.ReminderDatabase
import com.e15.alarmnats.Model.Category
import com.e15.alarmnats.Model.Event
import com.e15.alarmnats.R
import com.e15.alarmnats.adapter.EventsAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.multilevelview.models.RecyclerViewItem
import lecho.lib.hellocharts.gesture.ZoomType
import lecho.lib.hellocharts.listener.ColumnChartOnValueSelectListener
import lecho.lib.hellocharts.model.Axis
import lecho.lib.hellocharts.model.Column
import lecho.lib.hellocharts.model.ColumnChartData
import lecho.lib.hellocharts.model.SubcolumnValue
import lecho.lib.hellocharts.util.ChartUtils
import lecho.lib.hellocharts.view.Chart
import lecho.lib.hellocharts.view.ColumnChartView
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class StatTasksFragment : Fragment(), View.OnClickListener {
    private val TOTAL_DATA = 0

    private val RESPECTIVE_DATA = 2
    private var chart: ColumnChartView? = null

    private var data: ColumnChartData? = null
    private var hasAxes = true
    private var hasAxesNames = true
    private var hasLabels = false
    private var hasLabelForSelected = false
    private var dataType = TOTAL_DATA
    lateinit var dbHandler: ReminderDatabase

    lateinit var previousWeek: ImageView

    lateinit var nextWeek: ImageView

    lateinit var currentDay: Date

    lateinit var month: TextView

    lateinit var day: TextView

    lateinit var year: TextView

    var isworkingTimer = true

    lateinit var rdWorkingTime: RadioButton

    lateinit var rdRemainingTime: RadioButton

    lateinit var listEventsToTalTimer: ArrayList<Long>

    lateinit var listEndSubStart:ArrayList<Long>

    var isClick:Boolean = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)

        dbHandler = ReminderDatabase(activity!!.applicationContext)

        val rootView = inflater.inflate(R.layout.fragment_stat_task, container, false)

        previousWeek = rootView.findViewById(R.id.previousWeek)

        nextWeek = rootView.findViewById(R.id.nextWeek)

        previousWeek.setOnClickListener(this)

        nextWeek.setOnClickListener(this)

        month = rootView.findViewById(R.id.month)

        day = rootView.findViewById(R.id.day)

        year = rootView.findViewById(R.id.year)

        rdWorkingTime = rootView.findViewById(R.id.rdWorkingTime)

        rdRemainingTime = rootView.findViewById(R.id.rdRemainingTime)

        rdWorkingTime.setOnClickListener(this)

        rdRemainingTime.setOnClickListener(this)

        listEventsToTalTimer = ArrayList()

        listEndSubStart= ArrayList()

        //populate date information to UI
        var calendar = Calendar.getInstance()

        calendar.firstDayOfWeek = Calendar.MONDAY

        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

        currentDay = calendar.time

        month.text = (calendar.get(Calendar.MONTH) + 1).toString()

        day.text = calendar.get(Calendar.DAY_OF_MONTH).toString()

        year.text = calendar.get(Calendar.YEAR).toString()

        chart = rootView.findViewById(R.id.chart)
        chart!!.onValueTouchListener = ValueTouchListener()

        //Set label for each columns
        //We can unable by calling false
        //And setting up mode when clicking then display label by setting hasLabelforSeleect=true
        chart!!.isValueSelectionEnabled = false

        //set for having label when displaying
        hasLabels = true

        generateData()

        return rootView
    }

    // MENU
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        inflater.inflate(R.menu.column_chart, menu)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.total_task) {

            generateData()

            return true
        }
        if (id == R.id.respective_task) {
            dataType = RESPECTIVE_DATA

            generateData()

            return true

        }
        return super.onOptionsItemSelected(item)
    }

    private fun generateData() {
        when (dataType) {
            TOTAL_DATA -> generateTotalOrRepectiveData()
            RESPECTIVE_DATA -> generateStackedData()
            else -> generateTotalOrRepectiveData()
        }
    }


    override fun onClick(v: View?) {

        when (v!!.id) {

            R.id.previousWeek -> {

                var calendar = Calendar.getInstance()

                calendar.time = currentDay

                calendar.firstDayOfWeek = Calendar.MONDAY

                //Checking
                calendar.get(Calendar.DAY_OF_MONTH).toString()

                calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH)-7)

                //Checking
                calendar.get(Calendar.DAY_OF_MONTH).toString()

                month.text = (calendar.get(Calendar.MONTH) + 1).toString()

                day.text = calendar.get(Calendar.DAY_OF_MONTH).toString()

                year.text = calendar.get(Calendar.YEAR).toString()

                currentDay = calendar.time

                generateData()

            }

            R.id.nextWeek -> {

                var calendar = Calendar.getInstance()

                calendar.time = currentDay

                //Checking
                var x=calendar.get(Calendar.DAY_OF_MONTH).toString()

                calendar.firstDayOfWeek = Calendar.MONDAY

                //DAY_OF_WEEK is days in week (monday, tuesday,...)
                calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH)+7)

//                calendar.add(Calendar.DAY_OF_MONTH, 7)

                //Checking
                x=calendar.get(Calendar.DAY_OF_MONTH).toString()

                month.text = (calendar.get(Calendar.MONTH) + 1).toString()

                day.text = calendar.get(Calendar.DAY_OF_MONTH).toString()

                year.text = calendar.get(Calendar.YEAR).toString()

                currentDay = calendar.time

                generateData()

            }

            R.id.rdWorkingTime -> {

                if(!isClick){
                    Thread.sleep(500)
                }

                if(!isClick){

                    isClick=true

                    prepareDataAnimation()

                    chart!!.startDataAnimation()
                }

            }

            R.id.rdRemainingTime -> {

                if(isClick){
                    Thread.sleep(500)
                }

                if(isClick){

                    isClick=false

                    prepareDataAnimation()

                    chart!!.startDataAnimation()
                }

            }

        }

    }


    private fun generateTotalOrRepectiveData() {

        listEventsToTalTimer.clear()

        listEndSubStart.clear()

        var format = SimpleDateFormat("MM/dd/yyyy")

        var listTask = fillAllItems(false).filter { event -> event.levelRecusion==0 }

        var calendar = Calendar.getInstance()

        calendar.time = currentDay

        //Sets what (the first day) of the week is; e.g., SUNDAY in the U.S.,
        // MONDAY in France.
        calendar.firstDayOfWeek = Calendar.MONDAY

        //DAY_OF_WEEK
        //Field number for get and set indicating the day of the week.
        // This field takes values SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, and SATURDAY.
//        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

        var days = arrayOfNulls<String>(7)

        for (i in 0..6) {

            days[i] = format.format(calendar.time)

            calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH)+1)

        }

        val numSubcolumns = 1
        val numColumns = 8
        // Column can have many subcolumns, here by default I use 1 subcolumn in each of 8 columns.
        val columns = ArrayList<Column>()
        var values: MutableList<SubcolumnValue>
        for (i in 0 until numColumns) {

            values = ArrayList()
            for (j in 0 until numSubcolumns) {

                var totalTimer: Float = 0.toFloat()

                var offsetTimer = 0.toLong()

                var event = Event(0)

                if (i != 7) {
                    //Calculating all time of childs of current process
                    listTask.forEach {

                        if (format.format(format.parse(it.date)).equals(days[i])) {

                            var timerFormat = SimpleDateFormat("HH:mm")

                            var startTime = timerFormat.parse(it.startTime)

                            var endTime = timerFormat.parse(it.endTime)

                            var difference: Long

                            difference = endTime.time - startTime.time

                            offsetTimer += caculateAllRemainingTimeOfChilds(it,listTask as ArrayList<Event>)/60/1000

                            totalTimer += difference / 60 / 1000

                        }

                    }

                }

                listEventsToTalTimer.add(offsetTimer)

                listEndSubStart.add(totalTimer.toLong())

                var subColumn = SubcolumnValue(totalTimer, ChartUtils.pickColor())

                //Set label for each day
                if (i < 7) subColumn.setLabel(days[i]!!.substring(0, days[i]!!.length - 5))
                else {
                    subColumn.setLabel("")
                }

                values.add(subColumn)
            }

            val column = Column(values)
            column.setHasLabels(hasLabels)
            column.setHasLabelsOnlyForSelected(hasLabelForSelected)

            //Add column corresponding days of (a current week)
            columns.add(column)
        }

        data = ColumnChartData(columns)

        if (hasAxes) {
            val axisX = Axis()
            val axisY = Axis().setHasLines(true)
            if (hasAxesNames) {
                axisX.name = "Ngày"
                axisY.name = "Số phút đã hoàn thành"
            }
            data!!.axisXBottom = axisX
            data!!.axisYLeft = axisY
        } else {
            data!!.axisXBottom = null
            data!!.axisYLeft = null
        }

        data!!.isStacked = false

        chart!!.columnChartData = data

        chart!!.zoomType = ZoomType.HORIZONTAL_AND_VERTICAL

    }

    /**
     * Generates columns with stacked subcolumns.
     */
    private fun generateStackedData() {

        listEventsToTalTimer.clear()

        listEndSubStart.clear()

        var format = SimpleDateFormat("MM/dd/yyyy")

        var listTask = fillAllItems(false).filter { event -> event.levelRecusion==0 }

        var calendar = Calendar.getInstance()

        calendar.time = currentDay

        //Sets what (the first day) of the week is; e.g., SUNDAY in the U.S.,
        // MONDAY in France.
        calendar.firstDayOfWeek = Calendar.MONDAY

        //DAY_OF_WEEK
        //Field number for get and set indicating the day of the week.
        // This field takes values SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, and SATURDAY.
//        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

        var days = arrayOfNulls<String>(7)

        for (i in 0..6) {

            days[i] = format.format(calendar.time)

            calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH)+1)

        }

        val numColumns = 8
        // Column can have many stacked subcolumns, here I use 4 stacke subcolumn in each of 4 columns.
        val columns = ArrayList<Column>()
        var values: MutableList<SubcolumnValue>
        for (i in 0 until numColumns) {

            listEventsToTalTimer.add(0)
            listEndSubStart.add(0)
            //This is values of (a single column)
            values = ArrayList()
            for (task in listTask) {

                if (i < 7 && format.format(format.parse(task.date)).equals(days[i])) {

                    var offsetTimer = 0.toLong()

                    var timer = 0.toLong()

                    var timerFormat = SimpleDateFormat("HH:mm")

                    var startTime = timerFormat.parse(task.startTime)

                    var endTime = timerFormat.parse(task.endTime)

                    var difference: Long

                    difference = endTime.time - startTime.time - task.remainingTime

                    offsetTimer = caculateAllRemainingTimeOfChilds(task,listTask as ArrayList<Event>)/60/1000

                    timer += difference / 60 / 1000

                    listEventsToTalTimer.add(offsetTimer)

                    listEndSubStart.add(difference/60/1000)

                    var subColumn = SubcolumnValue(timer.toFloat(), ChartUtils.pickColor())

                    subColumn.setLabel(days[i]!!.substring(0, days[i]!!.length - 5))

                    values.add(subColumn)

                }

            }

            //Create corresponding column containing (all above values)
            val column = Column(values)
            column.setHasLabels(hasLabels)
            column.setHasLabelsOnlyForSelected(hasLabelForSelected)
            columns.add(column)
        }

        data = ColumnChartData(columns)

        // Set stacked flag.
        data!!.isStacked = true

        if (hasAxes) {
            val axisX = Axis()
            val axisY = Axis().setHasLines(true)
            if (hasAxesNames) {
                axisX.name = "Số ngày"
                axisY.name = "Số phút đã hoàn thành"
            }
            data!!.axisXBottom = axisX
            data!!.axisYLeft = axisY
        } else {
            data!!.axisXBottom = null
            data!!.axisYLeft = null
        }

        chart!!.columnChartData = data

        chart!!.zoomType = ZoomType.HORIZONTAL_AND_VERTICAL

    }

    private fun toggleLabels() {
        hasLabels = !hasLabels

        if (hasLabels) {
            hasLabelForSelected = false
            chart!!.isValueSelectionEnabled = hasLabelForSelected
        }

        generateData()
    }


    /**
     * To animate values you have to change targets values and then call [Chart.startDataAnimation]
     * method(don't confuse with View.animate()).
     */
    private fun prepareDataAnimation() {

        //a number of the element
        var count = 0

        //Loop for (all columns) of data then change value of (each column)
        for (indexColumn in data!!.columns.indices) {

            var currentColumn = data!!.columns.get(indexColumn)

            count++

            //Current column may be has several subcolumns and
            // we need to change (value of each subcolumn)
            for (index in currentColumn.values.indices) {

                //Count to position of each (subcolumn)
                //We we save all value event base on (linear order)
                count++

                var currentSubColumn = currentColumn.values.get(index)

                //Getting value of (current subcolumn)
                var currentValue = currentSubColumn.value

                var targetValue = 0.toFloat()

                if (dataType == RESPECTIVE_DATA) {

                    //If we choose stack data type
                    if(!isClick) targetValue = listEventsToTalTimer.get(count-1).toFloat()
                    else{

                        //If We re-click restart total time to initialization
                        targetValue = listEndSubStart.get(count-1).toFloat()

                    }

                } else {

                    //If we choose total
                    if(!isClick) targetValue = listEventsToTalTimer.get(indexColumn).toFloat()
                    else{

                        //If We re-click restart total time to initialization
                        targetValue = listEndSubStart.get(indexColumn).toFloat()

                    }

                }

                currentSubColumn.setTarget(targetValue)
            }
        }
    }

    //return all item (in the database)
    fun fillAllItems(isEdit: Boolean): ArrayList<Event> {

        var allItems=ArrayList<Event>()

        //Firstly, creating new empty ArrayList<Item>
        val allItemsEm = ArrayList<Event>()

        //Then we hold (all categorys) from (the database)
        var categories = dbHandler!!.allCategory

        //Looping for (All categories)
        for (i in categories!!.indices) {

            val category = categories!![i]

            //Build a (CategoryItem) base (Category) (at index)
            //Note that: This item has (CATEGORY_TYPE)
            val categoryItem = Event(0, category.title, Category.CATEGORY_TYPE, -2, category.hasIdCategory, isEdit, category.color)

            categoryItem.hashIdUser=category.hashIdUser

            //Add item has CATEGORY_TYPE
            allItemsEm.add(categoryItem)

            //Getting (all items) from database (corresponding category)
            // (has this cagory) in (the current loop)
            var events = dbHandler!!.getEventsByCategory(category.title)

            var tempEvents = ArrayList<Event>()

            for (j in events.indices) {

                val e = events.get(j)

                Log.e("--------doneOrNot", e.isDone.toString())

                val eventItem = Event(e.hashId, 0, e.title!!, e.description!!, e.place!!,
                        e.category!!, e.startTime!!, e.endTime, e.date!!,
                        e.isShow!!, Event.EVENT_TYPE, e.notify!!, e.repeatMode!!,
                        e.repeatCount!!, e.repeatType!!, 0, categoryItem.color!!)

                eventItem.isDone = e.isDone
                eventItem.remainingTime = e.remainingTime
                eventItem.levelRecusion = e.levelRecusion
                eventItem.parentId = e.parentId
                eventItem.addChildren(e.children)
                allItemsEm.add(eventItem)
            }

            //Add child but displaying base on (levelRecusion) of (the corresponding node)
            recusiveListEvent(allItemsEm as ArrayList<Event>, 0)

            //All node have recustion==0 --> is added to allItems
            //Category has (levelRecursion==-2)
            for (e in allItemsEm) {

                if (e.parentId.equals("") || e.levelRecusion == -2) {

                    tempEvents.add(e)

                }

            }

            allItems.clear()

            allItems.addAll(tempEvents)

        }


        return allItems
    }

    fun recusiveListEvent(listEvents: ArrayList<Event>, level: Int) {

        for (e in listEvents.indices) {

            if (listEvents.get(e).levelRecusion == level) {

                var listChilds: ArrayList<Event> = ArrayList<Event>()

                for (e1 in listEvents) {

                    //Reach (one node) then reachs (all orther nodes)
                    //If (any node) has (parenrId== node.id) --> Add child
                    if (listEvents.get(e).hashId.equals(e1.parentId) && e1.levelRecusion == level + 1) {

                        recusiveListEvent(listEvents, level + 1)

                        listChilds.add(e1)

                    }

                }

                if (listChilds.size != 0) listEvents.get(e).addChildren(listChilds as List<RecyclerViewItem>?)

            }

        }

    }

    //This function is used to caculate all remaining time childs
    fun caculateAllRemainingTimeOfChilds(currentTime: Event, allItems: ArrayList<Event>): Long {

        var finalTotal: Long = 0

        for (i in allItems) {

            //Getting (current item) from (allitems)
            if (i.hashId.equals(currentTime.hashId)) {

                var totalchilds = 0.toLong()

                var workingTimeOfCurrenTask: Long = 0

                if (i.hasChildren()) {

                    for (child in i.children) {

                        totalchilds += caculateAllRemainingTimeOfChilds(child as Event, i.children as ArrayList<Event>)

                        child as Event

                        //It is used to hold (all interval time of childs)
                        var totalIntervalStartEndTime = 0.toLong()

                        //This is used to updating workingTime of (a current task) each times
                        for (subOfSubItem in i.children) {

                            subOfSubItem as Event

                            var formatter = SimpleDateFormat("HH:mm")

                            var startTime = formatter.parse(subOfSubItem.startTime)

                            var endTime = formatter.parse(subOfSubItem.endTime)

                            totalIntervalStartEndTime += endTime.time - startTime.time

                        }

                        //Hold all (inteval time) of (the current child task)
                        var formatter = SimpleDateFormat("HH:mm")

                        var startTime = formatter.parse(i.startTime)

                        var endTime = formatter.parse(i.endTime)

                        var difference = endTime.time - startTime.time

                        //this parameter is used to save (total of all childs interval time)
//                        totalIntervalTimeOfChildTasks+=difference

                        //If (remainintTime ==-1) it means that this (current child) task (has run)
                        if (i.remainingTime != -1.toLong() && i.remainingTime != difference - totalIntervalStartEndTime) {

                            //We holds (all time) of (current task)
                            // - (total (all times) of child tasks)
                            // - (remainingTime)
                            totalchilds += difference - totalIntervalStartEndTime - i.remainingTime

                        }

                    }

                }

                //Updating ignores Category
                //For child task hasn't childs
                // (child has run but not having subchild task)
                if (!i.hasChildren() && i.type != 0) {

                    //These lines are used to caculate working time of current task
                    var formatter = SimpleDateFormat("HH:mm")

                    var startTime = formatter.parse(i.startTime)

                    var endTime = formatter.parse(i.endTime)

                    var difference = endTime.time - startTime.time

                    if (i.remainingTime != -1.toLong() && i.remainingTime != difference) {

                        workingTimeOfCurrenTask = difference - i.remainingTime

                    }

                }

                finalTotal += totalchilds + workingTimeOfCurrenTask

            }
        }

        return finalTotal

    }

    private inner class ValueTouchListener : ColumnChartOnValueSelectListener {

        override fun onValueSelected(columnIndex: Int, subcolumnIndex: Int, value: SubcolumnValue) {
            Toast.makeText(activity, "$value", Toast.LENGTH_SHORT).show()
        }

        override fun onValueDeselected() {
            // TODO Auto-generated method stub

        }

    }

}