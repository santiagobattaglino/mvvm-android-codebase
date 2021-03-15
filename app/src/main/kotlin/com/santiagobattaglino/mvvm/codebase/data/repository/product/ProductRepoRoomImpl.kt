package com.santiagobattaglino.mvvm.codebase.data.repository.product

import android.util.Log
import com.haroldadmin.cnradapter.NetworkResponse
import com.santiagobattaglino.mvvm.codebase.data.network.api.Api
import com.santiagobattaglino.mvvm.codebase.data.network.error.ErrorObject
import com.santiagobattaglino.mvvm.codebase.data.room.dao.ProductDAO
import com.santiagobattaglino.mvvm.codebase.data.room.dao.StockDAO
import com.santiagobattaglino.mvvm.codebase.domain.entity.Product
import com.santiagobattaglino.mvvm.codebase.domain.entity.Stock
import com.santiagobattaglino.mvvm.codebase.presentation.viewmodel.ResultProducts
import com.santiagobattaglino.mvvm.codebase.presentation.viewmodel.ResultStockByUser
import kotlinx.coroutines.flow.Flow

class ProductRepoRoomImpl(
    private val apiRest: Api,
    private val productDAO: ProductDAO
) : ProductRepo {

    private val tag = javaClass.simpleName

    override suspend fun getProducts(): ResultProducts {
        return when (val networkResponse = apiRest.getProducts()) {
            is NetworkResponse.Success -> {
                val result = networkResponse.body
                saveProductsLocal(result)
                ResultProducts(productDAO.getList(), null)
            }
            is NetworkResponse.ServerError -> {
                Log.d(
                    tag,
                    ErrorObject(networkResponse.code, networkResponse.body?.message).toString()
                )
                ResultProducts(
                    null,
                    ErrorObject(networkResponse.code, networkResponse.body?.message)
                )
            }
            is NetworkResponse.NetworkError -> {
                Log.d(tag, ErrorObject(0, networkResponse.error.message).toString())
                ResultProducts(null, null)
            }
        }
    }

    override suspend fun saveProductsLocal(data: List<Product>) {
        Log.d(tag, "saveProductsLocal size: ${data.size}")
        productDAO.saveList(data)
    }

    override fun getProductsFlow(): Flow<List<Product>> {
        return productDAO.getListFlow()
    }

    override suspend fun getProductsByCategory(catId: Int): ResultProducts {
        return ResultProducts(productDAO.getProductsByCategory(catId), null)
    }
}