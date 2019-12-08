package com.e15.alarmnats.adapter

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.e15.alarmnats.Model.SettingItem
import com.e15.alarmnats.R
import com.e15.alarmnats.activity.ColorSettingActivity
import com.e15.alarmnats.fragment.CategoryDialogFragment
import com.e15.alarmnats.viewholder.ViewHolderColorSetting

class SettingColorAdapter: RecyclerView.Adapter<ViewHolderColorSetting> {
    lateinit var listItemSetting: ArrayList<SettingItem>

    lateinit var context: Context

    lateinit var colorFromres:Array<String>

    constructor(context: Context) : super() {
        this.context = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderColorSetting {

        var view = LayoutInflater.from(parent.context).inflate(R.layout.item_setting_color, parent, false)

        var viewHolderSetting = ViewHolderColorSetting(view)

        return viewHolderSetting

    }

    override fun getItemCount(): Int {

        return listItemSetting.size

    }

    override fun onBindViewHolder(holder: ViewHolderColorSetting, position: Int) {

        var holderSettingItem = holder as ViewHolderColorSetting

        holderSettingItem.imageIcon.setImageResource(listItemSetting.get(position).iconId)

        holderSettingItem.tvtitleSetting.setText(listItemSetting.get(position).title)

        holderSettingItem.tvDetailSetting.setText(listItemSetting.get(position).detailTitle)


        holderSettingItem.rootView.setBackgroundColor((listItemSetting.get(position).backGround))


        if (listItemSetting.get(position).id == 1) {

            holderSettingItem.rootView.setOnClickListener(object : View.OnClickListener {

                override fun onClick(v: View?) {

                    var intent: Intent = Intent(context, ColorSettingActivity::class.java)

                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

                    context.startActivity(intent)

                }
            })

        }

        var intColor=(listItemSetting.get(position).backGround)

        colorFromres=context.resources.getStringArray(R.array.listColors)

        //find the current color
        var currentPositionOfColor=findPositionOfThecurrentColor(String.format("#%06X",(0xFFFFFF and intColor)))

        Log.e("----------current",String.format("#%06X",(0xFFFFFF and intColor))+" "+colorFromres.get(0))

        var listColors=ArrayList<CategoryDialogFragment.Colors>()

        addColorToList(currentPositionOfColor,listColors)

        var colorsEventAdapter:ColorsEventAdapter= ColorsEventAdapter(context, listColors)

        holder.colorGrids.adapter=colorsEventAdapter

        var i=position

        holder.colorGrids.onItemClickListener=object: AdapterView.OnItemClickListener{

            override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                Toast.makeText(context,"Clicking this view!", Toast.LENGTH_SHORT).show()

                listColors.clear()

                addColorToList(position,listColors)

                colorsEventAdapter.notifyDataSetChanged()

                currentPositionOfColor=position

                holderSettingItem.rootView.setBackgroundColor(Color.parseColor(colorFromres.get(position)))

                var edit: SharedPreferences.Editor=context.getSharedPreferences("backgroundEvents", Context.MODE_PRIVATE).edit()

                edit.putInt("colors $i", Color.parseColor(colorFromres.get(position)))

                edit.commit()

            }
        }

    }

    fun addColorToList(position:Int, listColors:ArrayList<CategoryDialogFragment.Colors>){

        for(index in colorFromres.indices){

            var newColor= CategoryDialogFragment.Colors()

            newColor.name=colorFromres[index]

            if(index==position){

                newColor.isSelected=true

            }
            else{
                newColor.isSelected=false
            }

            listColors.add(newColor)

        }

    }

    fun findPositionOfThecurrentColor(currentColor:String):Int{

        var position=0;

        for(index in colorFromres.indices){

            if(currentColor.equals(colorFromres.get(index))){

                return index

            }

        }

        return position

    }

}