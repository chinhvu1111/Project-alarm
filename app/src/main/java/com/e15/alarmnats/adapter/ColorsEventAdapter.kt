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

class ColorsEventAdapter(var context: Context, var colors:ArrayList<CategoryDialogFragment.Colors>): BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        var view=convertView

        var viewHolder:ViewHolderEventColor

        if(view==null){

            view= LayoutInflater.from(context).inflate(R.layout.color_events_item, parent, false)

            viewHolder= ViewHolderEventColor(view)

            view.tag=viewHolder

        }else{

            viewHolder=view.tag as ViewHolderEventColor

        }

        if(colors[position].isSelected){

            viewHolder.colorCircle.setColorAndConer(Color.parseColor(colors[position].name),true)

        }else{

            viewHolder.colorCircle?.setColorAndConer(Color.parseColor(colors[position].name), false)

        }

        return view!!

    }

    override fun getItem(position: Int): Any {

        return colors[position]

    }

    override fun getItemId(position: Int): Long {

        return 0

    }

    override fun getCount(): Int {

        return colors.size

    }

    inner class ViewHolderEventColor{

        lateinit var colorCircle:ColorCircle

        constructor(convertView: View?){

            colorCircle=convertView!!.findViewById(R.id.itemColor)

        }

    }

}