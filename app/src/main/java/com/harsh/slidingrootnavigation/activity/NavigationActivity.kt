package com.harsh.slidingrootnavigation.activity

import android.content.Context
import android.graphics.Color
import android.graphics.Outline
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.NestedScrollView
import com.google.android.material.appbar.MaterialToolbar
import com.harsh.slidingrootnavigation.R
import com.harsh.slidingrootnavigation.SlidingRootNav
import com.harsh.slidingrootnavigation.SlidingRootNavBuilder
import com.harsh.slidingrootnavigation.callback.DragStateListener

class NavigationActivity : AppCompatActivity() {

    private var slidingRootNav: SlidingRootNav? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_navigation)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupNavigationDrawer(savedInstanceState)
        setupNavigationItemClick()
    }

    private fun setupNavigationDrawer(savedInstanceState: Bundle?) {
        slidingRootNav = SlidingRootNavBuilder(this)
            .withDragDistance(200)
            .withRootViewScale(0.63f)
            .withRootViewElevation(0)
            .withToolbarMenuToggle(findViewById(R.id.mainToolbar))
            .withMenuOpened(false)
            .withContentClickableWhenMenuOpened(false)
            .withSavedState(savedInstanceState)
            .withMenuLayout(R.layout.item_nav_drawer_menu)
            .inject()
        findViewById<MaterialToolbar>(R.id.mainToolbar).navigationIcon = ContextCompat.getDrawable(this@NavigationActivity, R.drawable.ic_menu)

        val slidingRootNavLayout = slidingRootNav?.getLayout()

        val rootView = slidingRootNavLayout?.getChildAt(2) ?: return
        rootView.outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                outline.setRoundRect(0, 0, view.width, view.height, dpToPx(32f))
            }
        }
        val strokeDrawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = dpToPx(32f)
            setStroke(dpToPx(1f).toInt(), Color.parseColor("#E6E6E6"))
            setColor(Color.TRANSPARENT)
        }
        slidingRootNavLayout.addDragStateListener(object : DragStateListener {
            override fun onDragStart() {}
            override fun onDragEnd(isMenuOpened: Boolean) {
                if (isMenuOpened) {
                    rootView.clipToOutline = true
                    rootView.foreground = strokeDrawable
                } else {
                    rootView.clipToOutline = false
                    rootView.foreground = null
                }
            }
        })
    }

    private fun setupNavigationItemClick() {
        slidingRootNav?.getLayout().apply {
            ViewCompat.setOnApplyWindowInsetsListener(findViewById<NestedScrollView>(R.id.nestedViewDrawer)) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }

            findViewById<LinearLayout>(R.id.searchNavHolder)?.setOnClickListener {
                slidingRootNav?.closeMenu()
            }
            findViewById<LinearLayout>(R.id.homeNavHolder)?.setOnClickListener {
                slidingRootNav?.closeMenu()
            }
            findViewById<LinearLayout>(R.id.favoriteNavHolder)?.setOnClickListener {
                slidingRootNav?.closeMenu()
            }
        }
    }
}

fun Context.dpToPx(dp: Float) = dp * resources.displayMetrics.density