package com.e15.alarmnats.activity

import android.content.Context
import android.os.Bundle
import android.widget.GridView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.e15.alarmnats.Model.SettingItem
import com.e15.alarmnats.R
import com.e15.alarmnats.adapter.SettingColorAdapter
import com.e15.alarmnats.fragment.CategoryDialogFragment

class ColorSettingActivity : AppCompatActivity() {

    var listTypes= ArrayList<SettingItem>()

    lateinit var recycleViewEvents: RecyclerView

    lateinit var colorGrids: GridView

    lateinit var listColors:ArrayList<CategoryDialogFragment.Colors>

    lateinit var colorFromres:Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_color_setting)

        var sharePreferences=applicationContext.getSharedPreferences("backgroundEvents", Context.MODE_PRIVATE)

        var colorDonow=sharePreferences.getInt("colors 0",0)
        var colorArrange=sharePreferences.getInt("colors 1",0)
        var colorHelp=sharePreferences.getInt("colors 2",0)
        var colorIgnore=sharePreferences.getInt("colors 3",0)

        listTypes= ArrayList()

        var actions=resources.getStringArray(R.array.action_type_events)

        var e=SettingItem()

        e.iconId=R.drawable.ic_event_note_black_15dp

        e.title=actions[0]

        e.detailTitle=""

        if(colorDonow==0) e.backGround=resources.getColor(R.color.donow)
        else{
            e.backGround=colorDonow
        }

        var e1=SettingItem()

        e1.iconId=R.drawable.ic_event_note_black_15dp

        e1.title=actions[1]

        e1.detailTitle=""

        if(colorArrange==0) e1.backGround=resources.getColor(R.color.arrange)
        else{
            e1.backGround=colorArrange
        }

        var e2=SettingItem()

        e2.iconId=R.drawable.ic_event_note_black_15dp

        e2.title=actions[2]

        e2.detailTitle=""

        if(colorHelp==0) e2.backGround=resources.getColor(R.color.help)
        else{
            e2.backGround=colorHelp
        }

        var e3=SettingItem()

        e3.iconId=R.drawable.ic_event_note_black_15dp

        e3.title=actions[3]

        e3.detailTitle=""

        if(colorIgnore==0) e3.backGround=resources.getColor(R.color.ignore)
        else{
            e3.backGround=colorIgnore
        }

        listTypes.add(e)
        listTypes.add(e1)
        listTypes.add(e2)
        listTypes.add(e3)

        recycleViewEvents=findViewById(R.id.recyclerEventColor)

        var adapterSetting=SettingColorAdapter(applicationContext)

        adapterSetting.listItemSetting=listTypes

        recycleViewEvents.layoutManager= LinearLayoutManager(this)

        recycleViewEvents.adapter=adapterSetting

        recycleViewEvents.itemAnimator= DefaultItemAnimator()

        recycleViewEvents.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))


    }

}