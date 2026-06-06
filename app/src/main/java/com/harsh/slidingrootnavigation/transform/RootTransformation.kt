package com.harsh.slidingrootnavigation.transform

import android.view.View

interface RootTransformation {
    fun transform(dragProgress: Float, rootView: View?)
}