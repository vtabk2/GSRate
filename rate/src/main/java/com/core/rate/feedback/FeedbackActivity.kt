package com.core.rate.feedback

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.widget.addTextChangedListener
import com.core.rate.R
import com.core.rate.RateInApp
import com.core.rate.databinding.FbActivityFeedbackBinding
import com.core.rate.hideNavigationBar
import com.core.rate.setOnClickDontDoubleClick

class FeedbackActivity: AppCompatActivity() {

    private lateinit var _viewBinding: FbActivityFeedbackBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _viewBinding = FbActivityFeedbackBinding.inflate(layoutInflater)
        setContentView(_viewBinding.root)
        initView()

        if (RateInApp.instance.isHideNavigationBar) {
            hideNavigationBar()
        }

        enableEdgeToEdge()

        if (RateInApp.instance.isHideStatusBar) {
            hideSystemBars(window)
        }

        ViewCompat.setOnApplyWindowInsetsListener(_viewBinding.root) { view, windowInsets ->
            // Lấy thông tin về kích thước của các thanh hệ thống
            val systemBarInsets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            val cutoutInsets = windowInsets.getInsets(WindowInsetsCompat.Type.displayCutout())

            // Áp dụng padding cho view để nó không bị che
            // Ở đây, ta thêm padding ở trên cùng và dưới cùng của layout
            val topPadding = if (RateInApp.instance.isSpaceDisplayCutout && cutoutInsets.top > 0) {
                cutoutInsets.top
            } else if (RateInApp.instance.isSpaceStatusBar) {
                systemBarInsets.top
            } else 0

            val bottomPadding = if (RateInApp.instance.isHideNavigationBar) 0 else systemBarInsets.bottom
            view.setPadding(systemBarInsets.left, topPadding, systemBarInsets.right, bottomPadding)

            // Trả về windowInsets để các view con có thể tiếp tục xử lý
            windowInsets // Hoặc windowInsets nếu muốn các view con tiếp tục nhận
        }
    }

    private fun displayCutout(): Int {
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

    private fun checkWindowReady(rootView: View, onReady: () -> Unit) {
        ViewCompat.setOnApplyWindowInsetsListener(rootView) { view, windowInsets ->
            onReady()
            windowInsets
        }
    }

    private fun initView() {
        checkWindowReady(_viewBinding.root) {
            _viewBinding.actionBar.setPadding(0, displayCutout(), 0, 0)
        }

        _viewBinding.apply {
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
                it.setOnClickDontDoubleClick { _ ->
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

            nlRoot.onBackKeyboardCallback = {
                if (RateInApp.instance.isHideNavigationBar) {
                    hideNavigationBar()
                }
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (RateInApp.instance.isHideNavigationBar) {
            hideNavigationBar()
        }
    }

    private fun hideSystemBars(window: android.view.Window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val insetsController = window.insetsController ?: return
            insetsController.hide(WindowInsets.Type.statusBars())
            insetsController.systemBarsBehavior =
                WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        } else {
            val insetsController = WindowInsetsControllerCompat(
                window,
                window.decorView
            )
            insetsController.hide(WindowInsetsCompat.Type.statusBars())
            insetsController.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }
}

