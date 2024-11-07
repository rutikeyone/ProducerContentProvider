package com.consumer.content.impl

import android.app.Activity
import com.contentprovider.core.common.ActivityNotCreatedException
import com.contentprovider.core.common.Resources

class AndroidResources : Resources, ActivityRequired {

    private var currentActivity: Activity? = null

    override fun getString(id: Int): String {
        val activity = currentActivity ?: throw ActivityNotCreatedException()
        return activity.getString(id)
    }

    override fun getString(id: Int, vararg placeholders: Any): String {
        val activity = currentActivity ?: throw ActivityNotCreatedException()
        return activity.getString(id, placeholders)
    }

    override fun onCreated(activity: Activity) {
        this.currentActivity = activity
    }

    override fun onDestroyed() {
        this.currentActivity = null
    }

}