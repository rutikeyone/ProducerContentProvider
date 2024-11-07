package com.consumer.content.impl

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.compose.ui.platform.ComposeView
import com.consumer.content.common_impl.R
import com.contentprovider.core.common.CommonUi
import com.google.android.material.snackbar.Snackbar

class AndroidCommonUi : CommonUi, ActivityRequired {

    private var currentActivity: Activity? = null
    private var isStarted = false

    override fun toast(@StringRes idResource: Int) {
        val activity = this.currentActivity ?: return
        val text = activity.getString(idResource)

        Toast.makeText(activity, text, Toast.LENGTH_LONG).show()

    }

    override fun onCreated(activity: Activity) {
        this.currentActivity = activity
    }

    override fun onStarted() {
        isStarted = true
    }

    override fun onStopped() {
        isStarted = false
    }

    override fun onDestroyed() {
        this.currentActivity = null
    }
}