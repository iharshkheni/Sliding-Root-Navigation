package com.harsh.slidingrootnavigation.util

abstract class SideNavUtils {
    companion object {
        @JvmStatic
        fun evaluate(fraction: Float, startValue: Float, endValue: Float): Float {
            return startValue + fraction * (endValue - startValue)
        }
    }
}