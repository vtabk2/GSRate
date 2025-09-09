package com.core.rate.feedback

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.KeyEvent
import androidx.appcompat.widget.LinearLayoutCompat

class NewLinearLayoutCompat @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : LinearLayoutCompat(context, attrs, defStyleAttr) {
    var onBackKeyboardCallback: (() -> Unit)? = null

    override fun dispatchKeyEventPreIme(event: KeyEvent?): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // nothing
        } else {
            event?.let {
                if (it.keyCode == KeyEvent.KEYCODE_BACK && it.action == KeyEvent.ACTION_UP) {
                    onBackKeyboardCallback?.invoke()
                }
            }
        }
        return super.dispatchKeyEventPreIme(event)
    }
}