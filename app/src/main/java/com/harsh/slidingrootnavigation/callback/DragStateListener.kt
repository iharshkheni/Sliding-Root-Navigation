package com.harsh.slidingrootnavigation.callback

interface DragStateListener {
    fun onDragStart()
    fun onDragEnd(isMenuOpened: Boolean)
}