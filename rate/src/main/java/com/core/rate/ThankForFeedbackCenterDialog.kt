package com.core.rate

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import com.core.rate.databinding.FbDialogThankFeedbackBinding
import com.core.rate.utils.hideNavigationBar

class ThankForFeedbackCenterDialog(context: Context) : AlertDialog(context) {
    private var mViewBinding = FbDialogThankFeedbackBinding.inflate(LayoutInflater.from(context))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mViewBinding.root)
        mViewBinding.apply {
            val showHeader = context.resources.getBoolean(R.bool.fb_show_header_thank_feedback)
            imageHeader.visibility = if (showHeader) View.VISIBLE else View.GONE

            tvOk.setOnClickListener {
                dismiss()
            }
        }

        val window = window
        val wlp = window?.attributes
        wlp?.let {
            wlp.width = (context.resources.displayMetrics.widthPixels * 0.9f).toInt()
            wlp.height = WindowManager.LayoutParams.WRAP_CONTENT
            window.attributes = wlp
            window.setBackgroundDrawableResource(android.R.color.transparent)
        }

        if (RateInApp.instance.isHideNavigationBar) {
            hideNavigationBar()
        }
    }
}