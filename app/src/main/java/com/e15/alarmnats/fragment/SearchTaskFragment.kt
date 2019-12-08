package com.e15.alarmnats.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.e15.alarmnats.Database.ReminderDatabase
import com.e15.alarmnats.Model.Event
import com.e15.alarmnats.Model.FilterOfSection
import com.e15.alarmnats.R
import com.e15.alarmnats.adapter.ExpandableEventsSection
import com.e15.alarmnats.utils.LoadEventsUsecase
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter

class SearchTaskFragment: Fragment(),ExpandableEventsSection.ClickListener, SearchView.OnQueryTextListener {
    //A custom RecyclerView Adapter that allows Sections to be added to it.
    // Sections are displayed in the same order they were added.
    lateinit var sectionAdapter: SectionedRecyclerViewAdapter

    lateinit var listAllEvents:ArrayList<Event>

    lateinit var templistAllEvents:ArrayList<Event>
    lateinit var searchView: SearchView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        var view=inflater.inflate(R.layout.activity_search_task, container, false)

        var mdbHandler= ReminderDatabase(activity!!.applicationContext)

        listAllEvents= ArrayList()

        listAllEvents=mdbHandler.allEventsCustom

        templistAllEvents= ArrayList(listAllEvents)

        sectionAdapter= SectionedRecyclerViewAdapter()

        var eventsMap:MutableMap<String, ArrayList<Event>> =
                LoadEventsUsecase().groupByType(activity!!.applicationContext,listAllEvents)


        for(entry in eventsMap.entries){

            Log.e("------------","key "+entry.key+" value "+entry.value)

            sectionAdapter.addSection(ExpandableEventsSection(entry.key,entry.value,this))

        }

        var recycleView: RecyclerView =view.findViewById(R.id.recyclerSearch)

        recycleView.layoutManager= LinearLayoutManager(context)

        recycleView.adapter=sectionAdapter

        searchView=view.findViewById(R.id.search_events)

        searchView.setOnQueryTextListener(this)

        return view

    }

    override fun onHeaderRootViewClicked(section: ExpandableEventsSection) {

        var wasExpanded=section.expanded

        var sectionAdapter=sectionAdapter.getAdapterForSection(section)

        var previousItemsTotal=section.contentItemsTotal

        section.expanded=!wasExpanded

        sectionAdapter.notifyHeaderChanged()

        if(wasExpanded){

            //Notify any registered observers that the itemCount items starting
            // at position positionStart have changed. Equivalent to calling
            // notifyItemRangeChanged(position, itemCount, null);.
            //This is an item change event, not a structural change event.
            // It indicates that any reflection of the data in the given position
            // range is out of date and should be updated.
            // The items in the given range retain the same identity.
            sectionAdapter.notifyItemRangeRemoved(0,previousItemsTotal)

        }else{

            sectionAdapter.notifyAllItemsInserted()

        }

    }

    override fun onItemRootViewClicked(sectionTile: String, itemAdapterPosition: Int) {

        Toast.makeText(
                context,
                String.format(
                        "Clicked on position #%s of Section %s",
                        sectionAdapter.getPositionInSection(itemAdapterPosition),
                        sectionTile
                ),
                Toast.LENGTH_SHORT
        ).show()

    }

    override fun onQueryTextSubmit(query: String?): Boolean {

        return false

    }

    override fun onQueryTextChange(newText: String?): Boolean {

        //Filter the result of Adapter
        for(section in sectionAdapter.copyOfSectionsMap.values){

            if(section is FilterOfSection){
                section.filter(newText!!)
            }

        }

        sectionAdapter.notifyDataSetChanged()

        return false

    }

}
