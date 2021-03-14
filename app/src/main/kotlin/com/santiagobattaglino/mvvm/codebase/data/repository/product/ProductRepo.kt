package com.santiagobattaglino.mvvm.codebase.data.repository.product

import com.santiagobattaglino.mvvm.codebase.domain.entity.Product
import com.santiagobattaglino.mvvm.codebase.presentation.viewmodel.ResultProducts
import kotlinx.coroutines.flow.Flow

interface ProductRepo {
    suspend fun getProducts(): ResultProducts
    suspend fun saveProductsLocal(data: List<Product>)
    fun getProductsFlow(): Flow<List<Product>>
}