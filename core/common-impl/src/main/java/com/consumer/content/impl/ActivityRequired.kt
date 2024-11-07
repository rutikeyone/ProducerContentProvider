package com.consumer.content.impl

import android.app.Activity

interface ActivityRequired {

    fun onCreated(activity: Activity)

    fun onStarted() {}

    fun onStopped() {}

    fun onDestroyed() {}

}