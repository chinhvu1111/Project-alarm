package com.e15.alarmnats.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.e15.alarmnats.R
import com.e15.alarmnats.fragment.CategoryDialogFragment
import com.e15.alarmnats.view.ColorCircle

class ColorsAdapter(internal var context: Context, //List of (Categories) correspond to (the color)
                    private val colors: List<CategoryDialogFragment.Colors>) : BaseAdapter() {

    override fun getCount(): Int {
        return colors.size
    }

    override fun getItem(position: Int): Any {
        return colors[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    //Display each colors
    inner class ViewHolder(convertView: View) {
        val colorCircle: ColorCircle?

        init {
            colorCircle = convertView.findViewById<View>(R.id.item) as ColorCircle?
        }
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView

        var vHolder: ViewHolder? = null

        if (convertView == null) {

            //inflate (the layout) correspond to (color layout)
            convertView = LayoutInflater.from(context).inflate(R.layout.color_items, parent, false)

            vHolder = ViewHolder(convertView!!)

            //Sets the tag associated with this view.
            // A tag can be used to mark a view in (its hierarchy) and does (not) have to be (unique) within the hierarchy.
            // Tags can also be used to (store data) within a view without (resorting) to another data structure.
            convertView!!.tag = vHolder

        } else {

            vHolder = convertView.tag as ViewHolder

        }

        //If you press on (the color)
        // what you (want to choose)
        //The corner is apprear
        if (colors[position].isSelected) {

            //Get (color) by name then set (a coner) for (this color)
            //Parameter isCorner is passed into this (function/method)
            vHolder.colorCircle?.setColorAndConer(Color.parseColor(colors[position].name), true)

        } else {

            vHolder.colorCircle?.setColorAndConer(Color.parseColor(colors[position].name), false)

        }


        return convertView
    }

    //Gets a View that displays in the (drop down popup) the data
    // at the (specified position) in the (data set).
    override fun getDropDownView(position: Int, convertView: View, parent: ViewGroup): View {

        return super.getDropDownView(position, convertView, parent)

    }
}