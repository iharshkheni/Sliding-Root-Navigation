package com.harsh.navigation.util

import android.content.Context
import androidx.drawerlayout.widget.DrawerLayout
import com.harsh.navigation.SlidingRootNavLayout

class ActionBarToggleAdapter(context: Context) : DrawerLayout(context) {
    private var adaptee: SlidingRootNavLayout? = null

    override fun openDrawer(gravity: Int) {
        adaptee?.openMenu()
    }

    override fun closeDrawer(gravity: Int) {
        adaptee?.closeMenu()
    }

    override fun isDrawerVisible(drawerGravity: Int): Boolean {
        // Returns true if the menu is NOT closed (i.e., open/visible)
        return adaptee?.isMenuClosed() == false
    }

    override fun getDrawerLockMode(edgeGravity: Int): Int {
        val currentAdaptee = adaptee ?: return LOCK_MODE_UNLOCKED
        return when {
            currentAdaptee.isMenuLocked() && currentAdaptee.isMenuClosed() -> LOCK_MODE_LOCKED_CLOSED
            currentAdaptee.isMenuLocked() && !currentAdaptee.isMenuClosed() -> LOCK_MODE_LOCKED_OPEN
            else -> LOCK_MODE_UNLOCKED
        }
    }

    fun setAdaptee(adaptee: SlidingRootNavLayout) {
        this.adaptee = adaptee
    }
}