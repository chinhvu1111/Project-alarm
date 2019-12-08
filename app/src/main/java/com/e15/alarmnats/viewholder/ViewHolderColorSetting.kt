package com.e15.alarmnats.viewholder

import android.view.View
import android.widget.GridView
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.e15.alarmnats.R

class ViewHolderColorSetting: RecyclerView.ViewHolder {

    var imageIcon: ImageView

    var tvtitleSetting: TextView

    var tvDetailSetting: TextView

    var rootView: View

    var colorGrids: GridView

    constructor(itemView: View) : super(itemView){

        rootView=itemView.findViewById(R.id.lnLayoutRootSettingColor)

        imageIcon=itemView.findViewById(R.id.img_icon_setting_color)

        tvtitleSetting=itemView.findViewById(R.id.tvtitleSettingColor)

        tvDetailSetting=itemView.findViewById(R.id.tvDetailSettingColor)

        colorGrids=itemView.findViewById(R.id.colorEventGrid)

    }

}