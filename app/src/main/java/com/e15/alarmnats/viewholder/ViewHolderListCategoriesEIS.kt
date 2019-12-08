package com.e15.alarmnats.viewholder

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.e15.alarmnats.R

class ViewHolderListCategoriesEIS: RecyclerView.ViewHolder{

    companion object {
        var action="addFragment"
    }

    lateinit var tvId: TextView

    lateinit var tvcategory: TextView

    constructor(itemView: View) : super(itemView){

        tvId=itemView.findViewById(R.id.tvId)

        tvcategory=itemView.findViewById(R.id.tvcategory);

    }
}