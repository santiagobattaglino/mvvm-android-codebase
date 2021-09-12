package com.santiagobattaglino.mvvm.codebase.data.repository.login

import com.santiagobattaglino.mvvm.codebase.domain.entity.Login
import com.santiagobattaglino.mvvm.codebase.domain.model.LoginRequest
import com.santiagobattaglino.mvvm.codebase.presentation.viewmodel.ResultLogin

interface LoginRepository {
    suspend fun postLogin(loginRequest: LoginRequest): ResultLogin
    suspend fun saveLogin(login: Login)
    suspend fun getLogin(): Login?
    suspend fun deleteLogin(id: Int)
}