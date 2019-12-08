package com.e15.alarmnats.activity

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.e15.alarmnats.R

//Interface SharedPreferences.onSharedPreferencesChangeListener
//(Interface definition) for a callback to be invoked when a (shared preference) is changed
class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    private var themeChangeListener: OnThemeChangeListener? = null
    private var sharedPreferences: SharedPreferences? = null

    //(Interface services) to classes (override) onThemeChange() method
    interface OnThemeChangeListener {
        fun onThemeChange()
    }

    //Called during <onCreate(Bundle)> to supply the preferences for this fragment.
    // Subclasses are expected to call setPreferenceScreen(PreferenceScreen) either directly
    // or via helper methods such as addPreferencesFromResource(int).
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {

        addPreferencesFromResource(R.xml.preferences_pomodoro)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState?: Bundle.EMPTY)

        if (context != null) {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context!!)
        }
    }

    override fun onResume() {
        super.onResume()
        //Registers a callback to be invoked when a change happens to a preference.
        sharedPreferences!!.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        sharedPreferences!!.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            themeChangeListener = context as OnThemeChangeListener
        } catch (e: ClassCastException) {
            throw ClassCastException(e.toString() + "implement OnThemeChangeListener")
        }

    }

    /**
     * Trigger recreate of activity to create dynamic theme change
     */
    public override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {

        var setting: SharedPreferences = android.preference.PreferenceManager.getDefaultSharedPreferences(activity)

        var sessionTime = setting.getInt("pref_work_time", 25);

//        Toast.makeText(activity,sessionTime.toString(), Toast.LENGTH_SHORT).show();

        //Finds a (Preference) based on (its key).
        val pref = findPreference(key)

        val themeLight = resources.getString(R.string.LightTheme)
        val themeDark = resources.getString(R.string.DarkTheme)
        val themeBlack = resources.getString(R.string.BlackTheme)

        if (pref is ListPreference) {
            val value = sharedPreferences.getString(key, "")
            if (value == themeLight || value == themeDark || value == themeBlack) {
                themeChangeListener?.onThemeChange()
            }
        }
    }

    companion object {

        val instance: SettingsFragment
            get() = SettingsFragment()
    }
}