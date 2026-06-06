package com.harsh.slidingrootnavigation.transform

import android.view.View
import com.harsh.slidingrootnavigation.util.SideNavUtils

class YTranslationTransformation(private val endTranslation: Float) : RootTransformation {
    override fun transform(dragProgress: Float, rootView: View?) {
        val translation = SideNavUtils.evaluate(dragProgress, START_TRANSLATION, endTranslation)
        rootView?.translationY = translation
    }
    companion object {
        private const val START_TRANSLATION = 0f
    }
}