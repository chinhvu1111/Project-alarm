package com.e15.alarmnats.adapter

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.ActivityInfo
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.e15.alarmnats.Database.ReminderDatabase
import com.e15.alarmnats.Model.Event
import com.e15.alarmnats.R
import com.e15.alarmnats.activity.TimerActivity
import com.e15.alarmnats.fragment.DisplayDoneTaskFragment
import com.e15.alarmnats.fragment.TaskBaseOnCustomCategoryFragment
import com.e15.alarmnats.fragment.ViewModelTask
import com.e15.alarmnats.viewholder.ViewHolderEvent
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CustomTaskAdapter: RecyclerView.Adapter<ViewHolderEvent> {

    var listEvent:ArrayList<Event>;

    constructor(listEvent: ArrayList<Event>) : super() {
        this.listEvent = listEvent
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderEvent {

        var context=parent.context

        var view: View? = LayoutInflater.from(context).inflate(R.layout.item_task_layout_list,parent,false)

        //assign (view) to parameter of (ViewHolderTask)
        //ListView will use directly
        var viewHolderTask: ViewHolderEvent = ViewHolderEvent(view!!);

        //Like getMeasuredHeightAndState(),
        // but only returns the (raw height component)
        // (that is the result is masked by MEASURED_SIZE_MASK).
//        var height=parent.measuredHeight/4;

        //Sets the minimum height of the view.
        // It is not guaranteed the view will be able to achieve this minimum height (for example,
        // if its parent layout constrains it with less available height)
//        view.minimumHeight=height

        return viewHolderTask;

    }

    override fun getItemCount(): Int {

        return listEvent.size

    }

    override fun onBindViewHolder(holder: ViewHolderEvent, position: Int) {

//        var currentPosition=position

        var event=listEvent.get(position)

        Log.e("--------------",event.description)

        holder.id.setText(event.requestCode.toString())

        holder.title.setText(event.title)

        holder.category.setText(event.category)

        holder.imgUpitem.setOnClickListener(object: View.OnClickListener{

            override fun onClick(v: View?) {

//                Log.e("--------position",position.toString())
//                Log.e("--------currentPosition",position.toString())

                var currentPosition=listEvent.indexOf(event)

                if(currentPosition>0){

                    listEvent.removeAt(currentPosition).also {
                        listEvent.add(currentPosition-1,it)
                    }

                    notifyItemMoved(currentPosition, currentPosition-1)

//                    currentPosition--

                }

            }

        })

        holder.imgDownItem.setOnClickListener(object: View.OnClickListener{

            override fun onClick(v: View?) {

//                Log.e("--------position",position.toString())
//                Log.e("--------currentPosition",currentPosition.toString())

                var currentPosition=listEvent.indexOf(event)

                if(currentPosition<listEvent.size-1){

                    listEvent.removeAt(currentPosition).also {

                        listEvent.add(currentPosition+1,it)

                    }

                    notifyItemMoved(currentPosition, currentPosition+1)

//                    currentPosition++;

                }

//                if(currentPosition==listEvent.size-1){
//
//                    currentPosition=currentPosition%(listEvent.size-1)
//
//                }

            }

        })

        holder.viewForeground.setOnLongClickListener(object: View.OnLongClickListener{

            override fun onLongClick(v: View?): Boolean {

                var builder: AlertDialog.Builder= AlertDialog.Builder(holder.rootView.context, R.style.MyDialogTheme)

                builder.setTitle("Chọn tác vụ mong muốn")

                var choices= arrayOf("Thực hiện với Pomodoro","Đánh dấu hoàn thành", "Đánh dấu là bỏ qua chưa hoàn thành","Xóa và bỏ qua")

                builder.setItems(choices, DialogInterface.OnClickListener { dialog, which ->

                    var list=ArrayList<Event>()

                    var c=0;

                    ViewModelTask.context=holder.rootView.context

                    var viewModelTask= ViewModelTask()

                    //Because both refer to the same object
                    if(event.isUrgent&&event.isImportant){

                        list=viewModelTask.listTask.value!!

                    }

                    Toast.makeText(holder.rootView.context,event.isImportant.toString()+" "+event.isUrgent, Toast.LENGTH_SHORT).show()

                    if(!event.isUrgent&&event.isImportant){

                        list=viewModelTask.listTask2!!.value!!

                    }

                    if(event.isUrgent&&!event.isImportant){

                        list=viewModelTask.listTask1!!.value!!

                    }

                    if(!event.isUrgent&&!event.isImportant){

                        list=viewModelTask.listTask3!!.value!!

                    }

                    when(which){

                        0->{

                            val i = Intent(holder.rootView.context, TimerActivity::class.java)

                            i.putExtra("id",event.hashId);
                            i.putExtra("date",event

                            !!.date);
                            i.putExtra("startTime",event.startTime);
                            i.putExtra("endTime",event.endTime);
                            i.putExtra("title",event.title)
                            i.putExtra("description",event.description);
                            i.putExtra("enabled",event.enabled);

                            i.putExtra("taskIsDone",event.isDone)
                            i.putExtra("remainingTime",event.remainingTime)
                            i.putExtra("fromClass","EIS")

                            i.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP

                            Toast.makeText(holder.rootView.context,"${event.hashId} `${event.title} ${event.remainingTime}",Toast.LENGTH_SHORT).show()

                            holder.rootView.context.startActivity(i)

                            (holder.rootView.context as Activity).finish()

                        }

                        1->{


                            if(list.size>0){

                                Toast.makeText(holder.rootView.context,"Đánh dấu task ${event.title} là hoàn thành", Toast.LENGTH_SHORT).show()

                                //Marking at current task done
                                list.get(position).isDone=1

                                var mdbHandler=ReminderDatabase(holder.rootView.context);

                                mdbHandler.updateEvent(list.get(position))

//                                DisplayDoneTaskFragment.listEventDone.add(event)
//                            list.removeAt(position)

                                list.removeAt(position)

                                removeItem(position)

                            }


                        }

                        2->{


                            if(list.size>0){

                                Toast.makeText(holder.rootView.context,"Đánh dấu task ${event.title} là bỏ qua chưa hoàn thành", Toast.LENGTH_SHORT).show()

                                //Marking at current task undone
                                list.get(position).isDone=0

//                                DisplayDoneTaskFragment.listEventUndone.add(event)

                                list.removeAt(position)

                                removeItem(position)

                            }


                        }

                        3->{

                            if(list.size>0){

                                Toast.makeText(holder.rootView.context,"Xóa task ${event.title}", Toast.LENGTH_SHORT).show()

                                //Removing forever
                                list.removeAt(position)

                                removeItem(position)

                            }

                        }

                    }

                })

                var dialog: AlertDialog =builder.create()

                dialog.show()

                return true

            }

        })

    }

    fun removeItem(position:Int){

        listEvent.removeAt(position)

        //Notify any registered observers that the item previously located at position
        // has been removed from the data set. The items previously located at
        // and after position may now be found at oldPosition - 1.
        notifyItemRemoved(position)

    }

//    fun splitToComponentTimes(biggy:Long):String{
//
//        val longVal = biggy
//        val hours = longVal / 3600
//        var remainder = longVal - hours * 3600
//        val mins = remainder / 60
//        remainder = remainder - mins * 60
//        val secs = remainder
//
//        return "$hours giờ $mins phút và $secs giây"
//
//    }

}