package com.harsh.navigation.callback

interface DragStateListener {
    fun onDragStart()
    fun onDragEnd(isMenuOpened: Boolean)
}