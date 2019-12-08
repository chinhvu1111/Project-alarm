package com.e15.alarmnats.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.e15.alarmnats.Model.SettingItem
import com.e15.alarmnats.R
import com.e15.alarmnats.adapter.Setting_adapter

class Setting_general_EIS_Fragment: Fragment() {

    lateinit var recyclerSetting: RecyclerView

    lateinit var listItemSetting:ArrayList<SettingItem>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        var view=inflater.inflate(R.layout.fragment_setting_eis, container, false)

        recyclerSetting=view.findViewById(R.id.recyclerSetting)

        listItemSetting= ArrayList()

        var item=SettingItem()

        item.iconId=R.drawable.ic_color_lens_black_24dp

        item.title="Thiết lập màu sắc cho tác vụ"

        item.detailTitle="Màu sắc sẽ được thiết lập cho 4 loại tác vụ dựa trên mức độ"

        item.backGround=R.color.color_white

        item.id=1

        var item1=SettingItem()

        item1.iconId=R.drawable.ic_alarm_black_24dp

        item1.title="Lọc tác vụ"

        item1.detailTitle="Tác vụ sẽ được lọc dựa trên thời gian bắt đầu tác vụ đó"

        item1.backGround=R.color.colorWhite

        item1.id=2

        listItemSetting.add(item)

        listItemSetting.add(item1)

        var setting_adapter=Setting_adapter(activity!!.applicationContext,activity!!)

        setting_adapter.listItemSetting=this.listItemSetting

        recyclerSetting.layoutManager= LinearLayoutManager(activity!!.applicationContext)

        recyclerSetting.adapter=setting_adapter

        recyclerSetting.itemAnimator= DefaultItemAnimator()

        recyclerSetting.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))

        return view

    }

}