package com.santiagobattaglino.mvvm.codebase.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.santiagobattaglino.mvvm.codebase.data.network.error.ErrorObject
import com.santiagobattaglino.mvvm.codebase.data.repository.product.ProductRepo
import com.santiagobattaglino.mvvm.codebase.domain.entity.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProductViewModel(
    private val repo: ProductRepo
) : ViewModel() {

    private val mTag = javaClass.simpleName

    val productsUiData = MutableLiveData<ResultProducts>()

    fun getProducts() {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                repo.getProducts()
            }

            result.products?.let {
                productsUiData.value = ResultProducts(it)
            }

            result.error?.let {
                productsUiData.value = ResultProducts(error = it)
            }
        }
    }
}

data class ResultProducts(
    val products: List<Product>? = null,
    val error: ErrorObject? = null
)