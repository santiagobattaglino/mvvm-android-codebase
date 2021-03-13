package com.santiagobattaglino.mvvm.codebase.util

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.Window
import android.view.WindowManager

object WindowUtil {
    fun hideWindowStatusBar(window: Window) {
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.TRANSPARENT
    }

    fun getSystemStatusBarHeight(context: Context): Int {
        val id = context.resources.getIdentifier(
            "status_bar_height", "dimen", "android"
        )
        return if (id > 0) context.resources.getDimensionPixelSize(id) else id
    }
}