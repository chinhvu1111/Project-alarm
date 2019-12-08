package com.e15.alarmnats.fragment

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.e15.alarmnats.Database.ReminderDatabase
import com.e15.alarmnats.Model.Event
import com.e15.alarmnats.R
import com.e15.alarmnats.adapter.EventDoneGraphAdapter
import lecho.lib.hellocharts.model.PieChartData
import lecho.lib.hellocharts.model.SliceValue
import lecho.lib.hellocharts.view.PieChartView

class DisplayDoneTaskFragment: Fragment() {

    lateinit var eventsChart: PieChartView

    var listEvent=ArrayList<SliceValue>()

    lateinit var recycleDone: RecyclerView

    lateinit var listEventDone:List<Event>;

    lateinit var listEventUndone:List<Event>;

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        var mdbHandler=ReminderDatabase(activity!!.applicationContext)

        listEventDone=mdbHandler.allEventsCustom.filter { event: Event -> event.isDone==1 }

        listEventUndone=mdbHandler.allEventsCustom.filter { event: Event -> event.isDone!=1 }

        var view=inflater.inflate(R.layout.fragment_display_done_tasks, container, false)

        ViewModelTask.context=activity!!.applicationContext

        var viewModelTask=ViewModelTask()


        var total=mdbHandler.allEvents.size

        Log.e("-------------Done", listEventDone.size.toString())
        Log.e("-------------Total", total.toString())

        var doneRate:Float=0.toFloat();

        if(total!=0||((total==0)&&(listEventDone.size!=0))){

            doneRate= (listEventDone.size.toFloat()/(total.toFloat()+ listEventDone.size.toFloat())).toFloat()*100

        }

//        Log.e("--------------",(listEventDone.size/(total+ listEventDone.size)).toFloat().toString())

        eventsChart=view.findViewById<PieChartView>(R.id.eventsChart)

        var unDoneRate=100-doneRate

        var slideDone= SliceValue(unDoneRate, resources.getColor(R.color.search)).setLabel("Chưa hoàn thành $unDoneRate%")

        var slideUndone= SliceValue(doneRate, resources.getColor(R.color.lighterBlue)).setLabel("Hoàn thành $doneRate%")

        listEvent.add(slideDone)
        listEvent.add(slideUndone)

        var pieChartData= PieChartData(listEvent)

        pieChartData.setHasLabels(true).valueLabelTextSize=15

        pieChartData.setHasCenterCircle(true).setCenterText1("% công việc").setCenterText1FontSize(16).
                setCenterCircleColor(Color.parseColor("#0097A7"))

        eventsChart.pieChartData=pieChartData

        recycleDone=view.findViewById(R.id.recycleEventDone)

        var eventDoneGraphAdapterView = EventDoneGraphAdapter()

        var eventUndoneGraphAdapter=EventDoneGraphAdapter()

        eventDoneGraphAdapterView.listEventState= listEventDone as ArrayList<Event>

        eventUndoneGraphAdapter.listEventState= listEventUndone as ArrayList<Event>

        recycleDone.layoutManager= LinearLayoutManager(activity!!.applicationContext)

        recycleDone.adapter=eventDoneGraphAdapterView

        var rdDoneEvents: RadioButton =view.findViewById(R.id.rdDoneEvents)

        var rdUndoneEvents: RadioButton =view.findViewById(R.id.rdUndoneEvents)

        rdDoneEvents.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {

                recycleDone.adapter=eventDoneGraphAdapterView

            }
        })

        rdUndoneEvents.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {

                recycleDone.adapter=eventUndoneGraphAdapter

            }
        })

        if(rdDoneEvents.isChecked){

            recycleDone.adapter=eventDoneGraphAdapterView

        }else{

            recycleDone.adapter=eventUndoneGraphAdapter

        }

        return view;

    }

}