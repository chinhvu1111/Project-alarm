package com.e15.alarmnats.fragment

import android.os.Bundle
import android.preference.PreferenceFragment
import com.e15.alarmnats.R

class SettingFragment : PreferenceFragment() {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        addPreferencesFromResource(R.xml.preference)

    }
}
