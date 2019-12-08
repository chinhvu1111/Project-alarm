package com.e15.alarmnats.viewholder

import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.e15.alarmnats.R

class ViewHolderEvent: RecyclerView.ViewHolder {

    lateinit var id: TextView;

    lateinit var title: TextView;

    lateinit var category: TextView

    lateinit var viewForeground: LinearLayout

    lateinit var imgUpitem: ImageView

    lateinit var imgDownItem: ImageView

    lateinit var rootView: View

    constructor(itemView: View) : super(itemView){

        rootView=itemView

        this.id=itemView.findViewById<TextView>(R.id.tvid);

        this.title=itemView.findViewById<TextView>(R.id.tvTitle)

        this.category=itemView.findViewById(R.id.tvCategory)

        this.imgUpitem=itemView.findViewById(R.id.imgUpItem)

        this.imgDownItem=itemView.findViewById(R.id.imgDownItem)

        this.viewForeground=itemView.findViewById(R.id.lnLayuout)

    }
}