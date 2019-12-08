package com.e15.alarmnats.adapter

import android.content.Context
import android.graphics.Color
import android.view.*
import android.view.animation.AccelerateInterpolator
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.e15.alarmnats.Database.ReminderDatabase
import com.e15.alarmnats.Model.Event
import com.e15.alarmnats.R
import com.e15.alarmnats.utils.AlarmReceiver
import com.e15.alarmnats.utils.Utils
import com.e15.alarmnats.view.ColorCircle
import java.util.ArrayList

class AllEventsAdapter(private val context: Context, allItems: MutableList<Event>) : RecyclerView.Adapter<AllEventsAdapter.EventViewHolder>(), Filterable {
    private var allItemList = ArrayList<Event>()
    private var filterList: MutableList<Event> = ArrayList<Event>()
    private var hideOrShowListener: HideOrShowListener
    private var lastPosition: Int = 0
    private var flag = false
    private var dbHandler: ReminderDatabase


    init {
        hideOrShowListener = context as HideOrShowListener
        //Note warning
        this.allItemList = allItems as ArrayList<Event>
        filterList = allItems
        dbHandler = ReminderDatabase(context)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.default_events_item, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val item = filterList[position]

        val startTime = item.startTime!!.trim().split(":")
        val endTime = item.endTime!!.trim().split(":")
        val date = item.date!!.trim().split("/")
        val startHour = Integer.parseInt(startTime[0])
        val endHour = Integer.parseInt(endTime[0])

        holder.tvEventName.setText(item.title)
        holder.colorCircleView.setColor(Color.parseColor(item.color))

        var textStart:String=""

        if (startHour < 12){
            textStart=startHour.toString() + " : " + startTime[1] + " am "
        }
        else {
            textStart=(startHour - 12).toString() + " : " + startTime[1] + " pm "
        }

        var textEnd:String=""

        if (endHour < 12) {

            textEnd=endHour.toString() + " : " + endTime[1] + " am"

        }
        else{

            textEnd=(endHour - 12).toString() + " : " + endTime[1] + " pm"

        }

        holder.tvTime.text = textStart+textEnd
        holder.tvDate.setText(Utils.months[Integer.parseInt(date[0])-1] + " " + date[1])
        holder.tvPlace.setText(item.place)
        if (item.description!!.length === 0) {
            item.description=context.resources.getString(R.string.no_detail)
        }
        holder.tvDescription.setText(item.description)

        holder.tvDescription.visibility = View.VISIBLE

        //Sets the interpolator for the underlying animator that animates the requested properties.
        // By default, the animator uses the default interpolator for ValueAnimator.
        // Calling this method will cause the declared object to be used instead.
        holder.tvDescription.animate().alpha(1f).setDuration(200).setInterpolator(AccelerateInterpolator()).start()
        holder.tvEdit.visibility = View.VISIBLE


        holder.tvEdit.setOnClickListener { hideOrShowListener.setHideOrShow(true, item) }


    }

    override fun getItemCount(): Int {
        return filterList.size
    }


    //Filter class
    //(A filter constrains data) with a (filtering pattern).
    //Filters are usually created by (Filterable classes)
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): Filter.FilterResults {
                val query = charSequence.toString()
                if (query.isEmpty()) {
                    filterList = allItemList
                } else {
                    val filterItem = ArrayList<Event>()
                    for (item in allItemList) {
                        if (item.title!!.toLowerCase().contains(query.toLowerCase()) || item.description!!.toLowerCase().contains(query.toLowerCase())) {
                            filterItem.add(item)
                        }
                    }
                    filterList = filterItem
                }

                val results = Filter.FilterResults()
                results.values = filterList
                return results
            }

            override fun publishResults(constraint: CharSequence, results: Filter.FilterResults) {
                filterList = results.values as MutableList<Event>
                notifyDataSetChanged()
            }
        }
    }


    inner class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener, View.OnCreateContextMenuListener {
        val descriptionLayout: LinearLayout
        val containerLayout: LinearLayout
        val eventContainer: RelativeLayout
        val tvEventName: TextView
        val tvTime: TextView
        val tvDate: TextView
        val tvPlace: TextView
        val tvDescription: TextView
        val tvEdit: TextView
        val colorCircleView: ColorCircle

        private val onEditMenu = MenuItem.OnMenuItemClickListener { item ->
            when (item.itemId) {
                0 -> hideOrShowListener.setHideOrShow(true, filterList[adapterPosition])
                1 -> {
                    dbHandler.removeEvent(filterList[adapterPosition].hashId)

                    Utils.showToastMessage("Deleted", context)

                    AlarmReceiver().cancelAlarm(context, filterList[adapterPosition].requestCode)

                    val deleteItem = filterList[adapterPosition]

                    filterList.removeAt(adapterPosition)

                    //refresh original datalist
                    for (i in allItemList.indices) {
                        if (deleteItem.hashId.equals(allItemList.get(i).hashId)) {
                            allItemList.removeAt(i)
                        }
                    }
                    notifyItemRemoved(adapterPosition)
                    hideOrShowListener.deleteEventCallBack()
                }
            }
            true
        }

        init {
            descriptionLayout = itemView.findViewById<View>(R.id.descriptionLayout) as LinearLayout
            containerLayout = itemView.findViewById<View>(R.id.container) as LinearLayout
            eventContainer = itemView.findViewById<View>(R.id.eventContainer) as RelativeLayout
            tvEventName = itemView.findViewById<View>(R.id.tvEventName) as TextView
            tvTime = itemView.findViewById<View>(R.id.tvTime) as TextView
            tvDate = itemView.findViewById<View>(R.id.tvDate) as TextView
            tvPlace = itemView.findViewById<View>(R.id.tvPlace) as TextView
            tvDescription = itemView.findViewById<View>(R.id.tvDescription) as TextView
            tvEdit = itemView.findViewById<View>(R.id.tvEdit) as TextView
            colorCircleView = itemView.findViewById<View>(R.id.color_circle) as ColorCircle
            eventContainer.setOnClickListener(this)
            eventContainer.setOnCreateContextMenuListener(this)
            descriptionLayout.setOnCreateContextMenuListener(this)
        }

        override fun onClick(v: View) {
            if (flag) {
                filterList[lastPosition].isShow=false
            }
            filterList[adapterPosition].isShow=true
            flag = true
            lastPosition = adapterPosition
            notifyDataSetChanged()
        }

        //Called when 9the context menu for this view) is being built.
        override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {

            //Display Dialog choose
            menu.setHeaderTitle("Chọn tác vụ")

            //Add a (new item) to the menu. This item displays the (given title) for its label.

            //groupId – The group identifier that this item should be part of. This can be used to define groups of items for batch state changes. Normally use NONE if an item should not be in a group.
            //itemId – Unique item ID. Use NONE if you do not need a unique ID.
            //order – The order for the item. Use NONE if you (do not care about) the (order). See MenuItem.getOrder().
            //title – The text to display for the item.
            val Edit = menu.add(Menu.NONE, 0, 0, context.resources.getString(R.string.menu_edit))
            val Delete = menu.add(Menu.NONE, 1, 0, context.resources.getString(R.string.menu_delete))
            Edit.setOnMenuItemClickListener(onEditMenu)
            Delete.setOnMenuItemClickListener(onEditMenu)
        }
    }


    interface HideOrShowListener {
        fun setHideOrShow(isEdit: Boolean, item: Event)
        fun deleteEventCallBack()
    }

    companion object {
        val EVENT_TYPE = 1
    }


}