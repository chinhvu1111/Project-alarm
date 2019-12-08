package com.e15.alarmnats.utils

import android.content.Context
import android.preference.PreferenceManager
import com.e15.alarmnats.R

object ThemeUtil {

    private val themeKey = "pref_theme_value"

    //Check theme value of application be included in SharedPreference
    fun themeCheck(activity: Context) {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(activity)
        val appTheme = sharedPref.getString(themeKey, "Dark")

        when (appTheme) {
            "Light" -> activity.setTheme(R.style.LightAppTheme)
            "Dark" -> activity.setTheme(R.style.DarkAppTheme)
            "Black" -> activity.setTheme(R.style.BlackAppTheme)
            else -> {
            }
        }
    }
}