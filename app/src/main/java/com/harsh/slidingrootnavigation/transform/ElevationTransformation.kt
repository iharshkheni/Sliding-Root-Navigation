package com.harsh.slidingrootnavigation.transform

import android.view.View
import com.harsh.slidingrootnavigation.util.SideNavUtils

class ElevationTransformation(private val endElevation: Float) : RootTransformation {
    override fun transform(dragProgress: Float, rootView: View?) {
        val elevation = SideNavUtils.evaluate(dragProgress, START_ELEVATION, endElevation)
        rootView?.elevation = elevation
    }
    companion object {
        private const val START_ELEVATION = 0f
    }
}