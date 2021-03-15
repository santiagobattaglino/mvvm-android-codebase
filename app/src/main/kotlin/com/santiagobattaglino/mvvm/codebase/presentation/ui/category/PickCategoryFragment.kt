package com.santiagobattaglino.mvvm.codebase.presentation.ui.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.santiagobattaglino.mvvm.codebase.R
import com.santiagobattaglino.mvvm.codebase.data.repository.SharedPreferenceUtils
import com.santiagobattaglino.mvvm.codebase.domain.entity.Category
import com.santiagobattaglino.mvvm.codebase.presentation.ui.base.BaseFragment
import com.santiagobattaglino.mvvm.codebase.presentation.viewmodel.CategoryViewModel
import com.santiagobattaglino.mvvm.codebase.presentation.viewmodel.ProductViewModel
import kotlinx.android.synthetic.main.fragment_pick_category_product.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PickCategoryFragment : BaseFragment(), CategoryAdapter.OnViewHolderClick {

    private val mTag = javaClass.simpleName

    private val categoryViewModel: CategoryViewModel by viewModel()
    private val productsViewModel: ProductViewModel by sharedViewModel()
    private val sp: SharedPreferenceUtils by inject()
    private var categories = mutableListOf<Category>()
    private lateinit var adapter: CategoryAdapter
    private var selectedCategory: Category? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_pick_category_product, container, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        category_grid.adapter = null
    }

    override fun observe() {
        observeCategories()
    }

    private fun observeCategories() {
        categoryViewModel.categoriesUiData.observe(viewLifecycleOwner, {
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
        setUpGrid()
        categoryViewModel.getCategories()
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
            adapter.mData = categories.toList()
            adapter.notifyDataSetChanged()

            productsViewModel.getProductsByCategory(it.id)
        }
    }
}