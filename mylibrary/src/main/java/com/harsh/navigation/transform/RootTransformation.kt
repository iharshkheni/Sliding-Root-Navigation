package com.harsh.navigation.transform

import android.view.View

interface RootTransformation {
    fun transform(dragProgress: Float, rootView: View?)
}