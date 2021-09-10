package com.santiagobattaglino.mvvm.codebase.data.repository.update

import com.santiagobattaglino.mvvm.codebase.data.network.api.Api
import com.santiagobattaglino.mvvm.codebase.data.network.error.ErrorObject
import com.santiagobattaglino.mvvm.codebase.data.room.dao.LoginDAO
import com.santiagobattaglino.mvvm.codebase.data.room.dao.UpdateDAO
import com.santiagobattaglino.mvvm.codebase.domain.entity.Update
import com.santiagobattaglino.mvvm.codebase.presentation.viewmodel.ResultUpdates
import android.util.Log
import com.haroldadmin.cnradapter.NetworkResponse
import com.santiagobattaglino.mvvm.codebase.presentation.viewmodel.ResultStockByUser
import io.github.wax911.library.model.request.QueryContainerBuilder

class UpdateRepoRoomImpl(
    private val api: Api,
    private val loginDAO: LoginDAO,
    private val updateDAO: UpdateDAO
) : UpdateRepo {

    private val tag = javaClass.simpleName

    override suspend fun getUpdates(incidentId: Int): ResultUpdates {
        val requestBody = QueryContainerBuilder()
        requestBody.putVariable("incidentId", incidentId)

        val token = "Bearer " + loginDAO.getToken()

        return when (val networkResponse = api.getUpdates(token, requestBody)) {
            is NetworkResponse.Success -> {
                val updates = networkResponse.body.data?.getUpdatesForIncidentId
                updates?.let {
                    deleteIncidentUpdates(incidentId)
                    if (updates.isNotEmpty()) {
                        it.forEach { update ->
                            update.incidentId = incidentId
                        }
                        saveUpdatesLocal(it)
                    }
                }
                ResultUpdates(updateDAO.getList(incidentId), null)
            }
            is NetworkResponse.ServerError -> {
                Log.d(
                    tag,
                    ErrorObject(networkResponse.code, networkResponse.body?.message).toString()
                )
                ResultUpdates(
                    updateDAO.getList(incidentId),
                    ErrorObject(networkResponse.code, networkResponse.body?.message)
                )
            }
            is NetworkResponse.NetworkError -> {
                Log.d(tag, ErrorObject(0, networkResponse.error.message).toString())
                ResultUpdates(updateDAO.getList(incidentId), null)
            }
            is NetworkResponse.UnknownError -> {
                val error = ErrorObject(ErrorObject.UNKNOWN, "unknown error")
                ResultUpdates(null, error)
            }
        }
    }

    override suspend fun saveUpdatesLocal(updates: List<Update>) {
        updateDAO.saveList(updates)
    }

    override suspend fun deleteIncidentUpdates(incidentId: Int) {
        updateDAO.delete(incidentId)
    }
}