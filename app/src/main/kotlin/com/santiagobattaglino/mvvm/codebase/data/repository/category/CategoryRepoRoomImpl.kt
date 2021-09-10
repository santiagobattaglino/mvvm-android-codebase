package com.santiagobattaglino.mvvm.codebase.data.repository.category

import android.util.Log
import com.haroldadmin.cnradapter.NetworkResponse
import com.santiagobattaglino.mvvm.codebase.data.network.api.Api
import com.santiagobattaglino.mvvm.codebase.data.network.error.ErrorObject
import com.santiagobattaglino.mvvm.codebase.data.room.dao.CategoryDAO
import com.santiagobattaglino.mvvm.codebase.domain.entity.Category
import com.santiagobattaglino.mvvm.codebase.presentation.viewmodel.ResultCats

class CategoryRepoRoomImpl(
    private val apiRest: Api,
    private val dao: CategoryDAO
) : CategoryRepo {

    private val tag = javaClass.simpleName

    override suspend fun getList(): ResultCats {
        return when (val networkResponse = apiRest.getCategories()) {
            is NetworkResponse.Success -> {
                val result = networkResponse.body
                saveListLocal(result)
                ResultCats(dao.getList(), null)
            }
            is NetworkResponse.ServerError -> {
                Log.d(
                    tag,
                    ErrorObject(networkResponse.code, networkResponse.body?.message).toString()
                )
                ResultCats(
                    null,
                    ErrorObject(networkResponse.code, networkResponse.body?.message)
                )
            }
            is NetworkResponse.NetworkError -> {
                Log.d(tag, ErrorObject(0, networkResponse.error.message).toString())
                ResultCats(null, null)
            }
            is NetworkResponse.UnknownError -> {
                val error = ErrorObject(ErrorObject.UNKNOWN, "unknown error")
                Log.d(tag, error.toString())
                ResultCats(null, error)
            }
        }
    }

    override suspend fun saveListLocal(data: List<Category>) {
        Log.d(tag, "saveListLocal size: ${data.size}")
        dao.saveList(data)
    }
}