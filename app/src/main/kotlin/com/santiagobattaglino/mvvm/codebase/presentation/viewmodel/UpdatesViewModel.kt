package com.santiagobattaglino.mvvm.codebase.presentation.viewmodel

import com.santiagobattaglino.mvvm.codebase.data.network.error.ErrorObject
import com.santiagobattaglino.mvvm.codebase.data.repository.update.UpdateRepo
import com.santiagobattaglino.mvvm.codebase.domain.entity.Update
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UpdatesViewModel(
    private val repo: UpdateRepo
) : ViewModel() {

    val updatesUiData = MutableLiveData<ResultUpdates>()

    fun getUpdates(incidentId: Int) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                repo.getUpdates(incidentId)
            }

            result.updates?.let {
                updatesUiData.value =
                    ResultUpdates(it)
            }

            result.error?.let {
                updatesUiData.value =
                    ResultUpdates(error = it)
            }
        }
    }
}

data class ResultUpdates(val updates: List<Update>? = null, val error: ErrorObject? = null)