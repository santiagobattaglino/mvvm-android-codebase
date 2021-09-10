package com.santiagobattaglino.mvvm.codebase.data.repository.stock

import android.util.Log
import com.haroldadmin.cnradapter.NetworkResponse
import com.santiagobattaglino.mvvm.codebase.data.network.api.Api
import com.santiagobattaglino.mvvm.codebase.data.network.error.ErrorObject
import com.santiagobattaglino.mvvm.codebase.data.room.dao.StockDAO
import com.santiagobattaglino.mvvm.codebase.domain.entity.Stock
import com.santiagobattaglino.mvvm.codebase.presentation.viewmodel.ResultProducts
import com.santiagobattaglino.mvvm.codebase.presentation.viewmodel.ResultStockByUser
import kotlinx.coroutines.flow.Flow

class StockRepoRoomImpl(
    private val apiRest: Api,
    private val stockDAO: StockDAO
) : StockRepo {

    private val tag = javaClass.simpleName

    override suspend fun getStockByUser(userId: Int): ResultStockByUser {
        return when (val networkResponse = apiRest.getStockByUser(userId)) {
            is NetworkResponse.Success -> {
                val result = networkResponse.body
                result.map {
                    it.userId = userId
                }
                saveStockListLocal(result)
                ResultStockByUser(result, null)
            }
            is NetworkResponse.ServerError -> {
                Log.d(
                    tag,
                    ErrorObject(networkResponse.code, networkResponse.body?.message).toString()
                )
                ResultStockByUser(
                    null,
                    ErrorObject(networkResponse.code, networkResponse.body?.message)
                )
            }
            is NetworkResponse.NetworkError -> {
                Log.d(tag, ErrorObject(0, networkResponse.error.message).toString())
                ResultStockByUser(null, null)
            }
            is NetworkResponse.UnknownError -> {
                val error = ErrorObject(ErrorObject.UNKNOWN, "unknown error")
                ResultStockByUser(null, error)
            }
        }
    }

    override suspend fun saveStockListLocal(data: List<Stock>) {
        Log.d(tag, "saveStockListLocal size: ${data.size}")
        stockDAO.saveList(data)
    }

    override fun getStockByUserFromLocal(userId: Int): Flow<List<Stock>> {
        return stockDAO.getStockByUserFromLocal(userId)
    }
}