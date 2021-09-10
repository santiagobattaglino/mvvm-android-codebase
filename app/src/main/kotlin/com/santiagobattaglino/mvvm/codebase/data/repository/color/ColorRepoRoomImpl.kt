package com.santiagobattaglino.mvvm.codebase.data.repository.color

import android.util.Log
import com.haroldadmin.cnradapter.NetworkResponse
import com.santiagobattaglino.mvvm.codebase.data.network.api.Api
import com.santiagobattaglino.mvvm.codebase.data.network.error.ErrorObject
import com.santiagobattaglino.mvvm.codebase.data.room.dao.ColorDAO
import com.santiagobattaglino.mvvm.codebase.domain.entity.Color
import com.santiagobattaglino.mvvm.codebase.presentation.viewmodel.ResultCats
import com.santiagobattaglino.mvvm.codebase.presentation.viewmodel.ResultColors

class ColorRepoRoomImpl(
    private val apiRest: Api,
    private val dao: ColorDAO
) : ColorRepo {

    private val tag = javaClass.simpleName

    override suspend fun getList(): ResultColors {
        return when (val networkResponse = apiRest.getColors()) {
            is NetworkResponse.Success -> {
                val result = networkResponse.body
                saveListLocal(result)
                ResultColors(dao.getList(), null)
            }
            is NetworkResponse.ServerError -> {
                Log.d(
                    tag,
                    ErrorObject(networkResponse.code, networkResponse.body?.message).toString()
                )
                ResultColors(
                    null,
                    ErrorObject(networkResponse.code, networkResponse.body?.message)
                )
            }
            is NetworkResponse.NetworkError -> {
                Log.d(tag, ErrorObject(0, networkResponse.error.message).toString())
                ResultColors(null, null)
            }
            is NetworkResponse.UnknownError -> {
                val error = ErrorObject(ErrorObject.UNKNOWN, "unknown error")
                Log.d(tag, error.toString())
                ResultColors(null, error)
            }
        }
    }

    override suspend fun saveListLocal(data: List<Color>) {
        Log.d(tag, "saveListLocal size: ${data.size}")
        dao.saveList(data)
    }
}