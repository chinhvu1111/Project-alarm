package com.e15.alarmnats.viewholder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.e15.alarmnats.R

class ViewHolderSetting : RecyclerView.ViewHolder{

    lateinit var imageIcon: ImageView

    lateinit var tvtitleSetting: TextView

    lateinit var tvDetailSetting: TextView

    lateinit var rootView: View

    constructor(itemView: View) : super(itemView){

        rootView=itemView.findViewById(R.id.lnLayoutRoot)

        imageIcon=itemView.findViewById(R.id.img_icon_setting)

        tvtitleSetting=itemView.findViewById(R.id.tvtitleSetting)

        tvDetailSetting=itemView.findViewById(R.id.tvDetailSetting)

    }
}