package com.santiagobattaglino.mvvm.codebase.data.repository.login

import android.util.Log
import com.santiagobattaglino.mvvm.codebase.data.network.api.Api
import com.santiagobattaglino.mvvm.codebase.data.network.error.ErrorObject
import com.santiagobattaglino.mvvm.codebase.data.room.dao.LoginDAO
import com.santiagobattaglino.mvvm.codebase.domain.entity.Login
import com.santiagobattaglino.mvvm.codebase.domain.model.CreateRateAppRequest
import com.santiagobattaglino.mvvm.codebase.domain.model.LoginRequest
import com.santiagobattaglino.mvvm.codebase.domain.model.RegisterRequest
import com.santiagobattaglino.mvvm.codebase.domain.model.SetAccountPrivateRequest
import com.santiagobattaglino.mvvm.codebase.presentation.viewmodel.ResultLogin
import com.santiagobattaglino.mvvm.codebase.presentation.viewmodel.ResultRateApp
import com.haroldadmin.cnradapter.NetworkResponse
import com.santiagobattaglino.mvvm.codebase.presentation.viewmodel.ResultCategories
import io.github.wax911.library.model.request.QueryContainerBuilder

class LoginRepositoryRoomImpl(
    private val api: Api,
    private val apiGraphql: Api,
    private val loginDAO: LoginDAO
) : LoginRepository {

    override suspend fun postLogin(loginRequest: LoginRequest): ResultLogin {
        return when (val login = api.postLogin(loginRequest)) {
            is NetworkResponse.Success -> {
                saveLogin(login.body)
                ResultLogin(login.body, null)
            }
            is NetworkResponse.ServerError -> {
                ResultLogin(null, ErrorObject(login.code, login.body?.message))
            }
            is NetworkResponse.NetworkError -> {
                ResultLogin(null, ErrorObject(0, login.error.message))
            }
            is NetworkResponse.UnknownError -> {
                val error = ErrorObject(ErrorObject.UNKNOWN, "unknown error")
                ResultLogin(null, error)
            }
        }
    }

    override suspend fun postRegister(request: RegisterRequest): ResultLogin {
        return when (val result = api.postRegister(request)) {
            is NetworkResponse.Success -> {
                ResultLogin(result.body, null)
            }
            is NetworkResponse.ServerError -> {
                ResultLogin(null, ErrorObject(result.code, result.body?.message))
            }
            is NetworkResponse.NetworkError -> {
                ResultLogin(null, ErrorObject(0, result.error.message))
            }
            is NetworkResponse.UnknownError -> {
                val error = ErrorObject(ErrorObject.UNKNOWN, "unknown error")
                ResultLogin(null, error)
            }
        }
    }

    override suspend fun saveLogin(login: Login) {
        // We're overwriting the entry with id = 1
        login.defaultUserId = "1"
        login.time = System.currentTimeMillis()
        loginDAO.saveLogin(login)
    }

    override suspend fun getLogin(): Login? {
        return loginDAO.getLogin()
    }

    override suspend fun deleteLogin(id: Int) {
        return loginDAO.deleteLogin(id)
    }

    override suspend fun updateUser(setAccountPrivateRequest: SetAccountPrivateRequest): ResultLogin {
        val login = loginDAO.getLogin()
        val token = "Bearer " + login?.token
        val requestBody = QueryContainerBuilder()
        requestBody.putVariable("input", setAccountPrivateRequest)
        requestBody.putVariable("userId", login?.id)

        return when (val result = apiGraphql.updateUser(token, requestBody)) {
            is NetworkResponse.Success -> {
                result.body.data?.updateUser?.let {
                    loginDAO.updateUserPrivateStatus(it.isPrivateAccount)
                }
                ResultLogin(result.body.data?.updateUser, null)
            }
            is NetworkResponse.ServerError -> {
                ResultLogin(null, ErrorObject(result.code, result.body?.message))
            }
            is NetworkResponse.NetworkError -> {
                ResultLogin(null, ErrorObject(0, result.error.message))
            }
            is NetworkResponse.UnknownError -> {
                val error = ErrorObject(ErrorObject.UNKNOWN, "unknown error")
                ResultLogin(null, error)
            }
        }
    }

    override suspend fun rateApp(typeRating: String): ResultRateApp {
        val login = loginDAO.getLogin()
        val token = "Bearer " + login?.token
        val requestBody = QueryContainerBuilder()
        requestBody.putVariable("input", CreateRateAppRequest(typeRating))

        return when (val result = apiGraphql.createRateApp(token, requestBody)) {
            is NetworkResponse.Success -> {
                ResultRateApp(result.body.data?.createRateApp, null)
            }
            is NetworkResponse.ServerError -> {
                ResultRateApp(null, ErrorObject(result.code, result.body?.message))
            }
            is NetworkResponse.NetworkError -> {
                ResultRateApp(null, ErrorObject(0, result.error.message))
            }
            is NetworkResponse.UnknownError -> {
                val error = ErrorObject(ErrorObject.UNKNOWN, "unknown error")
                ResultRateApp(null, error)
            }
        }
    }
}