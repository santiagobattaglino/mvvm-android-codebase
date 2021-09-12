package com.santiagobattaglino.mvvm.codebase.presentation.ui

import android.os.Bundle
import android.util.TypedValue
import android.view.ViewGroup
import androidx.core.view.get
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.santiagobattaglino.mvvm.codebase.R
import com.santiagobattaglino.mvvm.codebase.databinding.ActivityBottomNavBinding
import com.santiagobattaglino.mvvm.codebase.presentation.ui.base.BaseActivity
import com.santiagobattaglino.mvvm.codebase.util.setupWithNavController

class BottomNavActivity : BaseActivity() {

    private val tag = javaClass.simpleName
    private lateinit var binding: ActivityBottomNavBinding
    //private val viewModel: BottomNavViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBottomNavBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
        /*incidentsViewModel.incidentUiData.observe(this, {
            it.incident?.let { incident ->
                incident.videoStream?.let { _ ->

                } ?: run {
                    //startActivity(intentFor<IncidentDetailActivity>(Arguments.ARG_INCIDENT_ID to incident.id))
                }
            }

            it.error?.let { error ->
                if (error.isNoContent()) {
                    //longToast(R.string.incident_not_exists)
                    Log.d(tag, "deleting notification ${error.msg}")
                    notificationsViewModel.deleteNotificationByIncidentId(error.msg)
                    error.msg?.let { incidentId ->
                        Log.d(tag, "deleting incident ${incidentId.toInt()}")
                        incidentsViewModel.deleteIncident(incidentId.toInt())
                    }
                }
            }
        })*/
    }

    private fun setupNavigation() {
        binding.bottomNavigation.setupWithNavController(
            navGraphIds = listOf(
                R.navigation.nav_trending,
                R.navigation.nav_trending,
                R.navigation.nav_trending,
                R.navigation.nav_trending,
                R.navigation.nav_trending
            ),
            fragmentManager = supportFragmentManager,
            containerId = R.id.nav_host_fragment,
            intent = intent
        )

        binding.bottomNavigation.itemIconTintList = null

        val menuView = binding.bottomNavigation.getChildAt(0) as BottomNavigationMenuView
        val iconView = (menuView.getChildAt(2) as ViewGroup)[0]
        val layoutParams = iconView.layoutParams
        val displayMetrics = resources.displayMetrics
        layoutParams.height =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40f, displayMetrics).toInt()
        layoutParams.width =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40f, displayMetrics).toInt()
        iconView.layoutParams = layoutParams
    }

    private fun setupInsets() {
        /*bottom_activity_container.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION

        ViewCompat.setOnApplyWindowInsetsListener(bottom_activity_container) { _, insets ->
            bottom_activity_container.updatePadding(top = -insets.stableInsetTop)
            insets
        }*/
    }
}