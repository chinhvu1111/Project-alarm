package com.e15.alarmnats.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TableLayout
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.e15.alarmnats.R
import com.google.android.material.tabs.TabLayout

class TeamWorkManagementActivity : AppCompatActivity() {

//    lateinit var mToolbar:Toolbar

    lateinit var mviewPager:ViewPager

    lateinit var adapterViewPage:FragmentStatePagerAdapter

    lateinit var mTabLayout:TabLayout

    lateinit var groupFragment:Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_team_work_management)

//        mToolbar=findViewById(R.id.toolBarGroup)
//
//        setSupportActionBar(mToolbar)

        adapterViewPage=MyPagerTeamWork(supportFragmentManager)

        mviewPager=findViewById(R.id.vpGroup)

        mviewPager.adapter=adapterViewPage

        mTabLayout=findViewById(R.id.tabMainGroupTask)

        mTabLayout.setupWithViewPager(mviewPager)

    }

    inner class MyPagerTeamWork: FragmentStatePagerAdapter {

        constructor(fm:FragmentManager):super(fm)

        override fun getItem(position: Int): Fragment {

            when(position){
                1->{
                    groupFragment=GroupTask()

                    return groupFragment

                }
            }

            return Fragment()

        }

        override fun getCount(): Int {
            return 3;
        }

        override fun getPageTitle(position: Int): CharSequence? {
            when(position){
                0->{
                    return "Trò chuyện"
                }
                1->{
                    return "Nhóm"
                }
                2->{
                    return "Thành viên"
                }
                else->{
                    return null
                }
            }
        }

    }

}
