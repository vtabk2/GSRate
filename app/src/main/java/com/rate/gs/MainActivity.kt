package com.rate.gs

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.core.rate.RateInApp

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RateInApp.instance.registerForFeedback(this)
        RateInApp.instance.showDialogRateAndFeedback(context = this)
    }
}