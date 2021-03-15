package com.santiagobattaglino.mvvm.codebase.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.santiagobattaglino.mvvm.codebase.data.network.error.ErrorObject
import com.santiagobattaglino.mvvm.codebase.data.repository.category.CategoryRepo
import com.santiagobattaglino.mvvm.codebase.domain.entity.Category
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CategoryViewModel(
    private val repo: CategoryRepo
) : ViewModel() {

    private val mTag = javaClass.simpleName

    val categoriesUiData = MutableLiveData<ResultCats>()

    fun getCategories() {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                repo.getList()
            }

            result.categories?.let {
                categoriesUiData.value = ResultCats(it)
            }

            result.error?.let {
                categoriesUiData.value = ResultCats(error = it)
            }
        }
    }
}

data class ResultCats(
    val categories: List<Category>? = null,
    val error: ErrorObject? = null
)