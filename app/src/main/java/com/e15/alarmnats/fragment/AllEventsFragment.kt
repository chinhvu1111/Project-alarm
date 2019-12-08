package com.e15.alarmnats.fragment

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.e15.alarmnats.Database.ReminderDatabase
import com.e15.alarmnats.Model.Event
import com.e15.alarmnats.R
import com.e15.alarmnats.activity.TaskManagementActivity
import com.e15.alarmnats.adapter.AllEventsAdapter
import java.util.ArrayList

class AllEventsFragment : Fragment(), SearchView.OnQueryTextListener {

    private var recyclerView: RecyclerView? = null
    private var adapter: AllEventsAdapter? = null
    private var events: ArrayList<Event>? = null
    private var dbHandler: ReminderDatabase? = null
    //private LinearLayout show_message_layout;
    private var searchViewLayout: RelativeLayout? = null
    private var filterList: List<Event>? = null
    private var searchView: SearchView? = null
    private val viewHeight: Int = 0
    private var tv_show_message: TextView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbHandler = ReminderDatabase(context!!)
        events = dbHandler!!.allEvents
        filterList = ArrayList<Event>()

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.all_events_fragment, container, false)
        //show_message_layout = (LinearLayout) view.findViewById(R.id.show_message_layout);
        searchViewLayout = view.findViewById(R.id.search_box_layout)
        searchView = searchViewLayout!!.findViewById<View>(R.id.search_view) as SearchView
        tv_show_message = view.findViewById(R.id.tv_show_message)
        recyclerView = view.findViewById(R.id.recyclerView)
        adapter = AllEventsAdapter(this!!.context!!, this!!.events!!)
        recyclerView!!.layoutManager = LinearLayoutManager(activity)
        recyclerView!!.adapter = adapter

        showCreateEventMessage()

        searchViewLayout!!.visibility = View.GONE


        searchView!!.setIconifiedByDefault(false)
        searchView!!.setOnQueryTextListener(this)


        val search_close_btn = searchViewLayout!!.findViewById<View>(R.id.search_btn_close) as ImageButton
        search_close_btn.setOnClickListener {
            hideViewAnim(searchViewLayout)
        }

        return view
    }


    fun refreshEvents() {
        events!!.clear()
        val allevents = dbHandler!!.allEvents
        events!!.addAll(allevents)
        adapter!!.notifyDataSetChanged()
        showCreateEventMessage()
        //refresh searchview and clear query
        searchView!!.setQuery("", false)
        searchView!!.clearFocus()

    }


    fun showCreateEventMessage() {
        if (events!!.size == 0) {
            tv_show_message!!.visibility = View.VISIBLE
        } else {
            tv_show_message!!.visibility = View.GONE
        }
    }

    fun showSearchLayout() {
        showViewAnim(searchViewLayout)
    }

    fun showViewAnim(view: View?) {
        view!!.translationY = -126f
        view.animate().translationY(0f)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationStart(animation: Animator) {
                        view.visibility = View.VISIBLE
                    }
                })
                .setDuration(200).setInterpolator(DecelerateInterpolator()).start()

        //        recyclerView.animate().translationY(126f)
        //                .setDuration(200).start();
    }

    fun hideViewAnim(view: View?) {
        view!!.animate().translationY((-view.height).toFloat())
                .setDuration(200)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        view.visibility = View.GONE
                        //clear query text
                        searchView!!.setQuery("", false)
                    }
                }).start()
        //        recyclerView.animate().translationY(0)
        //                .setDuration(200).start();

    }


    override fun onQueryTextSubmit(query: String): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String): Boolean {
        adapter!!.getFilter().filter(newText)
        return true
    }


}