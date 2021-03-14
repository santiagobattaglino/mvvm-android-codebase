package com.santiagobattaglino.mvvm.codebase.data.repository.stock

import com.santiagobattaglino.mvvm.codebase.domain.entity.Incident
import com.santiagobattaglino.mvvm.codebase.domain.entity.Stock
import com.santiagobattaglino.mvvm.codebase.presentation.viewmodel.ResultStockByUser
import kotlinx.coroutines.flow.Flow

interface StockRepo {
    suspend fun getStockByUser(userId: Int): ResultStockByUser
    suspend fun saveStockListLocal(data: List<Stock>)
    fun getStockByUserFromLocal(userId: Int): Flow<List<Stock>>
}