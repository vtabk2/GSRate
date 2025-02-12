package com.core.rate.feedback

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.ViewCompat
import androidx.core.widget.addTextChangedListener
import com.core.rate.R
import com.core.rate.databinding.FbActivityFeedbackBinding
import com.core.rate.setOnClickDontDoubleClick

class FeedbackActivity: AppCompatActivity() {

    private lateinit var _viewBinding: FbActivityFeedbackBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _viewBinding = FbActivityFeedbackBinding.inflate(layoutInflater)
        setContentView(_viewBinding.root)
        makeStatusBarTransparent()
        initView()
    }

    private fun makeStatusBarTransparent() {
        window.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

            decorView.systemUiVisibility = if(true) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                } else {
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                }
            } else {
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            }
            statusBarColor = Color.TRANSPARENT
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
            window.insetsController?.show(WindowInsets.Type.navigationBars())
        }
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
    }

    fun displayCutout(): Int {
        var height = 0
        if (window != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val windowInsets = window.decorView.rootWindowInsets
                if (windowInsets != null) {
                    val displayCutout = windowInsets.displayCutout
                    if (displayCutout != null) {
                        height = displayCutout.safeInsetTop
                        // Use cutoutHeight as needed (e.g., adjust layout margins)
                    }
                }
            }
        }
        return height
    }

    fun checkWindowReady(rootView: View, onReady: () -> Unit) {
        ViewCompat.setOnApplyWindowInsetsListener(rootView) { view, windowInsets ->
            onReady()
            windowInsets
        }
    }

    fun initView() {
        checkWindowReady(_viewBinding.root) {
            _viewBinding.actionBar.setPadding(0, displayCutout(), 0, 0)
        }

        _viewBinding.run {
            ivBack.setOnClickListener {
                finish()
            }

            if(resources.getBoolean(R.bool.fb_button_submit_feedback_inside_input)) {
                val lp = ivSendFeedback.layoutParams as LinearLayoutCompat.LayoutParams
                lp.setMargins(lp.leftMargin, resources.getDimensionPixelSize(R.dimen.fb_top_margin_button_submit_feedback), lp.rightMargin, lp.bottomMargin)
                edtFeedback.setPadding(edtFeedback.paddingLeft, edtFeedback.paddingTop, edtFeedback.paddingRight, resources.getDimensionPixelSize(R.dimen.fb_bottom_padding_input))
            }

            val isFlexUI = resources.getBoolean(R.bool.fb_matter_feedback_flex_ui)
            matterFlexible.visibility = if(isFlexUI) View.VISIBLE else View.GONE
            matterLinear.visibility = if(!isFlexUI) View.VISIBLE else View.GONE

            val isType = resources.getBoolean(R.bool.fb_show_text_type_in_feedback)
            tvType.visibility = if(isType) View.VISIBLE else View.GONE


            val listOption = if(!isFlexUI) listOf(tvFeatureQuality, tvCrash, tvBug, tvOthers) else listOf(tvFeatureQualityFl, tvCrashFl, tvBugFl, tvOtherFl)
            listOption.forEach {
                it.setOnClickDontDoubleClick {
                    it.isSelected = !it.isSelected
                }
            }

            edtFeedback.addTextChangedListener(afterTextChanged = {
                it?.let {
                    ivSendFeedback.isEnabled = it.length >= 6
                }
            })

            ivSendFeedback.setOnClickListener {
                ShareUtils.feedbackFocusEmail(
                    this@FeedbackActivity, Feedback(
                        content = edtFeedback.text.toString(),
                        isFeatureQuality = tvFeatureQuality.isSelected || tvFeatureQualityFl.isSelected,
                        isCrash = tvCrash.isSelected || tvCrashFl.isSelected,
                        isBug = tvBug.isSelected || tvBugFl.isSelected,
                        isOthers = tvOthers.isSelected || tvOtherFl.isSelected
                    )
                )
                setResult(Activity.RESULT_OK)
                finish()
            }


        }
    }



}

