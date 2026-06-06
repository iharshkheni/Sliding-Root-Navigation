package com.harsh.slidingrootnavigation

interface SlidingRootNav {
    fun isMenuClosed(): Boolean
    fun isMenuOpened(): Boolean
    fun isMenuLocked(): Boolean
    fun closeMenu()
    fun closeMenu(animated: Boolean)
    fun openMenu()
    fun openMenu(animated: Boolean)
    fun setMenuLocked(locked: Boolean)
    fun getLayout(): SlidingRootNavLayout?
}