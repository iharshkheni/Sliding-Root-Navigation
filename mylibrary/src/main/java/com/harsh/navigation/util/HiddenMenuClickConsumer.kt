package com.harsh.navigation.util

import android.annotation.SuppressLint
import android.content.Context
import android.view.MotionEvent
import android.view.View
import com.harsh.navigation.SlidingRootNavLayout

class HiddenMenuClickConsumer(context: Context) : View(context) {
    private var menuHost: SlidingRootNavLayout? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        // Safe check in case menuHost is not set yet
        return menuHost?.isMenuClosed() ?: false
    }

    fun setMenuHost(layout: SlidingRootNavLayout) {
        this.menuHost = layout
    }
}