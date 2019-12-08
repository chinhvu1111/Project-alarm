package com.e15.alarmnats.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.e15.alarmnats.R
import java.text.SimpleDateFormat

class ClockFragment:Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        var view=inflater.inflate(R.layout.clock_layout, container, false);

        val tDate = view.findViewById(R.id.date) as TextView
        val tTime = view.findViewById(R.id.time) as TextView
        val tGreeting = view.findViewById(R.id.greeting) as TextView
        val tampm = view.findViewById(R.id.ampm) as TextView
        val date = System.currentTimeMillis()

        val sdfDate = SimpleDateFormat("MMM dd yyyy ")
        val sdfTime = SimpleDateFormat("hh:mm")
        val sdfGreetingTime = SimpleDateFormat("HH")

        val dateString = sdfDate.format(date)
        val timeString = sdfTime.format(date)
        val greetingTimeString = sdfGreetingTime.format(date)

        //this is in a try block since the reference returns null when on
        //different pages
        try {
            tDate.text = dateString
            tTime.text = timeString
        } catch (e: NullPointerException) {
        }

        if (Integer.valueOf(greetingTimeString) < 12) {

            tGreeting.text = "Chào buổi sáng"

            tampm.text = "AM"

        } else if (Integer.valueOf(greetingTimeString) < 17) {

            tGreeting.text = "Chào buổi tối"

            tampm.text = "PM"

        } else {

            tGreeting.text = "Chiều đến rồi"

            tampm.text = "PM"

        }

        return view

    }

}