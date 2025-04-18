package com.core.rate

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.isVisible
import com.core.rate.databinding.FbDialogRateBottomBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class RateBottomDialog : BottomSheetDialogFragment() {
    private lateinit var mViewBinding: FbDialogRateBottomBinding

    var onRate: ((star: Int) -> Unit)? = null
    var oldImage: Int = R.drawable.fb_ic_smile_1
    var onIgnore: (() -> Unit)? = null
    private var isRated = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mViewBinding = FbDialogRateBottomBinding.inflate(layoutInflater, container, false)
        return mViewBinding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener { dialogInterface ->
            val bottomSheetDialog = dialogInterface as BottomSheetDialog
            val bottomSheet = bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.setBackgroundColor(Color.TRANSPARENT)
        }
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewBinding.apply {
            val listStar = listOf(
                ivStar1,
                ivStar2,
                ivStar3,
                ivStar4,
                ivStar5
            )
            listStar.forEachIndexed { index, imageView ->
                imageView.tag = index
                imageView.setOnClickDontDoubleClick(500) { view ->
                    listStar.forEach {
                        it.isSelected = it.tag as Int <= view.tag as Int
                    }
                    tvReview.isEnabled = true
                    val smileIcon = getSmileIcon(view.tag as Int)
                    if (ivSmile.isVisible) {
                        if (smileIcon != oldImage) {
                            animateIconChange(ivSmile, smileIcon)
                        }
                    } else {
                        ivSmile.visibility = View.VISIBLE
                        ivSmile.setImageResource(smileIcon)
                    }
                    tvTitle.setText(getTextTitle(view.tag as Int))
                    oldImage = smileIcon
                    tvReview.setText(getTextButton(view.tag as Int))
                }
            }

            tvReview.setOnClickListener {
                val star = listStar.last { it.isSelected }.tag as Int
                onRate?.invoke(star + 1)
                isRated = true
                dismiss()
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if (!isRated) {
            onIgnore?.invoke()
        }
    }

    private fun getSmileIcon(star: Int): Int {
        return when (star) {
            0 -> R.drawable.fb_ic_smile_1
            1 -> R.drawable.fb_ic_smile_2
            2 -> R.drawable.fb_ic_smile_3
            3 -> R.drawable.fb_ic_smile_4
            4 -> R.drawable.fb_ic_smile_5
            else -> R.drawable.fb_ic_smile_1
        }
    }

    private fun getTextTitle(star: Int): Int {
        return when (star) {
            0, 1, 2, 3 -> R.string.fb_rate_us_bad
            4 -> R.string.fb_rate_us_good
            else -> R.string.fb_rate_us_default
        }
    }

    private fun getTextButton(star: Int): Int {
        return when (star) {
            0, 1, 2, 3 -> R.string.fb_feedback_rate
            4 -> R.string.fb_rate_on_google_play
            else -> R.string.fb_feedback_rate
        }
    }

    private fun animateIconChange(imageView: ImageView, newIcon: Int) {
        val fadeOut = ObjectAnimator.ofFloat(imageView, "alpha", 1f, 0f)
        val fadeIn = ObjectAnimator.ofFloat(imageView, "alpha", 0f, 1f)

        fadeOut.duration = 200
        fadeIn.duration = 200

        fadeOut.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                imageView.setImageResource(newIcon)
                fadeIn.start()
            }
        })

        fadeOut.start()
    }
}