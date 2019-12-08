package com.e15.alarmnats.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.localbroadcastmanager.content.LocalBroadcastManager

//(LifeCycleObserver)
//This class is add to (LifecycleOwner)
class NotificationHelper(context: Context) : LifecycleObserver {

    private val notificationReciever: BroadcastReceiver
    private val notificationFilter: IntentFilter
    private val localManager: LocalBroadcastManager

    init {

        localManager = LocalBroadcastManager.getInstance(context)

        //Handling BroadCast by function receviver of class NotificationReceiver()
        notificationReciever = NotificationReceiver()

        notificationFilter = IntentFilter()

    }

    //Even.ON_CREATE
    //Constant for (onCreate event) of (the LifecycleOwner).
    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun registerReceiver() {
        //ACTION_CREEN_OFF
        //Broadcast Action: Sent when the device goes to sleep and becomes non-interactive.
        notificationFilter.addAction(Intent.ACTION_SCREEN_OFF)

        localManager.registerReceiver(notificationReciever, notificationFilter)
    }

    //Constant for (onStop event) of (the LifecycleOwner).
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun unregisterReceiver() {

        localManager.unregisterReceiver(notificationReciever)

    }

}