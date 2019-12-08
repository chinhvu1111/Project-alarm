package com.e15.alarmnats.viewholder

import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.e15.alarmnats.R

class ViewHolderEventDoneGraph: RecyclerView.ViewHolder {

    var id: TextView;

    var dayOfWeek: TextView

    var title: TextView;

    var doneState: TextView

    var viewForeground: LinearLayout

    var rootView: View

    constructor(itemView: View) : super(itemView){

        rootView=itemView

        this.id=itemView.findViewById<TextView>(R.id.tvidState);

        this.dayOfWeek=itemView.findViewById<TextView>(R.id.tvDayState)

        this.title=itemView.findViewById<TextView>(R.id.tvTitleState)

        this.doneState=itemView.findViewById(R.id.tvDoneState)

        this.viewForeground=itemView.findViewById(R.id.lnLayuoutState)

    }

}