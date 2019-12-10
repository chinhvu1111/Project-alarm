package com.e15.alarmnats.activity

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import android.widget.FrameLayout
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.e15.alarmnats.Model.SettingItem
import com.e15.alarmnats.R
import com.e15.alarmnats.adapter.Setting_adapter
import com.e15.alarmnats.adapter.Setting_general_adapter
import com.e15.alarmnats.fragment.SettingFragment
import kotlinx.android.synthetic.main.activity_demo_tab.*

class SettingActivity : AppCompatActivity() {

    lateinit var recyclerSetting: RecyclerView

    lateinit var listItemSetting:ArrayList<SettingItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting_general)

        recyclerSetting=findViewById(R.id.recyclerSettingGeneral)

        listItemSetting= ArrayList()

        var item=SettingItem()

        item.iconId=R.drawable.ic_work_black_24dp

        item.title="Thiết lập số lần chia tác vụ"

        item.detailTitle="Tức là số lần tối ta bạn có thể tạo ra các tác vụ con"

        item.backGround=R.color.color_white

        item.id=1

        var item1=SettingItem()

        item1.iconId=R.drawable.ic_account_circle_black_24dp

        item1.title="Đăng nhập"

        item1.detailTitle="Đăng nhập để đồng bộ dữ liệu"

        item1.backGround=R.color.colorWhite

        item1.id=2

        listItemSetting.add(item)

        listItemSetting.add(item1)

        var sharedPreferences=applicationContext.getSharedPreferences("accountLogin",Activity.MODE_PRIVATE)

        var username=sharedPreferences.getString("username","")

        var email=sharedPreferences.getString("Email","")

        if(!email.equals("")){

            var item2=SettingItem()

            item2.title="Hồ sơ bản thân"

            item2.detailTitle="Các nội dung liên quan đến bản thân người dùng"

            item2.iconId=R.drawable.user_profile

            item2.backGround=R.color.colorWhite

            item2.id=3

            listItemSetting.add(item2)

            var item3=SettingItem()

            item3.title="Đăng xuất"

            item3.detailTitle="${email}"

            item3.iconId=R.drawable.logout_64

            item3.backGround=R.color.colorWhite

            item3.id=4

            listItemSetting.add(item3)

        }

        var setting_adapter= Setting_general_adapter(applicationContext,this)

        setting_adapter.listItemSetting=this.listItemSetting

        recyclerSetting.layoutManager= LinearLayoutManager(this!!.applicationContext)

        recyclerSetting.adapter=setting_adapter

        recyclerSetting.itemAnimator= DefaultItemAnimator()

        recyclerSetting.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

    }
}
