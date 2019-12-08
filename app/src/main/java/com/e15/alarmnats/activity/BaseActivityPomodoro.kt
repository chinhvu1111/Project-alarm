package com.e15.alarmnats.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.e15.alarmnats.utils.NotificationHelper
import com.e15.alarmnats.utils.ThemeUtil

abstract class BaseActivityPomodoro : AppCompatActivity() {

    protected val THEME_REQUEST_CODE = 1


    //This function is used to add Fragment
    protected fun addFragmentToActivity(
            fragManager: FragmentManager,
            fragContainer: Int,
            fragment: Fragment
    ) {
        fragManager.beginTransaction()
                .add(fragContainer, fragment)
                .commit()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Check Preference of corresponding Activity() to change UI
        ThemeUtil.themeCheck(this)
        //getLifeCycle()
        //Returns the Lifecycle of the provider.
        //Overriding this method is no longer supported
        // and this method will be made final in a future version of ComponentActivity. If you do override this method, you must:

        //addObserver
        //Adds a (LifecycleObserver) that will be notified when the (LifecycleOwner) changes state.
        //The given observer will be brought to the current state of the LifecycleOwner.
        // For example, if the LifecycleOwner is in Lifecycle.State.STARTED state,
        // the given observer will receive Lifecycle.Event.ON_CREATE, Lifecycle.Event.ON_START events.

        //The Activitis implement this (BaseActivity.kt) is registered with (NotificationHelper)
        lifecycle.addObserver(NotificationHelper(this))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == THEME_REQUEST_CODE) {
            //Cause this Activity to be recreated with (a new instance).
            // This results in essentially the same flow as when the Activity is created due to (a configuration change) --
            // the current instance will go through its lifecycle to onDestroy and a new instance then created after it.
            //--> Restart Activity()
            recreate()
        }
    }
}