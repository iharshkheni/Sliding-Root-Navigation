package com.harsh.navigation.util

abstract class SideNavUtils {
    companion object {
        @JvmStatic
        fun evaluate(fraction: Float, startValue: Float, endValue: Float): Float {
            return startValue + fraction * (endValue - startValue)
        }
    }
}