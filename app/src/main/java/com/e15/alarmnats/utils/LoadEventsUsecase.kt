package com.e15.alarmnats.utils

import android.content.Context
import com.e15.alarmnats.Model.Event
import com.e15.alarmnats.R

class LoadEventsUsecase{

    fun groupByType(context: Context, listAllEvents:ArrayList<Event>):MutableMap<String, ArrayList<Event>>{

        var map:MutableMap<String, ArrayList<Event>> = mutableMapOf();

        //(Action) corresponding to the (event)
        var action_type_events=context.resources.getStringArray(R.array.action_type_events)

        for(action in action_type_events){

            var listEvents=getEventsWithGroup(listAllEvents,action)

            map.put(action, listEvents)

        }

        return map

    }

    fun getEventsWithGroup(events:ArrayList<Event>,level:String):ArrayList<Event>{

        var listEvents=ArrayList<Event>()

        for(e in events){

            if(e.level.equals(level)){

                listEvents.add(e)

            }

        }

        return listEvents

    }

}