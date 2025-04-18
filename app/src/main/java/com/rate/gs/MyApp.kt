package com.rate.gs

import android.app.Application
import com.core.rate.RateInApp

class MyApp: Application() {
    override fun onCreate() {
        super.onCreate()
        RateInApp.instance.registerActivityLifecycle(this)
        RateInApp.instance.isThankForFeedbackGravityBottom = false
    }
}