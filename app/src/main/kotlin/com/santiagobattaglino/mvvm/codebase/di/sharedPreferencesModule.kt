package com.santiagobattaglino.mvvm.codebase.di

import com.santiagobattaglino.mvvm.codebase.data.repository.SharedPreferenceUtils
import android.content.Context.MODE_PRIVATE
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val sharedPreferencesModule = module {
    single {
        SharedPreferenceUtils(
            androidContext().getSharedPreferences(
                SharedPreferenceUtils.SHARED_PREF_NAME,
                MODE_PRIVATE
            )
        )
    }
}