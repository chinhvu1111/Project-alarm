package com.e15.alarmnats.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.e15.alarmnats.Model.Event
import com.e15.alarmnats.R
import com.e15.alarmnats.viewholder.ViewHolderEventDoneGraph

class EventDoneGraphAdapter: RecyclerView.Adapter<ViewHolderEventDoneGraph>() {

    lateinit var listEventState:ArrayList<Event>

    lateinit var dayOfWeek:String

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderEventDoneGraph {

        var view= LayoutInflater.from(parent.context).inflate(R.layout.item_task_layout_done_graph, parent, false)

        var viewHolderEventDoneGraph=ViewHolderEventDoneGraph(view)

        return viewHolderEventDoneGraph

    }

    override fun getItemCount(): Int {

        return listEventState.size

    }

    override fun onBindViewHolder(holder: ViewHolderEventDoneGraph, position: Int) {

        var event=listEventState.get(position)

        holder.id.setText(event.requestCode.toString())

        holder.dayOfWeek.setText(event.date)

        holder.title.setText(event.title)

        if(event.isDone==1){

            holder.doneState.setText("Đã hoàn thành")

        }else{

            holder.doneState.setText("Chưa hoàn thành")

        }

        var sharePreferences=holder.rootView.context.getSharedPreferences("backgroundEvents", Context.MODE_PRIVATE)

        var colorDonow=sharePreferences.getInt("colors 0",0)
        var colorArrange=sharePreferences.getInt("colors 1",0)
        var colorHelp=sharePreferences.getInt("colors 2",0)
        var colorIgnore=sharePreferences.getInt("colors 3",0)


        if(event.isUrgent&&event.isImportant){

            if(colorDonow!=0){

                holder.id.setBackgroundColor(colorDonow)

            }else{

//                holder.viewForeground.setBackgroundColor()

            }

        }

        if(!event.isUrgent&&event.isImportant){

            if(colorArrange!=0){

                holder.id.setBackgroundColor(colorArrange)

            }else{

            }

        }

        if(event.isUrgent&&!event.isImportant){

            if(colorHelp!=0){

                holder.id.setBackgroundColor(colorHelp)

            }else{

            }

        }

        if(!event.isUrgent&&!event.isImportant){

            if(colorIgnore!=0){

                holder.id.setBackgroundColor(colorIgnore)

            }else{

            }

        }

    }

}