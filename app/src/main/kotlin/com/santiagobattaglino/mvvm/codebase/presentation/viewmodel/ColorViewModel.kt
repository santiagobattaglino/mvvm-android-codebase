package com.santiagobattaglino.mvvm.codebase.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.santiagobattaglino.mvvm.codebase.data.network.error.ErrorObject
import com.santiagobattaglino.mvvm.codebase.data.repository.color.ColorRepo
import com.santiagobattaglino.mvvm.codebase.domain.entity.Color
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ColorViewModel(
    private val repo: ColorRepo
) : ViewModel() {

    private val mTag = javaClass.simpleName

    val colorsUiData = MutableLiveData<ResultColors>()

    fun getColors() {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                repo.getList()
            }

            result.colors?.let {
                colorsUiData.value = ResultColors(it)
            }

            result.error?.let {
                colorsUiData.value = ResultColors(error = it)
            }
        }
    }
}

data class ResultColors(
    val colors: List<Color>? = null,
    val error: ErrorObject? = null
)