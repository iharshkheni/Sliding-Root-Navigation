package com.harsh.slidingrootnavigation

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.widget.FrameLayout
import androidx.core.view.ViewCompat
import androidx.customview.widget.ViewDragHelper
import com.harsh.slidingrootnavigation.callback.DragListener
import com.harsh.slidingrootnavigation.callback.DragStateListener
import com.harsh.slidingrootnavigation.transform.RootTransformation
import kotlin.math.abs

class SlidingRootNavLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr), SlidingRootNav {
    private val flingMinVelocity: Float = ViewConfiguration.get(context).scaledMinimumFlingVelocity.toFloat()
    private var isMenuLockedVal = false
    private var isMenuHidden = true
    private var isContentClickableWhenMenuOpened = false
    private var rootTransformation: RootTransformation? = null
    private var rootView: View? = null
    var dragProgress: Float = 0f
        private set
    private var maxDragDistance = 0
    private var dragState = 0
    private val dragHelper: ViewDragHelper = ViewDragHelper.create(this, ViewDragCallback())
    private var positionHelper: SlideGravity.Helper? = null
    private val dragListeners = ArrayList<DragListener>()
    private val dragStateListeners = ArrayList<DragStateListener>()

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return (!isMenuLockedVal && dragHelper.shouldInterceptTouchEvent(ev)) || shouldBlockClick(ev)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        dragHelper.processTouchEvent(event)
        return true
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child == rootView) {
                val rootLeft = positionHelper?.getRootLeft(dragProgress, maxDragDistance) ?: 0
                child.layout(rootLeft, top, rootLeft + (right - left), bottom)
            } else {
                child.layout(left, top, right, bottom)
            }
        }
    }

    override fun computeScroll() {
        if (dragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this)
        }
    }

    private fun changeMenuVisibility(animated: Boolean, newDragProgress: Float) {
        isMenuHidden = calculateIsMenuHidden()
        if (animated) {
            val left = positionHelper?.getLeftToSettle(newDragProgress, maxDragDistance) ?: 0
            val root = rootView
            if (root != null && dragHelper.smoothSlideViewTo(root, left, root.top)) {
                ViewCompat.postInvalidateOnAnimation(this)
            }
        } else {
            dragProgress = newDragProgress
            rootView?.let { rootTransformation?.transform(dragProgress, it) }
            requestLayout()
        }
    }

    override fun isMenuClosed(): Boolean {
        return isMenuHidden
    }

    override fun isMenuOpened(): Boolean {
        return !isMenuHidden
    }

    override fun getLayout(): SlidingRootNavLayout {
        return this
    }

    override fun isMenuLocked(): Boolean {
        return isMenuLockedVal
    }

    override fun closeMenu() {
        closeMenu(true)
    }

    override fun closeMenu(animated: Boolean) {
        changeMenuVisibility(animated, 0f)
    }

    override fun openMenu() {
        openMenu(true)
    }

    override fun openMenu(animated: Boolean) {
        changeMenuVisibility(animated, 1f)
    }

    override fun setMenuLocked(locked: Boolean) {
        isMenuLockedVal = locked
    }

    fun setRootView(view: View) {
        rootView = view
    }

    fun setContentClickableWhenMenuOpened(contentClickableWhenMenuOpened: Boolean) {
        isContentClickableWhenMenuOpened = contentClickableWhenMenuOpened
    }

    fun setRootTransformation(transformation: RootTransformation) {
        rootTransformation = transformation
    }

    fun setMaxDragDistance(maxDragDistance: Int) {
        this.maxDragDistance = maxDragDistance
    }

    fun setGravity(gravity: SlideGravity) {
        val helper = gravity.createHelper()
        positionHelper = helper
        helper.enableEdgeTrackingOn(dragHelper)
    }

    fun addDragListener(listener: DragListener) {
        dragListeners.add(listener)
    }

    fun addDragStateListener(listener: DragStateListener) {
        dragStateListeners.add(listener)
    }

    fun removeDragListener(listener: DragListener) {
        dragListeners.remove(listener)
    }

    fun removeDragStateListener(listener: DragStateListener) {
        dragStateListeners.remove(listener)
    }

    private fun shouldBlockClick(event: MotionEvent): Boolean {
        if (isContentClickableWhenMenuOpened) {
            return false
        }
        val root = rootView
        if (root != null && isMenuOpened()) {
            root.getHitRect(tempRect)
            return tempRect.contains(event.x.toInt(), event.y.toInt())
        }
        return false
    }

    private fun notifyDrag() {
        for (listener in dragListeners) {
            listener.onDrag(dragProgress)
        }
    }

    private fun notifyDragStart() {
        for (listener in dragStateListeners) {
            listener.onDragStart()
        }
    }

    private fun notifyDragEnd(isOpened: Boolean) {
        for (listener in dragStateListeners) {
            listener.onDragEnd(isOpened)
        }
    }

    override fun onSaveInstanceState(): Parcelable {
        val savedState = Bundle()
        savedState.putParcelable(EXTRA_SUPER, super.onSaveInstanceState())
        savedState.putInt(EXTRA_IS_OPENED, if (dragProgress > 0.5) 1 else 0)
        savedState.putBoolean(EXTRA_SHOULD_BLOCK_CLICK, isContentClickableWhenMenuOpened)
        return savedState
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is Bundle) {
            super.onRestoreInstanceState(state.getParcelable(EXTRA_SUPER))
            changeMenuVisibility(false, state.getInt(EXTRA_IS_OPENED, 0).toFloat())
            isMenuHidden = calculateIsMenuHidden()
            isContentClickableWhenMenuOpened = state.getBoolean(EXTRA_SHOULD_BLOCK_CLICK)
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    private fun calculateIsMenuHidden(): Boolean {
        return dragProgress == 0f
    }

    private inner class ViewDragCallback : ViewDragHelper.Callback() {
        private var edgeTouched = false

        override fun tryCaptureView(child: View, pointerId: Int): Boolean {
            if (isMenuLockedVal) {
                return false
            }
            val isOnEdge = edgeTouched
            edgeTouched = false
            return if (isMenuClosed()) {
                child == rootView && isOnEdge
            } else {
                if (child != rootView) {
                    rootView?.let { dragHelper.captureChildView(it, pointerId) }
                    false
                } else {
                    true
                }
            }
        }

        override fun onViewPositionChanged(changedView: View, left: Int, top: Int, dx: Int, dy: Int) {
            dragProgress = positionHelper?.getDragProgress(left, maxDragDistance) ?: 0f
            rootView?.let { rootTransformation?.transform(dragProgress, it) }
            notifyDrag()
            invalidate()
        }

        override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
            val left = if (abs(xvel) < flingMinVelocity) {
                positionHelper?.getLeftToSettle(dragProgress, maxDragDistance) ?: 0
            } else {
                positionHelper?.getLeftAfterFling(xvel, maxDragDistance) ?: 0
            }
            rootView?.let { dragHelper.settleCapturedViewAt(left, it.top) }
            invalidate()
        }

        override fun onViewDragStateChanged(state: Int) {
            if (dragState == ViewDragHelper.STATE_IDLE && state != ViewDragHelper.STATE_IDLE) {
                notifyDragStart()
            } else if (dragState != ViewDragHelper.STATE_IDLE && state == ViewDragHelper.STATE_IDLE) {
                isMenuHidden = calculateIsMenuHidden()
                notifyDragEnd(isMenuOpened())
            }
            dragState = state
        }

        override fun onEdgeTouched(edgeFlags: Int, pointerId: Int) {
            edgeTouched = true
        }

        override fun getViewHorizontalDragRange(child: View): Int {
            return if (child == rootView) maxDragDistance else 0
        }

        override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int {
            return positionHelper?.clampViewPosition(left, maxDragDistance) ?: 0
        }
    }

    companion object {
        private const val EXTRA_IS_OPENED = "extra_is_opened"
        private const val EXTRA_SUPER = "extra_super"
        private const val EXTRA_SHOULD_BLOCK_CLICK = "extra_should_block_click"
        private val tempRect = Rect()
    }
}