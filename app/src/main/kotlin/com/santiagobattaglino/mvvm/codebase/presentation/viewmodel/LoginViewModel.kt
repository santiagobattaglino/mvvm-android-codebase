package com.santiagobattaglino.mvvm.codebase.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.santiagobattaglino.mvvm.codebase.data.network.error.ErrorObject
import com.santiagobattaglino.mvvm.codebase.data.repository.login.LoginRepository
import com.santiagobattaglino.mvvm.codebase.domain.entity.Login
import com.santiagobattaglino.mvvm.codebase.domain.model.LoginRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel(
    private val loginRepository: LoginRepository
) : ViewModel() {

    val loginUiData = MutableLiveData<ResultLogin>()

    fun postLogin(loginRequest: LoginRequest) {
        viewModelScope.launch {
            val resultLogin = withContext(Dispatchers.IO) {
                loginRepository.postLogin(loginRequest)
            }

            resultLogin.login?.let {
                loginUiData.value = ResultLogin(it)
            }

            resultLogin.error?.let {
                loginUiData.value = ResultLogin(error = it)
            }
        }
    }
}

data class ResultLogin(val login: Login? = null, val error: ErrorObject? = null)