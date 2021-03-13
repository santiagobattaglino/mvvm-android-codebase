package com.santiagobattaglino.mvvm.codebase.presentation.viewmodel

import com.santiagobattaglino.mvvm.codebase.data.network.error.ErrorObject
import com.santiagobattaglino.mvvm.codebase.data.repository.login.LoginRepository
import com.santiagobattaglino.mvvm.codebase.domain.entity.Login
import com.santiagobattaglino.mvvm.codebase.domain.model.LoginRequest
import com.santiagobattaglino.mvvm.codebase.domain.model.RateApp
import com.santiagobattaglino.mvvm.codebase.domain.model.RegisterRequest
import com.santiagobattaglino.mvvm.codebase.domain.model.SetAccountPrivateRequest
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel(
    private val loginRepository: LoginRepository
) : ViewModel() {

    val loginUiData = MutableLiveData<ResultLogin>()
    val rateAppUiData = MutableLiveData<ResultRateApp>()

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

    fun postRegister(request: RegisterRequest) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                loginRepository.postRegister(request)
            }

            result.login?.let {
                postLogin(LoginRequest(email = it.email, password = request.password))
            }

            result.error?.let {
                loginUiData.value = ResultLogin(error = it)
            }
        }
    }

    fun getLogin() {
        viewModelScope.launch {
            val login = withContext(Dispatchers.IO) {
                loginRepository.getLogin()
            }

            loginUiData.value = ResultLogin(login)
        }
    }

    fun deleteLogin(id: Int) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                loginRepository.deleteLogin(id)
            }
        }
    }

    fun setAccountPrivate(isAccountPrivate: Boolean) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                loginRepository.updateUser(SetAccountPrivateRequest(isAccountPrivate))
            }

            result.login?.let {
                //postLogin(LoginRequest(email = it.email, password = request.password))
            }

            result.error?.let {
                loginUiData.value = ResultLogin(error = it)
            }
        }
    }

    fun rateApp(typeRating: String) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                loginRepository.rateApp(typeRating)
            }

            result.rateApp?.let {
                rateAppUiData.value = ResultRateApp(it)
            }

            result.error?.let {
                rateAppUiData.value = ResultRateApp(error = it)
            }
        }
    }
}

data class ResultLogin(val login: Login? = null, val error: ErrorObject? = null)
data class ResultRateApp(val rateApp: RateApp? = null, val error: ErrorObject? = null)