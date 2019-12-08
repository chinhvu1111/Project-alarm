package com.e15.alarmnats.utils

import android.app.Application
import com.google.firebase.database.FirebaseDatabase

class FbApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        FirebaseDatabase.getInstance().setPersistenceEnabled(true)

    }

}