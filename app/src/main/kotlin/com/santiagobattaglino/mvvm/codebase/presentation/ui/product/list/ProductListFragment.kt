package com.santiagobattaglino.mvvm.codebase.presentation.ui.product.list

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.santiagobattaglino.mvvm.codebase.R
import com.santiagobattaglino.mvvm.codebase.data.repository.SharedPreferenceUtils
import com.santiagobattaglino.mvvm.codebase.domain.entity.Product
import com.santiagobattaglino.mvvm.codebase.presentation.ui.FullScreenDialog
import com.santiagobattaglino.mvvm.codebase.presentation.ui.base.BasePermissionFragment
import com.santiagobattaglino.mvvm.codebase.presentation.ui.incidents.detail.IncidentDetailActivity
import com.santiagobattaglino.mvvm.codebase.presentation.ui.product.adapter.ProductAdapter
import com.santiagobattaglino.mvvm.codebase.presentation.viewmodel.ProductViewModel
import com.santiagobattaglino.mvvm.codebase.util.Arguments
import com.santiagobattaglino.mvvm.codebase.util.convertDpToPixel
import kotlinx.android.synthetic.main.fragment_stock_by_user_list.*
import org.jetbrains.anko.intentFor
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import pub.devrel.easypermissions.EasyPermissions

class ProductListFragment : BasePermissionFragment(), ProductAdapter.OnViewHolderClick,
    FullScreenDialog.OnFullScreenDialogViewClickListener {

    private val mTag = javaClass.simpleName

    private val productViewModel: ProductViewModel by sharedViewModel()
    private val sp: SharedPreferenceUtils by inject()

    private var products = mutableListOf<Product>()

    private lateinit var adapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_product_list, container, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        list.adapter = null
    }

    override fun onPrimaryNavigationFragmentChanged(isPrimaryNavigationFragment: Boolean) {
        super.onPrimaryNavigationFragmentChanged(isPrimaryNavigationFragment)
        if (isPrimaryNavigationFragment) {
            productViewModel.getProducts()
        }
    }

    override fun setUpViews() {
        setUpAppBar()

        adapter = ProductAdapter(this)
        list.adapter = adapter
        val layoutManager = LinearLayoutManager(
            context,
            RecyclerView.VERTICAL,
            false
        )
        list.layoutManager = layoutManager
        list.setHasFixedSize(false)

        /*nested_scroll_view.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, _, scrollY, _, oldScrollY ->
            if (v.getChildAt(v.childCount - 1) != null) {
                if (scrollY >= v.getChildAt(v.childCount - 1).measuredHeight - v.measuredHeight && scrollY > oldScrollY
                ) {
                    incidentsViewModel.getIncidents(
                        sp.getString(Arguments.ARG_LAST_KNOWN_LOCATION),
                        Constants.PAGE_SIZE,
                        incidents[incidents.lastIndex].updatedAt
                    )
                }
            }
        })*/
    }

    override fun observe() {
        observeProducts()
    }

    private fun observeProducts() {
        productViewModel.productsUiData.observe(viewLifecycleOwner, {
            it.error?.let { error ->
                handleError(mTag, error)
            }

            it.products?.let { products ->
                this.products = products.toMutableList()

                // Mocked Incident for Header Type
                this.products.add(0, Product(-1))

                adapter.mData = this.products.toList()
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(
            mTag,
            "onActivityResult requestCode: $requestCode, resultCode: $resultCode, data: $data"
        )
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
                    Log.d(
                        mTag,
                        "onRequestPermissionsResult locationPermReq granted"
                    )
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

    override fun dataViewClickFromList(view: View, position: Int, data: Product) {
        adapter.popupWindow?.dismiss()
        when (view.id) {
            R.id.item_incident_normal_container -> context?.startActivity(
                context?.intentFor<IncidentDetailActivity>(
                    Arguments.ARG_INCIDENT_ID to data.id
                )
            )
            R.id.item_incident_live_container -> {

            }
        }
    }

    private fun setUpAppBar() {
        val params = app_bar_layout.layoutParams as CoordinatorLayout.LayoutParams
        context?.let {
            params.height = it.resources.displayMetrics.heightPixels - convertDpToPixel(110, it)
        }
    }

    override fun onFullScreenDialogViewClicked(view: View) {

    }
}