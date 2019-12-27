package com.e15.alarmnats.fragment

import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.e15.alarmnats.Database.ReminderDatabase
import com.e15.alarmnats.Model.Event
import com.e15.alarmnats.R
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

    var isClick:Boolean = false

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

                if(isClick){
                    Thread.sleep(500)
                }

                if(isClick){

                    isClick=false

                    prepareDataAnimation()

                    chart!!.startDataAnimation()
                }

            }

            R.id.rdRemainingTime -> {

                if(!isClick){
                    Thread.sleep(500)
                }

                if(!isClick){

                    isClick=true

                    prepareDataAnimation()

                    chart!!.startDataAnimation()
                }

            }

        }

    }


    private fun generateTotalOrRepectiveData() {

        listEventsToTalTimer.clear()

        var format = SimpleDateFormat("MM/dd/yyyy")

        var listTask = dbHandler.allEvents.filter { event -> event.levelRecusion==0 }

        var calendar = Calendar.getInstance()

        calendar.time = currentDay

        //Sets what (the first day) of the week is; e.g., SUNDAY in the U.S.,
        // MONDAY in France.
        calendar.firstDayOfWeek = Calendar.MONDAY

        //DAY_OF_WEEK
        //Field number for get and set indicating the day of the week.
        // This field takes values SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, and SATURDAY.
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

        var days = arrayOfNulls<String>(7)

        for (i in 0..6) {

            days[i] = format.format(calendar.time)

            calendar.add(Calendar.DAY_OF_MONTH, 1)

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
                    listTask.forEach {

                        if (it.date.equals(days[i])) {

                            var timerFormat = SimpleDateFormat("HH:mm")

                            var startTime = timerFormat.parse(it.startTime)

                            var endTime = timerFormat.parse(it.endTime)

                            var difference: Long

                            difference = endTime.time - startTime.time - it.remainingTime

                            offsetTimer += (endTime.time - startTime.time) / 1000 / 60

                            totalTimer += difference / 60 / 1000

                        }

                    }

                }

                listEventsToTalTimer.add(offsetTimer)

                var subColumn = SubcolumnValue(totalTimer, ChartUtils.pickColor())

                if (i < 7) subColumn.setLabel(days[i]!!.substring(0, days[i]!!.length - 5))
                else {
                    subColumn.setLabel("")
                }

                values.add(subColumn)
            }

            val column = Column(values)
            column.setHasLabels(hasLabels)
            column.setHasLabelsOnlyForSelected(hasLabelForSelected)
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

        var format = SimpleDateFormat("MM/dd/yyyy")

        var listTask = dbHandler.allEvents.filter { event -> event.levelRecusion==0 }

        var calendar = Calendar.getInstance()

        calendar.time = currentDay

        //Sets what (the first day) of the week is; e.g., SUNDAY in the U.S.,
        // MONDAY in France.
        calendar.firstDayOfWeek = Calendar.MONDAY

        //DAY_OF_WEEK
        //Field number for get and set indicating the day of the week.
        // This field takes values SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, and SATURDAY.
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

        var days = arrayOfNulls<String>(7)

        for (i in 0..6) {

            days[i] = format.format(calendar.time)

            calendar.add(Calendar.DAY_OF_MONTH, 1)

        }

        val numColumns = 8
        // Column can have many stacked subcolumns, here I use 4 stacke subcolumn in each of 4 columns.
        val columns = ArrayList<Column>()
        var values: MutableList<SubcolumnValue>
        for (i in 0 until numColumns) {

            listEventsToTalTimer.add(0)
            //This is values of (a single column)
            values = ArrayList()
            for (task in listTask) {

                if (i < 7 && task.date.equals(days[i])) {

                    var offsetTimer = 0.toLong()

                    var timer = 0.toLong()

                    var timerFormat = SimpleDateFormat("HH:mm")

                    var startTime = timerFormat.parse(task.startTime)

                    var endTime = timerFormat.parse(task.endTime)

                    var difference: Long

                    difference = endTime.time - startTime.time - task.remainingTime

                    offsetTimer = (endTime.time - startTime.time) / 60 / 1000

                    timer += difference / 60 / 1000

                    listEventsToTalTimer.add(offsetTimer)

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

        for (indexColumn in data!!.columns.indices) {

            var currentColumn = data!!.columns.get(indexColumn)

            count++

            for (index in currentColumn.values.indices) {

                count++

                var currentSubColumn = currentColumn.values.get(index)

                var currentValue = currentSubColumn.value

                var targetValue = 0.toFloat()

                if (dataType == RESPECTIVE_DATA) {

                    targetValue = listEventsToTalTimer.get(count-1).toFloat() - currentValue

                } else {

                    targetValue = listEventsToTalTimer.get(indexColumn).toFloat() - currentValue

                }

                currentSubColumn.setTarget(targetValue)
            }
        }
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