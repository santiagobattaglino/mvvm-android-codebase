package com.santiagobattaglino.mvvm.codebase.data.repository.category

import com.santiagobattaglino.mvvm.codebase.domain.entity.Category
import com.santiagobattaglino.mvvm.codebase.presentation.viewmodel.ResultCats

interface CategoryRepo {
    suspend fun getList(): ResultCats
    suspend fun saveListLocal(data: List<Category>)
}