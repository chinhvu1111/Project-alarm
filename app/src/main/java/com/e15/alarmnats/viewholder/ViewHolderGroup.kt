package com.e15.alarmnats.viewholder

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.e15.alarmnats.R

class ViewHolderGroup: RecyclerView.ViewHolder {

    lateinit var imgIconGroup:ImageView

    lateinit var tvNameGroup:TextView

    lateinit var tvNumberOfMembers:TextView

    lateinit var tvHour:TextView

    lateinit var btnJoin:Button

    constructor(itemView:View):super(itemView){

        imgIconGroup=itemView.findViewById(R.id.imgIconGroup)

        tvNameGroup=itemView.findViewById(R.id.tvNameGroup)

        tvNumberOfMembers=itemView.findViewById(R.id.tvNumberOfMembers)

        tvHour=itemView.findViewById(R.id.tvHour)

        btnJoin=itemView.findViewById(R.id.btnJoin)

    }

}