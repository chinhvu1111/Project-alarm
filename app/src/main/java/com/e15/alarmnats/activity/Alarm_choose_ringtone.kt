package com.e15.alarmnats.activity

import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.GridView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.e15.alarmnats.Model.AlarmSound
import com.e15.alarmnats.R
import com.e15.alarmnats.adapter.GridAdapter

class Alarm_choose_ringtone : AppCompatActivity() {

    lateinit var grid: GridView;

    lateinit var alarm_chooser_toolbar: Toolbar

    var sound = AlarmSound(this)

    var respectiveText = arrayOf("Bridge",
            "Bình minh",
            "Bầu trời đêm",
            "Suối",
            "Mặt trời mọc",
            "Thác nước",
            "Núi",
            "Biển")

    var imageId =
            arrayOf(R.drawable.bridge_image,
                    R.drawable.new_dawn_image,
                    R.drawable.night_sky_image,
                    R.drawable.stream_image,
                    R.drawable.sunset_image,
                    R.drawable.waterfall_image,
                    R.drawable.mountain_image,
                    R.drawable.beach_image)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_choose_ringtone)

//        alarm_chooser_toolbar = findViewById(R.id.alarm_chooser_toolbar) as Toolbar
//        alarm_chooser_toolbar.setTitle("")
//        setSupportActionBar(alarm_chooser_toolbar!!)
//        alarm_chooser_toolbar.setTitle(R.string.alarm_chooser_toolbar_title)
//        alarm_chooser_toolbar.setTitleTextColor(Color.WHITE)
//
//        // Get a support ActionBar corresponding to this toolbar
//        val ab = supportActionBar
//
//        // Enable the Up button
//        ab?.setDisplayHomeAsUpEnabled(true)

        var gridAdapter: GridAdapter = GridAdapter(this, respectiveText, imageId)

        grid = findViewById(R.id.grid)

        grid.adapter = gridAdapter

        grid.setOnItemClickListener(object : AdapterView.OnItemClickListener {

            override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                var preferences = getSharedPreferences("alarm_tune", Context.MODE_PRIVATE);

                var editor = preferences.edit();

                when (position) {

                    0 -> {
                        editor.putInt("tune", R.raw.down_stream);

                        editor.apply()

                        Toast.makeText(applicationContext,"Đã lưu âm báo", Toast.LENGTH_SHORT).show()

                    }
                    1 -> {
                        editor.putInt("tune", R.raw.new_dawn);

                        editor.apply()

                        Toast.makeText(applicationContext,"Đã lưu âm báo", Toast.LENGTH_SHORT).show()

                    }
                    2 -> {
                        editor.putInt("tune", R.raw.bittersweet);

                        editor.apply()

                        Toast.makeText(applicationContext,"Đã lưu âm báo", Toast.LENGTH_SHORT).show()

                    }
                    3 -> {
                        editor.putInt("tune", R.raw.silver_flame);

                        editor.apply()

                        Toast.makeText(applicationContext,"Đã lưu âm báo", Toast.LENGTH_SHORT).show()

                    }
                    4 -> {
                        editor.putInt("tune", R.raw.river_fire);

                        editor.apply()

                        Toast.makeText(applicationContext,"Đã lưu âm báo", Toast.LENGTH_SHORT).show()

                    }
                    5 -> {
                        editor.putInt("tune", R.raw.soaring);

                        editor.apply()

                        Toast.makeText(applicationContext,"Đã lưu âm báo", Toast.LENGTH_SHORT).show()

                    }
                    6 -> {
                        editor.putInt("tune", R.raw.dreamer);

                        editor.apply()

                        Toast.makeText(applicationContext,"Đã lưu âm báo", Toast.LENGTH_SHORT).show()

                    }
                    7 -> {
                        editor.putInt("tune", R.raw.fireflies_and_stardust);

                        editor.apply()

                        Toast.makeText(applicationContext,"Đã lưu âm báo", Toast.LENGTH_SHORT).show()

                    }

                    else ->{

                    }

                }
                sound.stopRingTone()

                sound.chooseTrack(preferences.getInt("tune",0))

                sound.playRingTone()

            }

        })

    }

    override fun onDestroy() {

        sound.stopRingTone()

        super.onDestroy()
    }

}
