package com.e15.alarmnats.viewholder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.e15.alarmnats.R

class HeaderEventViewHolder : RecyclerView.ViewHolder {

    lateinit var rootView: View

    lateinit var tvTitle: TextView

    lateinit var imgArrow: ImageView

    constructor(itemView: View) : super(itemView) {

        rootView = itemView

        tvTitle = itemView.findViewById(R.id.tvHeaderTitle)

        imgArrow=itemView.findViewById(R.id.imgArrow)

    }
}