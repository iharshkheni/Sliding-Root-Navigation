package com.harsh.navigation

import androidx.customview.widget.ViewDragHelper
import kotlin.math.abs

enum class SlideGravity {

    LEFT {
        override fun createHelper(): Helper = LeftHelper()
    },
    RIGHT {
        override fun createHelper(): Helper = RightHelper()
    };

    abstract fun createHelper(): Helper

    interface Helper {
        fun getLeftAfterFling(flingVelocity: Float, maxDrag: Int): Int
        fun getLeftToSettle(dragProgress: Float, maxDrag: Int): Int
        fun getRootLeft(dragProgress: Float, maxDrag: Int): Int
        fun getDragProgress(viewLeft: Int, maxDrag: Int): Float
        fun clampViewPosition(left: Int, maxDrag: Int): Int
        fun enableEdgeTrackingOn(dragHelper: ViewDragHelper)
    }

    class LeftHelper : Helper {
        override fun getLeftAfterFling(flingVelocity: Float, maxDrag: Int): Int {
            return if (flingVelocity > 0) maxDrag else 0
        }
        override fun getLeftToSettle(dragProgress: Float, maxDrag: Int): Int {
            return if (dragProgress > 0.5f) maxDrag else 0
        }
        override fun getRootLeft(dragProgress: Float, maxDrag: Int): Int {
            return (dragProgress * maxDrag).toInt()
        }
        override fun getDragProgress(viewLeft: Int, maxDrag: Int): Float {
            return viewLeft.toFloat() / maxDrag
        }
        override fun clampViewPosition(left: Int, maxDrag: Int): Int {
            return left.coerceIn(0, maxDrag)
        }
        override fun enableEdgeTrackingOn(dragHelper: ViewDragHelper) {
            dragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_LEFT)
        }
    }

    class RightHelper : Helper {
        override fun getLeftAfterFling(flingVelocity: Float, maxDrag: Int): Int {
            return if (flingVelocity > 0) 0 else -maxDrag
        }
        override fun getLeftToSettle(dragProgress: Float, maxDrag: Int): Int {
            return if (dragProgress > 0.5f) -maxDrag else 0
        }
        override fun getRootLeft(dragProgress: Float, maxDrag: Int): Int {
            return -(dragProgress * maxDrag).toInt()
        }
        override fun getDragProgress(viewLeft: Int, maxDrag: Int): Float {
            return abs(viewLeft).toFloat() / maxDrag
        }
        override fun clampViewPosition(left: Int, maxDrag: Int): Int {
            return left.coerceIn(-maxDrag, 0)
        }
        override fun enableEdgeTrackingOn(dragHelper: ViewDragHelper) {
            dragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_RIGHT)
        }
    }
}