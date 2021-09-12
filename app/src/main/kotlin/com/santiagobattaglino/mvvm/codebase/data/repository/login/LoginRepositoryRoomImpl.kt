package com.santiagobattaglino.mvvm.codebase.data.repository.login

import com.santiagobattaglino.mvvm.codebase.data.network.api.Api
import com.santiagobattaglino.mvvm.codebase.data.network.error.ErrorObject
import com.santiagobattaglino.mvvm.codebase.data.room.dao.LoginDAO
import com.santiagobattaglino.mvvm.codebase.domain.entity.Login
import com.santiagobattaglino.mvvm.codebase.domain.model.LoginRequest
import com.santiagobattaglino.mvvm.codebase.presentation.viewmodel.ResultLogin
import com.haroldadmin.cnradapter.NetworkResponse

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
}