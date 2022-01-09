package com.example.nearbyworkers.utils

import android.app.Application
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage


class MyFirebase:Application()
{
    override fun onCreate() {
        super.onCreate()
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)

    }
}