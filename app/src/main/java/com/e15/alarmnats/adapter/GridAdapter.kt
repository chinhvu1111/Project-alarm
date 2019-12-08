package com.e15.alarmnats.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.e15.alarmnats.R

class GridAdapter : BaseAdapter {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        var grid: View;

        var inflater: LayoutInflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater;

        if (convertView == null) {

            grid = View(mContext);

            grid = inflater.inflate(R.layout.item_grid_layout,null);

            var textView=grid.findViewById<TextView>(R.id.grid_text);

            var imageView:ImageView=grid.findViewById(R.id.grid_image)

            textView.setText(respectiveText[position])

            imageView.setImageResource(imageId[position])

        }else{
            grid=convertView;
        }

        return grid

    }

    override fun getItem(position: Int): Any? {
        return null
    }

    override fun getItemId(position: Int): Long {
        return 0;
    }

    lateinit var mContext: Context;

    lateinit var respectiveText: Array<String>;

    lateinit var imageId: Array<Int>;

    constructor(mContext: Context, respectiveText: Array<String>, imageId: Array<Int>) : super() {
        this.mContext = mContext
        this.respectiveText = respectiveText
        this.imageId = imageId
    }

    override fun getCount(): Int {
        return imageId.size
    }


}