package com.e15.alarmnats.adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

import java.util.ArrayList

//Static library support version of the framework's android.app.FragmentManager.
// Used to write apps that run on platforms prior to Android 3.0.
// When running on Android 3.0 or above, this implementation is still used;
// it does not try to switch to the framework's implementation.
// See the framework FragmentManager documentation for a class overview.
class MyPagerAdapter(manager: FragmentManager) : FragmentPagerAdapter(manager) {

    private val mContext: Context? = null
    private val mFragments = ArrayList<Fragment>()

    fun addFragment(fragment: Fragment) {
        mFragments.add(fragment)
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return mFragments.size
    }

    override fun getItem(position: Int): Fragment {
        return mFragments[position]
    }
}