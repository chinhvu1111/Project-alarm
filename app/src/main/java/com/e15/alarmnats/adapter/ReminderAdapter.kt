package com.e15.alarmnats.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class ReminderAdapter(manager: FragmentManager): FragmentPagerAdapter(manager) {

    var mFragments=ArrayList<Fragment>()

    fun addFragment(fragment: Fragment){

        mFragments.add(fragment)

        notifyDataSetChanged()

    }

    override fun getItem(position: Int): Fragment {
        return mFragments[position]
    }

    override fun getCount(): Int {
        return mFragments.size
    }
}