package com.santiagobattaglino.mvvm.codebase.data.repository.login

import com.santiagobattaglino.mvvm.codebase.domain.entity.Login
import com.santiagobattaglino.mvvm.codebase.domain.model.LoginRequest
import com.santiagobattaglino.mvvm.codebase.domain.model.RegisterRequest
import com.santiagobattaglino.mvvm.codebase.domain.model.SetAccountPrivateRequest
import com.santiagobattaglino.mvvm.codebase.presentation.viewmodel.ResultLogin
import com.santiagobattaglino.mvvm.codebase.presentation.viewmodel.ResultRateApp

interface LoginRepository {
    suspend fun postLogin(loginRequest: LoginRequest): ResultLogin
    suspend fun postRegister(request: RegisterRequest): ResultLogin
    suspend fun saveLogin(login: Login)
    suspend fun getLogin(): Login?
    suspend fun deleteLogin(id: Int)
    suspend fun updateUser(setAccountPrivateRequest: SetAccountPrivateRequest): ResultLogin
    suspend fun rateApp(typeRating: String): ResultRateApp
}