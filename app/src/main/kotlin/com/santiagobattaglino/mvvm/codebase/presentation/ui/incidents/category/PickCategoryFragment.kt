package com.santiagobattaglino.mvvm.codebase.presentation.ui.incidents.category

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.santiagobattaglino.mvvm.codebase.R
import com.santiagobattaglino.mvvm.codebase.data.repository.SharedPreferenceUtils
import com.santiagobattaglino.mvvm.codebase.domain.entity.Category
import com.santiagobattaglino.mvvm.codebase.domain.model.CheckPlace
import com.santiagobattaglino.mvvm.codebase.presentation.ui.base.BasePermissionFragment
import com.santiagobattaglino.mvvm.codebase.presentation.viewmodel.IncidentsViewModel
import com.santiagobattaglino.mvvm.codebase.util.Arguments
import com.santiagobattaglino.mvvm.codebase.util.isLocationEnabled
import kotlinx.android.synthetic.main.fragment_pick_category.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

class PickCategoryFragment : BasePermissionFragment(), CategoryAdapter.OnViewHolderClick {

    private val mTag = javaClass.simpleName

    private val incidentsViewModel: IncidentsViewModel by viewModel()
    private val sp: SharedPreferenceUtils by inject()
    private var categories = mutableListOf<Category>()
    private lateinit var adapter: CategoryAdapter
    private var selectedCategory: Category? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_pick_category, container, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        category_grid.adapter = null
    }

    override fun onPrimaryNavigationFragmentChanged(isPrimaryNavigationFragment: Boolean) {
        super.onPrimaryNavigationFragmentChanged(isPrimaryNavigationFragment)
        if (isPrimaryNavigationFragment) {
            if (hasLocationPermission()) {

            } else {
                context?.let {
                    EasyPermissions.requestPermissions(
                        this,
                        it.getString(R.string.rationale_location),
                        locationPermReq,
                        *locationPermission
                    )
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            locationPermReq -> {
                if (hasLocationPermission()) {

                } else {
                    EasyPermissions.onRequestPermissionsResult(
                        requestCode,
                        permissions,
                        grantResults, this
                    )
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            if (hasLocationPermission()) {

            } else {
                EasyPermissions.requestPermissions(
                    this,
                    getString(R.string.rationale_location),
                    locationPermReq,
                    *locationPermission
                )
            }
        }
    }

    override fun observe() {
        observeLocation()
        observeCheckPlace()
        observeCategories()
    }

    private fun observeLocation() {
        incidentsViewModel.locationUiData.observe(this, {
            it?.let {
                Log.d(
                    mTag,
                    "fusedLocationClient.lastLocation.addOnSuccessListener: $it"
                )
                sp.saveString(Arguments.ARG_LAST_KNOWN_LOCATION, "${it.latitude},${it.longitude}")

                incidentsViewModel.postCheckPlace(
                    CheckPlace(
                        null,
                        it.latitude,
                        it.longitude
                    )
                )
            }
        })
    }

    private fun observeCheckPlace() {
        incidentsViewModel.checkPlaceUiData.observe(this, {
            it.error?.let { error ->
                handleError(mTag, error)
            }

            it.checkPlace?.let { checkPlace ->
                checkPlace.isAllowed?.let { isAllowed ->
                    if (!isAllowed) {
                        findNavController().navigate(R.id.category_to_place_not_allowed)
                    } else {
                        incidentsViewModel.getCategories()
                        upload_an_incident.isVisible = true
                        start_live_streaming.isVisible = true
                    }
                }
            }
        })
    }

    private fun observeCategories() {
        incidentsViewModel.categoriesUiData.observe(this, {
            it.categories?.let { categories ->
                this.categories = categories.toMutableList()
                adapter.mData = categories
            }
        })
    }

    override fun setUpViews() {
        ViewCompat.setOnApplyWindowInsetsListener(pick_category_container) { _, insets ->
            pick_category_container.updatePadding(top = insets.stableInsetTop + 70)
            insets
        }

        disableActions()

        help.setOnClickListener {
            it.findNavController().navigate(R.id.category_to_tutorial)
        }

        upload_an_incident.setOnClickListener {
            if (isLocationEnabled(context)) {
                it.findNavController().navigate(
                    R.id.category_to_upload,
                    bundleOf(Arguments.ARG_CATEGORY to selectedCategory)
                )
            } else {
                //context?.toast(getString(R.string.location_disabled))
            }
        }

        start_live_streaming.setOnClickListener {
            it.findNavController().navigate(
                R.id.category_to_confirm,
                bundleOf(Arguments.ARG_CATEGORY to selectedCategory)
            )
        }

        setUpGrid()
    }

    private fun setUpGrid() {
        adapter = CategoryAdapter(this)
        category_grid.adapter = adapter
        category_grid.layoutManager = GridLayoutManager(
            context,
            4,
            RecyclerView.VERTICAL,
            false
        )
        category_grid.setHasFixedSize(false)
    }

    override fun dataViewClickFromList(view: View, position: Int, data: Category) {
        categories.forEach {
            it.isSelected = false
        }
        val selectedCategory = categories.find {
            it.id == data.id
        }
        selectedCategory?.let {
            this.selectedCategory = it
            it.isSelected = true
            enableActions()
            adapter.mData = categories.toList()
            adapter.notifyDataSetChanged()
        } ?: run {
            disableActions()
        }
    }

    private fun enableActions() {
        upload_an_incident.isEnabled = true
        upload_an_incident.setTextColor(
            ResourcesCompat.getColor(
                resources,
                R.color.colorHeaderText,
                null
            )
        )
        start_live_streaming.isEnabled = true
        start_live_streaming.setTextColor(
            ResourcesCompat.getColor(
                resources,
                R.color.colorWhite,
                null
            )
        )
    }

    private fun disableActions() {
        upload_an_incident.isEnabled = false
        upload_an_incident.setTextColor(
            ResourcesCompat.getColor(
                resources,
                R.color.incident_circle_stroke,
                null
            )
        )
        start_live_streaming.isEnabled = false
        start_live_streaming.setTextColor(
            ResourcesCompat.getColor(
                resources,
                R.color.colorDisabledText,
                null
            )
        )
    }
}