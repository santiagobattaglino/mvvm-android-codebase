package com.santiagobattaglino.mvvm.codebase.domain.model

class RegisterRequest(
    val name: String = "",
    val lastName: String = "",
    val email: String = "",
    val password: String = "",
    val isHqAdmin: Boolean = false
)