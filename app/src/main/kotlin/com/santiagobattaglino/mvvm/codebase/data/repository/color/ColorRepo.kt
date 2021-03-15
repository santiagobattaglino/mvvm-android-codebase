package com.santiagobattaglino.mvvm.codebase.data.repository.color

import com.santiagobattaglino.mvvm.codebase.domain.entity.Color
import com.santiagobattaglino.mvvm.codebase.presentation.viewmodel.ResultColors

interface ColorRepo {
    suspend fun getList(): ResultColors
    suspend fun saveListLocal(data: List<Color>)
}