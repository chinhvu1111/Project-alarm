package com.e15.alarmnats.viewholder

import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.e15.alarmnats.R

class ViewHolderEventSearch: RecyclerView.ViewHolder {
    lateinit var id: TextView;

    lateinit var title: TextView;

    lateinit var category: TextView

    lateinit var viewForeground: LinearLayout

    lateinit var rootView: View

    constructor(itemView: View) : super(itemView){

        rootView=itemView

        this.id=itemView.findViewById<TextView>(R.id.tvidSearch);

        this.title=itemView.findViewById<TextView>(R.id.tvTitleSearch)

        this.category=itemView.findViewById(R.id.tvCategorySearch)

        this.viewForeground=itemView.findViewById(R.id.lnSearchLayout)

    }
}