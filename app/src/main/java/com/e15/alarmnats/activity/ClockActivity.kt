package com.e15.alarmnats.activity

import android.os.Bundle
import android.text.Html
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat

import com.e15.alarmnats.R
import com.e15.alarmnats.adapter.MyPagerAdapter
import com.e15.alarmnats.fragment.ClockFragment
import com.e15.alarmnats.fragment.SlideFragment
import kotlinx.android.synthetic.main.clock_layout.*
import java.text.SimpleDateFormat
import java.util.*

class ClockActivity : AppCompatActivity() {
//    internal lateinit var alarm_function: ImageView
//    internal lateinit var weather_function: ImageView

    lateinit var mDotLayout:LinearLayout;

    lateinit var mdots: Array<TextView>;

    lateinit var t:Thread;

    lateinit var mainLayout:RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //getSupportActionBar().setTitle(R.string.app_name);

//        alarm_function = findViewById(R.id.imgAlarmFunction)
//        weather_function = findViewById(R.id.imgWeatherFunction)

        mainLayout=findViewById(R.id.mainLayout)

        val fragmentOne = ClockFragment();
        val fragmentTwo = SlideFragment()
        var framentThree= AlarmListActivity()

        val pagerAdapter = MyPagerAdapter(supportFragmentManager)
        pagerAdapter.addFragment(fragmentOne)
        pagerAdapter.addFragment(framentThree)

        val viewPager = findViewById<View>(R.id.slidePager) as ViewPager
        viewPager.adapter = pagerAdapter

        mDotLayout=findViewById(R.id.mDotLayout);

        mdots= Array(2,init = {i-> TextView(this) });

        for(i in 0..mdots.size-1){

            mdots[i]= TextView(this);

            //Returns displayable styled text from
            // the provided HTML string with the legacy flags FROM_HTML_MODE_LEGACY.

            //This code uses specialization logo in HTML
            mdots[i].setText(Html.fromHtml("&#8226;"))

            mdots[i].setTextSize(35.toFloat())

            mdots[i].setTextColor(resources.getColor(R.color.white_smoke))

            mDotLayout.addView(mdots[i])

        }

        addDotsIndicator(1);

        viewPager.setOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

                addDotsIndicator(position)

            }

            override fun onPageSelected(position: Int) {
//                if (position == 0) {
//                    alarm_function.setImageResource(R.drawable.ic_alarm_choose)
//
//                    //Sets the amount that the view is scaled in x around the pivot point,
//                    // as a proportion of the view's unscaled width. A value of 1 means that no scaling is applied.
//                    alarm_function.scaleX = 1f
//                    alarm_function.scaleY = 1f
//
//                    weather_function.setImageResource(R.drawable.ic_weather_not_choose)
//                    weather_function.scaleX = 0.7.toFloat()
//                    weather_function.scaleY = 0.7.toFloat()
//                } else {
//                    alarm_function.setImageResource(R.drawable.ic_alarm_not_choose)
//                    alarm_function.scaleX = 0.7.toFloat()
//                    alarm_function.scaleY = 0.7.toFloat()
//
//                    weather_function.setImageResource(R.drawable.ic_weather_choose)
//                    weather_function.scaleX = 1f
//                    weather_function.scaleY = 1f
//                }
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })


        var calendar=Calendar.getInstance()

        var timeOfDay=calendar.get(Calendar.HOUR_OF_DAY)

        if (timeOfDay>=0&&timeOfDay<12) {

            mainLayout.background=ContextCompat.getDrawable(this,R.drawable.hello_morning)

        } else if (timeOfDay>=12&&timeOfDay<16) {

            mainLayout.background=ContextCompat.getDrawable(this,R.drawable.hello_afternoon)

        } else {

            mainLayout.background=ContextCompat.getDrawable(this,R.drawable.hello_evening)

        }

    }

    fun addDotsIndicator(position:Int){

        //This code is called to reset all state of (indicator pointer)
        for(i in 0..mdots.size-1){

            mdots[i].setTextColor(resources.getColor(R.color.white_smoke))

        }

        if(mdots.size>0){
            mdots[position].setTextColor(resources.getColor(R.color.light_sea_green))
        }

    }

}