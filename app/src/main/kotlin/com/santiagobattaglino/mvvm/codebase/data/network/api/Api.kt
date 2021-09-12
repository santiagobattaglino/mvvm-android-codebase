package com.santiagobattaglino.mvvm.codebase.data.network.api

import com.santiagobattaglino.mvvm.codebase.data.network.error.ErrorResponse
import com.haroldadmin.cnradapter.NetworkResponse
import com.santiagobattaglino.mvvm.codebase.domain.entity.*
import com.santiagobattaglino.mvvm.codebase.domain.model.*
import retrofit2.http.*

interface Api {

    @POST("/auth/login")
    suspend fun postLogin(
        @Body loginRequest: LoginRequest
    ): NetworkResponse<Login, ErrorResponse>
}