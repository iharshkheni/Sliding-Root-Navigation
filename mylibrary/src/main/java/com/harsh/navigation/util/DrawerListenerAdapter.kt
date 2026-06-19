package com.harsh.navigation.util

import android.view.View
import androidx.drawerlayout.widget.DrawerLayout
import com.harsh.navigation.callback.DragListener
import com.harsh.navigation.callback.DragStateListener

class DrawerListenerAdapter(private val adaptee: DrawerLayout.DrawerListener, private val drawer: View): DragListener,
    DragStateListener {

    override fun onDrag(progress: Float) {
        adaptee.onDrawerSlide(drawer, progress)
    }

    override fun onDragStart() {
        adaptee.onDrawerStateChanged(DrawerLayout.STATE_DRAGGING)
    }

    override fun onDragEnd(isMenuOpened: Boolean) {
        if (isMenuOpened) {
            adaptee.onDrawerOpened(drawer)
        } else {
            adaptee.onDrawerClosed(drawer)
        }
        adaptee.onDrawerStateChanged(DrawerLayout.STATE_IDLE)
    }
}