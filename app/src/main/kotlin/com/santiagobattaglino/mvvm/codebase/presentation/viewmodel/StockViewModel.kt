package com.santiagobattaglino.mvvm.codebase.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.santiagobattaglino.mvvm.codebase.data.network.error.ErrorObject
import com.santiagobattaglino.mvvm.codebase.data.repository.stock.StockRepo
import com.santiagobattaglino.mvvm.codebase.domain.entity.Stock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StockViewModel(
    private val repo: StockRepo
) : ViewModel() {

    private val mTag = javaClass.simpleName

    val stockByUserUiData = MutableLiveData<ResultStockByUser>()

    fun getStockByUser(userId: Int) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                repo.getStockByUser(userId)
            }

            result.stockByUser?.let {
                stockByUserUiData.value = ResultStockByUser(it)
                // we're already observing local changes with flow. Only login chunks of 10 from server.
                //Log.d(mTag, "loading all stockByUser on change")
            }

            result.error?.let {
                stockByUserUiData.value = ResultStockByUser(error = it)
            }
        }
    }

    fun getStockByUserFromLocal(userId: Int) {
        viewModelScope.launch {
            repo.getStockByUserFromLocal(userId).collect {
                stockByUserUiData.value = ResultStockByUser(it)
            }
        }
    }
}

data class ResultStockByUser(
    val stockByUser: List<Stock>? = null,
    val error: ErrorObject? = null
)