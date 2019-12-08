package com.e15.alarmnats.fragment

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.*
import com.e15.alarmnats.Database.ReminderDatabase
import com.e15.alarmnats.Model.Event
import com.e15.alarmnats.R
import com.e15.alarmnats.adapter.CustomTaskAdapter
import com.e15.alarmnats.adapter.RecyclerItemTouchHelper
import com.e15.alarmnats.viewholder.ViewHolderEvent
import java.util.*
import kotlin.collections.ArrayList


class DisplayClassifyTasksFragment : Fragment(), RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {
    lateinit var listCurrentEvent:ArrayList<Event>;

    lateinit var listCurrentEvent1:ArrayList<Event>;
    lateinit var listCurrentEvent2:ArrayList<Event>;
    lateinit var listCurrentEvent3:ArrayList<Event>;
    lateinit var important: TextView;

    lateinit var notimportant: TextView;

    lateinit var customTaskAdapter:CustomTaskAdapter

    lateinit var customTaskAdapter1:CustomTaskAdapter
    lateinit var customTaskAdapter2:CustomTaskAdapter
    lateinit var customTaskAdapter3:CustomTaskAdapter

    lateinit var layoutDonow: LinearLayout

    lateinit var layoutArrange: LinearLayout

    lateinit var layoutHelp: LinearLayout

    lateinit var layoutIgnore: LinearLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        var view= inflater.inflate(R.layout.fragment_display_classify_tasks, container,false)

        important=view.findViewById(R.id.important)

        important.setText("Quan trọng")

        notimportant=view.findViewById(R.id.notimportant)

        notimportant.setText("Không quan trọng")

        layoutDonow=view.findViewById(R.id.layoutDonow)

        layoutArrange=view.findViewById(R.id.layoutArrange)

        layoutHelp=view.findViewById(R.id.layoutHelp)

        layoutIgnore=view.findViewById(R.id.layoutIgnore)

        var sharePreferences=activity!!.applicationContext.getSharedPreferences("backgroundEvents", Context.MODE_PRIVATE)

        var colorDonow=sharePreferences.getInt("colors 0",0)
        var colorArrange=sharePreferences.getInt("colors 1",0)
        var colorHelp=sharePreferences.getInt("colors 2",0)
        var colorIgnore=sharePreferences.getInt("colors 3",0)

        if(colorDonow!=0)  {

            var unwrapperDrawable: Drawable = AppCompatResources.getDrawable(activity!!.applicationContext,R.drawable.classify_task_background)!!

            var wrappedDrawable= DrawableCompat.wrap(unwrapperDrawable)

            DrawableCompat.setTint(wrappedDrawable,colorDonow)

            layoutDonow.background=wrappedDrawable

        }
        else{

//            layoutDonow.background.

            var unwrapperDrawable: Drawable = AppCompatResources.getDrawable(activity!!.applicationContext,R.drawable.classify_task_background)!!

            var wrappedDrawable= DrawableCompat.wrap(unwrapperDrawable)

            DrawableCompat.setTint(wrappedDrawable,resources.getColor(R.color.donow))

            layoutDonow.background=wrappedDrawable
        }

        if(colorArrange!=0) {

            var unwrapperDrawable: Drawable = AppCompatResources.getDrawable(activity!!.applicationContext,R.drawable.classify_task_background)!!

            var wrappedDrawable= DrawableCompat.wrap(unwrapperDrawable)

            DrawableCompat.setTint(wrappedDrawable,colorArrange)

            layoutArrange.background=wrappedDrawable

        }
        else{

            var unwrapperDrawable: Drawable = AppCompatResources.getDrawable(activity!!.applicationContext,R.drawable.classify_task_background)!!

            var wrappedDrawable= DrawableCompat.wrap(unwrapperDrawable)

            DrawableCompat.setTint(wrappedDrawable,resources.getColor(R.color.arrange))

            layoutArrange.background=wrappedDrawable
        }

        if(colorHelp!=0) {

            var unwrapperDrawable: Drawable = AppCompatResources.getDrawable(activity!!.applicationContext,R.drawable.classify_task_background)!!

            var wrappedDrawable= DrawableCompat.wrap(unwrapperDrawable)

            DrawableCompat.setTint(wrappedDrawable,colorHelp)

            layoutHelp.background=wrappedDrawable

        }
        else{

            var unwrapperDrawable: Drawable = AppCompatResources.getDrawable(activity!!.applicationContext,R.drawable.classify_task_background)!!

            var wrappedDrawable= DrawableCompat.wrap(unwrapperDrawable)

            DrawableCompat.setTint(wrappedDrawable,resources.getColor(R.color.help))

            layoutHelp.background=wrappedDrawable
        }

        if(colorIgnore!=0) {

            var unwrapperDrawable: Drawable = AppCompatResources.getDrawable(activity!!.applicationContext,R.drawable.classify_task_background)!!

            var wrappedDrawable= DrawableCompat.wrap(unwrapperDrawable)

            DrawableCompat.setTint(wrappedDrawable,colorIgnore)

            layoutIgnore.background=wrappedDrawable

        }
        else{

            var unwrapperDrawable: Drawable = AppCompatResources.getDrawable(activity!!.applicationContext,R.drawable.classify_task_background)!!

            var wrappedDrawable= DrawableCompat.wrap(unwrapperDrawable)

            DrawableCompat.setTint(wrappedDrawable,resources.getColor(R.color.ignore))

            layoutIgnore.background=wrappedDrawable

            //Lets you assign a number of graphic images to a single Drawable and swap out the visible item by a string ID value.
//            var currentDrawable:StateListDrawable= layoutIgnore.background as StateListDrawable
//
//            var dcs:DrawableContainer.DrawableContainerState= currentDrawable.constantState as DrawableContainer.DrawableContainerState
//
//            var drawableItems=dcs.children
//
//            var gradientDrawable:GradientDrawable= drawableItems[0] as GradientDrawable
//
//            gradientDrawable.setStroke(2,resources.getColor(R.color.color_white))

        }

//        var ranim:RotateAnimation= AnimationUtils.loadAnimation(this,R.anim.animtext) as RotateAnimation;
//
//        //If fillAfter is true, the transformation
//        // that this animation performed will persist when it is finished.
//        // Defaults to false if not set. Note that this applies to
//        // individual animations and when using an AnimationSet to chain animations.
//        ranim.fillAfter=true
//
//        important.animation=ranim


        listCurrentEvent=ArrayList();
        listCurrentEvent1=ArrayList();
        listCurrentEvent2=ArrayList();
        listCurrentEvent3=ArrayList();

        customTaskAdapter =CustomTaskAdapter(listCurrentEvent)
        customTaskAdapter1 =CustomTaskAdapter(listCurrentEvent1)
        customTaskAdapter2 =CustomTaskAdapter(listCurrentEvent2)
        customTaskAdapter3 =CustomTaskAdapter(listCurrentEvent3)

        var recycleTask=view.findViewById<RecyclerView>(R.id.listdonow)

        var recycleTask1=view.findViewById<RecyclerView>(R.id.listarrange)

        var recycleTask2=view.findViewById<RecyclerView>(R.id.listhelp)

        var recycleTask3=view.findViewById<RecyclerView>(R.id.listignore)

//        recycleTask.setOnClickListener(object : View.OnClickListener{
//
//            override fun onClick(v: View?) {
//                Toast.makeText(applicationContext,"Onclick recycleView",Toast.LENGTH_SHORT).show()
//            }
//
//        })

        recycleTask.adapter=customTaskAdapter
        recycleTask2.adapter=customTaskAdapter1
        recycleTask1.adapter=customTaskAdapter2
        recycleTask3.adapter=customTaskAdapter3

        recycleTask.itemAnimator= DefaultItemAnimator()

        recycleTask.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))

        recycleTask1.itemAnimator= DefaultItemAnimator()

        recycleTask1.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))

        recycleTask2.itemAnimator= DefaultItemAnimator()

        recycleTask2.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))

        recycleTask3.itemAnimator= DefaultItemAnimator()

        recycleTask3.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))

        recycleTask.layoutManager= LinearLayoutManager(this.activity!!)
        recycleTask1.layoutManager= LinearLayoutManager(this.activity!!)
        recycleTask2.layoutManager= LinearLayoutManager(this.activity!!)
        recycleTask3.layoutManager= LinearLayoutManager(this.activity!!)

        for(i in 0..listCurrentEvent.size-1){
            Log.e("--------------",listCurrentEvent.get(i).description)
        }

        val itemTouchHelperCallback = RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT, this)

        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recycleTask)

        var listener1=object:RecyclerItemTouchHelper.RecyclerItemTouchHelperListener{
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int, position: Int) {

                var title=listCurrentEvent1.get(viewHolder.adapterPosition).title

                var item=listCurrentEvent1.get(viewHolder.adapterPosition)

                var deletedIn=viewHolder.adapterPosition

                customTaskAdapter1.removeItem(deletedIn)

//                TaskBaseOnCustomCategoryFragment.listEvent1.removeAt(position)


            }
        }

        val itemTouchHelperCallback1 = RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT, listener1)

        ItemTouchHelper(itemTouchHelperCallback1).attachToRecyclerView(recycleTask1)

        //-------Type of Task 2

        var listener2=object:RecyclerItemTouchHelper.RecyclerItemTouchHelperListener{
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int, position: Int) {

                var title=listCurrentEvent2.get(viewHolder.adapterPosition).title

                var item=listCurrentEvent2.get(viewHolder.adapterPosition)

                var deletedIn=viewHolder.adapterPosition

                customTaskAdapter2.removeItem(deletedIn)

//                TaskBaseOnCustomCategoryFragment.listEvent2.removeAt(position)


            }
        }

        val itemTouchHelperCallback2 = RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT, listener2)

        ItemTouchHelper(itemTouchHelperCallback2).attachToRecyclerView(recycleTask2)

        //----------Type of task 3

        var listener3=object:RecyclerItemTouchHelper.RecyclerItemTouchHelperListener{
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int, position: Int) {

                var title=listCurrentEvent3.get(viewHolder.adapterPosition).title

                var item=listCurrentEvent3.get(viewHolder.adapterPosition)

                var deletedIn=viewHolder.adapterPosition

                customTaskAdapter3.removeItem(deletedIn)

//                TaskBaseOnCustomCategoryFragment.listEvent3.removeAt(position)


            }
        }

        val itemTouchHelperCallback3 = RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT, listener3)

        ItemTouchHelper(itemTouchHelperCallback3).attachToRecyclerView(recycleTask3)

        return view

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        ViewModelTask.context=activity!!.applicationContext

        var viewmodel= ViewModelProviders.of(this).get(ViewModelTask::class.java)

        Log.e("----------add",viewmodel.listTask?.value?.size.toString())

//        viewmodel.listTask!!.removeObservers(this)

        viewmodel.listTask!!.observe(getViewLifecycleOwner(), object: Observer<ArrayList<Event>> {

            override fun onChanged(t: ArrayList<Event>?) {

                Log.e("----------add","changing")

                listCurrentEvent.clear()

                listCurrentEvent.addAll(t!!)

                customTaskAdapter.notifyDataSetChanged()

//                activity!!.recreate()

            }

        })

        viewmodel.listTask1!!.observe(getViewLifecycleOwner(), object: Observer<ArrayList<Event>> {

            override fun onChanged(t: ArrayList<Event>?) {

                Log.e("----------add","changing")

                listCurrentEvent1.clear()

                listCurrentEvent1.addAll(t!!)

                customTaskAdapter1.notifyDataSetChanged()

//                activity!!.recreate()

            }

        })

        viewmodel.listTask2!!.observe(getViewLifecycleOwner(), object: Observer<ArrayList<Event>> {

            override fun onChanged(t: ArrayList<Event>?) {

                Log.e("----------add","changing")

                listCurrentEvent2.clear()

                listCurrentEvent2.addAll(t!!)

                customTaskAdapter2.notifyDataSetChanged()

//                activity!!.recreate()

            }

        })

        viewmodel.listTask3!!.observe(getViewLifecycleOwner(), object: Observer<ArrayList<Event>> {

            override fun onChanged(t: ArrayList<Event>?) {

                Log.e("----------add","changing")

                listCurrentEvent3.clear()

                listCurrentEvent3.addAll(t!!)

                customTaskAdapter3.notifyDataSetChanged()

//                activity!!.recreate()

            }

        })

    }

    override fun onResume() {
        Log.e("-----------","The fragment is paused!")
        super.onResume()
    }

    override fun onPause() {

        Log.e("-----------","The fragment is paused!")

        super.onPause()
    }

    override fun onStop() {
        super.onStop()

        Log.e("-----------","The fragment is stopped!")
    }

    override fun onDestroy() {
        super.onDestroy()

        Log.e("-----------","The fragment is destroyed!")

    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int, position: Int) {

        if(viewHolder is ViewHolderEvent){

            var title=listCurrentEvent.get(viewHolder.adapterPosition).title

            var item=listCurrentEvent.get(viewHolder.adapterPosition)

            var deletedIn=viewHolder.adapterPosition

            customTaskAdapter.removeItem(deletedIn)

//            TaskBaseOnCustomCategoryFragment.listEvent.removeAt(position)

        }

    }

}
class ViewModelTask: ViewModel() {

    var x= MutableLiveData<Event>()

    companion object {
        lateinit var context:Context
    }

    var mdHandler=ReminderDatabase(context)

    val calendar:Calendar
    get() {
        //For filtering base on option
        var sharedPreferences= context.getSharedPreferences("CalendarPre", Activity.MODE_PRIVATE)

        var year=sharedPreferences.getInt("YearPre",-1)
        var month=sharedPreferences.getInt("MonthPre",-1)
        var day=sharedPreferences.getInt("DayPre",-1)
        var hour=sharedPreferences.getInt("HourPre",-1)
        var minute=sharedPreferences.getInt("MinutePre",-1)

        var calendar= Calendar.getInstance()

        calendar.set(Calendar.YEAR,-1)

        calendar.set(Calendar.MONTH,-1)

        calendar.set(Calendar.DAY_OF_MONTH,-1)

        //set all values to -1 value
        calendar.set(Calendar.HOUR_OF_DAY,-1)

        calendar.set(Calendar.MINUTE,-1)

        var listTemp=ArrayList<Event>()

        //Filter base on (date)
        if(year!=-1&&month!=-1&&day!=-1){

            calendar.set(Calendar.YEAR,year)

            calendar.set(Calendar.MONTH,month-1)

            calendar.set(Calendar.DAY_OF_MONTH,day)

        }

        //Filter base on (time)
        if(hour!=-1&&minute!=-1){

            calendar.set(Calendar.HOUR_OF_DAY,hour)

            calendar.set(Calendar.MINUTE,minute)

        }

        return calendar

    }

    var listTask: MutableLiveData<ArrayList<Event>> = mdHandler.getEvents(1,1, calendar)
    var listTask1: MutableLiveData<ArrayList<Event>> = mdHandler.getEvents(1,0, calendar)
    var listTask2: MutableLiveData<ArrayList<Event>> = mdHandler.getEvents(0,1, calendar)
    var listTask3: MutableLiveData<ArrayList<Event>> = mdHandler.getEvents(0,0, calendar)

//    fun temp()= TaskBaseOnCustomCategoryFragment().getList()

//    init {
//        tastBaseOnCustomCategoryFragment=TaskBaseOnCustomCategoryFragment()
//    }

}
