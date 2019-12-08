package com.e15.alarmnats.activity

import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.MenuItem
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.e15.alarmnats.R
import com.e15.alarmnats.databinding.SettingPomodoroBinding
import com.e15.alarmnats.utils.ThemeUtil

class SettingsActivity : AppCompatActivity(), SettingsFragment.OnThemeChangeListener {

    private var sBinding: SettingPomodoroBinding? = null

    public override fun onCreate(onSaveInstanceState: Bundle?) {
        super.onCreate(onSaveInstanceState)
        ThemeUtil.themeCheck(this)
        sBinding = DataBindingUtil.setContentView(this, R.layout.setting_pomodoro)

        supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings_fragment_container, SettingsFragment.instance)
                .commit()

        setSupportActionBar(sBinding!!.toolBar)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }

        updateUI()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Triggered on list preference change in SettingsFragment
     */
    override fun onThemeChange() {
        recreate()
    }

    /**
     * Handles UI of toolbar and status bar during theme change
     */
    private fun updateUI() {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        val appTheme = sharedPref.getString(resources.getString(R.string.THEME_KEY), "Dark")

        if (appTheme == "Light") {
            sBinding!!.toolBar.setTitleTextColor(resources.getColor(R.color.colorLightPrimary))
            sBinding!!.toolBar.setBackground(ColorDrawable(resources.getColor(R.color.colorAccent)))

            val window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.statusBarColor = resources.getColor(R.color.colorAccentDark)
            }

        }
    }
}