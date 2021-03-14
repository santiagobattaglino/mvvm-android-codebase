package com.santiagobattaglino.mvvm.codebase.di

import com.santiagobattaglino.mvvm.codebase.data.repository.comment.CommentRepo
import com.santiagobattaglino.mvvm.codebase.data.repository.comment.CommentRepoRoomImpl
import com.santiagobattaglino.mvvm.codebase.data.repository.incident.IncidentRepo
import com.santiagobattaglino.mvvm.codebase.data.repository.incident.IncidentRepoRoomImpl
import com.santiagobattaglino.mvvm.codebase.data.repository.login.LoginRepository
import com.santiagobattaglino.mvvm.codebase.data.repository.login.LoginRepositoryRoomImpl
import com.santiagobattaglino.mvvm.codebase.data.repository.notification.NotificationRepo
import com.santiagobattaglino.mvvm.codebase.data.repository.notification.NotificationRepoRoomImpl
import com.santiagobattaglino.mvvm.codebase.data.repository.update.UpdateRepo
import com.santiagobattaglino.mvvm.codebase.data.repository.update.UpdateRepoRoomImpl
import com.santiagobattaglino.mvvm.codebase.data.room.Database
import com.santiagobattaglino.mvvm.codebase.util.Constants
import androidx.room.Room
import com.santiagobattaglino.mvvm.codebase.data.repository.stock.StockRepo
import com.santiagobattaglino.mvvm.codebase.data.repository.stock.StockRepoRoomImpl
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

    single<IncidentRepo> {
        IncidentRepoRoomImpl(
            get(named("RetrofitRest")),
            get(named("RetrofitGraph")),
            get(),
            get()
        )
    }

    single<CommentRepo> {
        CommentRepoRoomImpl(
            get(named("RetrofitGraph")),
            get(),
            get(),
            get()
        )
    }

    single<UpdateRepo> {
        UpdateRepoRoomImpl(
            get(named("RetrofitGraph")),
            get(),
            get()
        )
    }

    single<NotificationRepo> {
        NotificationRepoRoomImpl(
            get()
        )
    }

    single<StockRepo> {
        StockRepoRoomImpl(
            get(named("RetrofitRest")),
            get()
        )
    }

    single {
        Room.databaseBuilder(androidApplication(), Database::class.java, Constants.DB_NAME)
            .fallbackToDestructiveMigration()
            .build()
    }

    single { get<Database>().loginDAO() }
    single { get<Database>().incidentDAO() }
    single { get<Database>().commentDAO() }
    single { get<Database>().updateDAO() }
    single { get<Database>().notificationDAO() }
    single { get<Database>().stockDAO() }
}