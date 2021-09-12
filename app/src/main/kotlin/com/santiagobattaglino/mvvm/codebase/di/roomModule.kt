package com.santiagobattaglino.mvvm.codebase.di

import androidx.room.Room
import com.santiagobattaglino.mvvm.codebase.data.repository.login.LoginRepository
import com.santiagobattaglino.mvvm.codebase.data.repository.login.LoginRepositoryRoomImpl
import com.santiagobattaglino.mvvm.codebase.data.room.Database
import com.santiagobattaglino.mvvm.codebase.util.Constants
import org.koin.android.ext.koin.androidApplication
import org.koin.core.qualifier.named
import org.koin.dsl.module

val roomModule = module {
    single<LoginRepository> {
        LoginRepositoryRoomImpl(
            get(named("RetrofitRest")),
            get(named("RetrofitGraph")),
            get()
        )
    }

    single {
        Room.databaseBuilder(androidApplication(), Database::class.java, Constants.DB_NAME)
            .fallbackToDestructiveMigration()
            .build()
    }

    single { get<Database>().loginDAO() }
}