package com.harsh.slidingrootnavigation

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.FloatRange
import androidx.annotation.IntRange
import androidx.annotation.LayoutRes
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import com.harsh.slidingrootnavigation.callback.DragListener
import com.harsh.slidingrootnavigation.callback.DragStateListener
import com.harsh.slidingrootnavigation.transform.CompositeTransformation
import com.harsh.slidingrootnavigation.transform.ElevationTransformation
import com.harsh.slidingrootnavigation.transform.RootTransformation
import com.harsh.slidingrootnavigation.transform.ScaleTransformation
import com.harsh.slidingrootnavigation.transform.YTranslationTransformation
import com.harsh.slidingrootnavigation.util.ActionBarToggleAdapter
import com.harsh.slidingrootnavigation.util.DrawerListenerAdapter
import com.harsh.slidingrootnavigation.util.HiddenMenuClickConsumer

class SlidingRootNavBuilder(private val activity: Activity) {

    private var contentView: ViewGroup? = null
    private var menuView: View? = null
    @LayoutRes
    private var menuLayoutRes: Int = 0
    private val transformations = ArrayList<RootTransformation>()
    private val dragListeners = ArrayList<DragListener>()
    private val dragStateListeners = ArrayList<DragStateListener>()
    private var dragDistance: Int = dpToPx(DEFAULT_DRAG_DIST_DP)
    private var toolbar: Toolbar? = null
    private var gravity: SlideGravity = SlideGravity.LEFT
    private var isMenuOpened: Boolean = false
    private var isMenuLocked: Boolean = false
    private var isContentClickableWhenMenuOpened: Boolean = true
    private var savedState: Bundle? = null

    fun withMenuView(view: View): SlidingRootNavBuilder {
        menuView = view
        return this
    }

    fun withMenuLayout(@LayoutRes layout: Int): SlidingRootNavBuilder {
        menuLayoutRes = layout
        return this
    }

    fun withToolbarMenuToggle(tb: Toolbar): SlidingRootNavBuilder {
        toolbar = tb
        return this
    }

    fun withGravity(g: SlideGravity): SlidingRootNavBuilder {
        gravity = g
        return this
    }

    fun withContentView(cv: ViewGroup): SlidingRootNavBuilder {
        contentView = cv
        return this
    }

    fun withMenuLocked(locked: Boolean): SlidingRootNavBuilder {
        isMenuLocked = locked
        return this
    }

    fun withSavedState(state: Bundle?): SlidingRootNavBuilder {
        savedState = state
        return this
    }

    fun withMenuOpened(opened: Boolean): SlidingRootNavBuilder {
        isMenuOpened = opened
        return this
    }

    fun withContentClickableWhenMenuOpened(clickable: Boolean): SlidingRootNavBuilder {
        isContentClickableWhenMenuOpened = clickable
        return this
    }

    fun withDragDistance(dp: Int): SlidingRootNavBuilder {
        return withDragDistancePx(dpToPx(dp))
    }

    fun withDragDistancePx(px: Int): SlidingRootNavBuilder {
        dragDistance = px
        return this
    }

    fun withRootViewScale(@FloatRange(from = 0.01) scale: Float): SlidingRootNavBuilder {
        transformations.add(ScaleTransformation(scale))
        return this
    }

    fun withRootViewElevation(@IntRange(from = 0) elevation: Int): SlidingRootNavBuilder {
        return withRootViewElevationPx(dpToPx(elevation))
    }

    fun withRootViewElevationPx(@IntRange(from = 0) elevation: Int): SlidingRootNavBuilder {
        transformations.add(ElevationTransformation(elevation.toFloat()))
        return this
    }

    fun withRootViewYTranslation(translation: Int): SlidingRootNavBuilder {
        return withRootViewYTranslationPx(dpToPx(translation))
    }

    fun withRootViewYTranslationPx(translation: Int): SlidingRootNavBuilder {
        transformations.add(YTranslationTransformation(translation.toFloat()))
        return this
    }

    fun addRootTransformation(transformation: RootTransformation): SlidingRootNavBuilder {
        transformations.add(transformation)
        return this
    }

    fun addDragListener(dragListener: DragListener): SlidingRootNavBuilder {
        dragListeners.add(dragListener)
        return this
    }

    fun addDragStateListener(dragStateListener: DragStateListener): SlidingRootNavBuilder {
        dragStateListeners.add(dragStateListener)
        return this
    }

    fun inject(): SlidingRootNav {
        val cv = getContentView()
        val oldRoot = cv.getChildAt(0)
        cv.removeAllViews()
        val newRoot = createAndInitNewRoot(oldRoot)
        val menu = getMenuViewFor(newRoot)
        initToolbarMenuVisibilityToggle(newRoot, menu)
        val clickConsumer = HiddenMenuClickConsumer(activity).apply {
            setMenuHost(newRoot)
        }
        newRoot.addView(menu)
        newRoot.addView(clickConsumer)
        newRoot.addView(oldRoot)
        cv.addView(newRoot)
        if (savedState == null) {
            if (isMenuOpened) {
                newRoot.openMenu(false)
            }
        }
        newRoot.setMenuLocked(isMenuLocked)
        return newRoot
    }

    private fun createAndInitNewRoot(oldRoot: View): SlidingRootNavLayout {
        val newRoot = SlidingRootNavLayout(activity).apply {
            id = R.id.srn_root_layout
            setRootTransformation(createCompositeTransformation())
            setMaxDragDistance(dragDistance)
            setGravity(gravity)
            setRootView(oldRoot)
            setContentClickableWhenMenuOpened(isContentClickableWhenMenuOpened)
        }
        for (l in dragListeners) {
            newRoot.addDragListener(l)
        }
        for (l in dragStateListeners) {
            newRoot.addDragStateListener(l)
        }
        return newRoot
    }

    private fun getContentView(): ViewGroup {
        var cv = contentView
        if (cv == null) {
            cv = activity.findViewById(android.R.id.content) as? ViewGroup
            contentView = cv
        }
        val currentCv = cv ?: throw IllegalStateException("Content view is null")
        if (currentCv.childCount != 1) {
            throw IllegalStateException(activity.getString(R.string.srn_ex_bad_content_view))
        }
        return currentCv
    }

    private fun getMenuViewFor(parent: SlidingRootNavLayout): View {
        var menu = menuView
        if (menu == null) {
            if (menuLayoutRes == 0) {
                throw IllegalStateException(activity.getString(R.string.srn_ex_no_menu_view))
            }
            menu = LayoutInflater.from(activity).inflate(menuLayoutRes, parent, false)
            menuView = menu
        }
        return menu ?: throw IllegalStateException("Menu view inflation failed")
    }

    private fun createCompositeTransformation(): RootTransformation {
        return if (transformations.isEmpty()) {
            CompositeTransformation(
                listOf(
                    ScaleTransformation(DEFAULT_END_SCALE),
                    ElevationTransformation(dpToPx(DEFAULT_END_ELEVATION_DP).toFloat())
                )
            )
        } else {
            CompositeTransformation(transformations)
        }
    }

    protected fun initToolbarMenuVisibilityToggle(sideNav: SlidingRootNavLayout, drawer: View) {
        val tb = toolbar
        if (tb != null) {
            val dlAdapter = ActionBarToggleAdapter(activity).apply {
                setAdaptee(sideNav)
            }
            val toggle = ActionBarDrawerToggle(
                activity, dlAdapter, tb,
                R.string.srn_drawer_open,
                R.string.srn_drawer_close
            )
            toggle.syncState()
            val listenerAdapter = DrawerListenerAdapter(toggle, drawer)
            sideNav.addDragListener(listenerAdapter)
            sideNav.addDragStateListener(listenerAdapter)
        }
    }

    private fun dpToPx(dp: Int): Int {
        return Math.round(activity.resources.displayMetrics.density * dp)
    }

    companion object {
        private const val DEFAULT_END_SCALE = 0.65f
        private const val DEFAULT_END_ELEVATION_DP = 8
        private const val DEFAULT_DRAG_DIST_DP = 180
    }
}