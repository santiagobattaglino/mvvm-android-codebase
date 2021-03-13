package com.santiagobattaglino.mvvm.codebase.di

import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
val appModule = listOf(
    viewModelsModule,
    retrofitModule,
    roomModule,
    sharedPreferencesModule
)
