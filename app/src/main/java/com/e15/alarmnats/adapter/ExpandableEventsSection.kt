package com.e15.alarmnats.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.e15.alarmnats.Model.Event
import com.e15.alarmnats.Model.FilterOfSection
import com.e15.alarmnats.R
import com.e15.alarmnats.viewholder.HeaderEventViewHolder
import com.e15.alarmnats.viewholder.ViewHolderEventSearch
import io.github.luizgrp.sectionedrecyclerviewadapter.Section
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters

class ExpandableEventsSection: Section, FilterOfSection{
    lateinit var title:String;

    lateinit var listeEvent:ArrayList<Event>

    lateinit var tempListeEvent:ArrayList<Event>;

    lateinit var clickListener:ClickListener

    var expanded=true

    constructor(
            title: String,
            listeEvent: ArrayList<Event>,
            clickListener: ClickListener
    ) : super(SectionParameters.builder().itemResourceId(R.layout.item_task_search).
            headerResourceId(R.layout.search_task_header).build()) {
        this.title = title
        this.listeEvent = listeEvent
        this.clickListener = clickListener

        tempListeEvent= ArrayList(listeEvent)

    }

    override fun getContentItemsTotal(): Int {

        if(expanded){
            return listeEvent.size
        }else{
            return 0
        }

    }

    override fun onBindItemViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {

        var eventViewHolder=holder as ViewHolderEventSearch

        var event=listeEvent.get(position)

        eventViewHolder.id.setText(event.hashId)

        eventViewHolder.title.setText(event.title)

        eventViewHolder.category.setText(event.category)

        eventViewHolder.rootView.setOnClickListener(object: View.OnClickListener{

            override fun onClick(v: View?) {

                clickListener.onItemRootViewClicked(title,eventViewHolder.adapterPosition)

            }
        })

    }


    override fun getItemViewHolder(view: View?): RecyclerView.ViewHolder {

        return ViewHolderEventSearch(view!!)

    }

    override fun getHeaderViewHolder(view: View?): RecyclerView.ViewHolder {

        return HeaderEventViewHolder(view!!)

    }

    override fun onBindHeaderViewHolder(holder: RecyclerView.ViewHolder?) {

        var headerHolderEvent:HeaderEventViewHolder= holder as HeaderEventViewHolder

        headerHolderEvent.tvTitle.setText(title)

        if(expanded){

            headerHolderEvent.imgArrow.setImageResource(R.drawable.ic_arrow_up_white_24dp)

        }
        else{

            headerHolderEvent.imgArrow.setImageResource(R.drawable.ic_arrow_down_white_24dp)

        }

        headerHolderEvent.rootView.setOnClickListener(View.OnClickListener {

            clickListener.onHeaderRootViewClicked(this)

        })


    }

    interface ClickListener{

        fun onHeaderRootViewClicked(section:ExpandableEventsSection)

        fun onItemRootViewClicked(sectionTile: String, itemAdapterPosition:Int)

    }

    override fun filter(query: String) {

        listeEvent= ArrayList(tempListeEvent)

        var listevents=ArrayList<Event>()

        var re=Regex("[^A-Za-z0-9 ]")

        var strSearch=re.replace(query.toLowerCase(),"")

//                strSearch=strSearch.replace("/[!@#\$%^&*]/g","")
//                strSearch=strSearch.replace("s/([()])//g","")

        for(event in listeEvent){

            var regex=".*("+strSearch.toLowerCase()+").*"

            if(event.category!!.toLowerCase().matches(regex = Regex(regex))||
                    event.title!!.toLowerCase().matches(regex = Regex(regex))){

                listevents.add(event)

            }

        }

        listeEvent.clear()

        listeEvent.addAll(listevents)

    }

}
