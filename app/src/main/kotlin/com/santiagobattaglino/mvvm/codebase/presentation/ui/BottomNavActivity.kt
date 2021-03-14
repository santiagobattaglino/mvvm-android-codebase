package com.santiagobattaglino.mvvm.codebase.presentation.ui

import com.santiagobattaglino.mvvm.codebase.presentation.ui.base.BaseActivity
import com.santiagobattaglino.mvvm.codebase.presentation.ui.incidents.detail.IncidentDetailActivity
import com.santiagobattaglino.mvvm.codebase.presentation.viewmodel.IncidentsViewModel
import com.santiagobattaglino.mvvm.codebase.presentation.viewmodel.NotificationsViewModel
import com.santiagobattaglino.mvvm.codebase.util.Arguments
import com.santiagobattaglino.mvvm.codebase.util.setupWithNavController
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.updatePadding
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.santiagobattaglino.mvvm.codebase.R
import kotlinx.android.synthetic.main.activity_bottom_nav.*
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.longToast
import org.koin.androidx.viewmodel.ext.android.viewModel

class BottomNavActivity : BaseActivity() {

    private val tag = javaClass.simpleName
    private val incidentsViewModel: IncidentsViewModel by viewModel()
    private val notificationsViewModel: NotificationsViewModel by viewModel()

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.extras?.getInt(Arguments.ARG_INCIDENT_ID)?.let {
            incidentsViewModel.getIncidentById(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bottom_nav)

        intent?.extras?.getInt(Arguments.ARG_INCIDENT_ID)?.let {
            incidentsViewModel.getIncidentById(it)
        }

        if (savedInstanceState == null) {
            setupNavigation()
            setupInsets()
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        setupNavigation()
    }

    override fun observe() {
        observeIncident()
    }

    private fun observeIncident() {
        incidentsViewModel.incidentUiData.observe(this, {
            it.incident?.let { incident ->
                incident.videoStream?.let { _ ->

                } ?: run {
                    startActivity(intentFor<IncidentDetailActivity>(Arguments.ARG_INCIDENT_ID to incident.id))
                }
            }

            it.error?.let { error ->
                if (error.isNoContent()) {
                    longToast(R.string.incident_not_exists)
                    Log.d(tag, "deleting notification ${error.msg}")
                    notificationsViewModel.deleteNotificationByIncidentId(error.msg)
                    error.msg?.let { incidentId ->
                        Log.d(tag, "deleting incident ${incidentId.toInt()}")
                        incidentsViewModel.deleteIncident(incidentId.toInt())
                    }
                }
            }
        })
    }

    private fun setupNavigation() {
        bottom_navigation.setupWithNavController(
            navGraphIds = listOf(
                R.navigation.nav_products,
                //R.navigation.nav_stock,
                //R.navigation.nav_incidents,
                R.navigation.nav_trending,
                R.navigation.nav_add_incident,
                R.navigation.nav_notifications,
                R.navigation.nav_profile
            ),
            fragmentManager = supportFragmentManager,
            containerId = R.id.nav_host_fragment,
            intent = intent
        )

        bottom_navigation.itemIconTintList = null

        val menuView = bottom_navigation.getChildAt(0) as BottomNavigationMenuView
        val iconView = menuView.getChildAt(2).findViewById<View>(R.id.icon)
        val layoutParams = iconView.layoutParams
        val displayMetrics = resources.displayMetrics
        layoutParams.height =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40f, displayMetrics).toInt()
        layoutParams.width =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40f, displayMetrics).toInt()
        iconView.layoutParams = layoutParams
    }

    private fun setupInsets() {
        bottom_activity_container.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION

        ViewCompat.setOnApplyWindowInsetsListener(bottom_activity_container) { _, insets ->
            bottom_activity_container.updatePadding(top = -insets.stableInsetTop)
            insets
        }
    }
}