package com.rate.gs

import android.app.Application
import com.core.rate.RateInApp

class MyApp: Application() {
    override fun onCreate() {
        super.onCreate()
        RateInApp.instance.registerActivityLifecycle(this)
        RateInApp.instance.isRateGravityBottom = true

        RateInApp.instance.isHideNavigationBar = true
        RateInApp.instance.isHideStatusBar = true
        RateInApp.instance.isSpaceStatusBar = false
        RateInApp.instance.isSpaceDisplayCutout = true
    }
}