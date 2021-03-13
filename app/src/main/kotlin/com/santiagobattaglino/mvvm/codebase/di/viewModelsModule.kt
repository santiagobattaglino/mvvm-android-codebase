package com.santiagobattaglino.mvvm.codebase.di

import com.santiagobattaglino.mvvm.codebase.presentation.viewmodel.*
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelsModule = module {
    viewModel { LoginViewModel(get()) }
    viewModel { IncidentsViewModel(get()) }
    viewModel { CommentsViewModel(get()) }
    viewModel { UpdatesViewModel(get()) }
    viewModel { NotificationsViewModel(get()) }
}
