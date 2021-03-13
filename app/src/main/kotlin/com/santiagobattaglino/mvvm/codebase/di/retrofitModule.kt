package com.santiagobattaglino.mvvm.codebase.di

import com.santiagobattaglino.mvvm.codebase.data.network.api.Api
import com.santiagobattaglino.mvvm.codebase.data.repository.SharedPreferenceUtils
import com.santiagobattaglino.mvvm.codebase.util.Arguments
import com.santiagobattaglino.mvvm.codebase.util.Constants
import com.google.gson.GsonBuilder
import com.google.gson.internal.bind.DateTypeAdapter
import com.haroldadmin.cnradapter.NetworkResponseAdapterFactory
import com.santiagobattaglino.mvvm.codebase.BuildConfig
import io.github.wax911.library.converter.GraphConverter
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit

val retrofitModule = module {
    single {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        interceptor
    }

    single { GsonBuilder().registerTypeAdapter(Date::class.java, DateTypeAdapter()).create() }

    // TODO this is working on the next launch after login or signIn. Check this: https://stackoverflow.com/questions/43051558/dagger-retrofit-adding-auth-headers-at-runtime
    // TODO Add Authorization header
    single {
        val sp: SharedPreferenceUtils = get()
        OkHttpClient.Builder()
            .connectTimeout(Constants.TIME_OUT, TimeUnit.SECONDS)
            .readTimeout(Constants.TIME_OUT, TimeUnit.SECONDS)
            //.retryOnConnectionFailure(true)
            .addInterceptor { chain ->
                val original = chain.request()
                val token = sp.getString(Arguments.ARG_USER_TOKEN) ?: ""
                val requestBuilder = original.newBuilder()
                    .header("Sec-WebSocket-Protocol", token)
                val request = requestBuilder.build()
                chain.proceed(request)
            }
            .addInterceptor(get() as HttpLoggingInterceptor)
            .build()
    }

    single(named("RetrofitRestBuilder")) {
        Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(get()))
            .addCallAdapterFactory(NetworkResponseAdapterFactory())
            .client(get() as OkHttpClient)
            .build()
    }

    single(named("RetrofitGraphBuilder")) {
        Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .addConverterFactory(GraphConverter.create(androidContext()))
            .addCallAdapterFactory(NetworkResponseAdapterFactory())
            .client(get() as OkHttpClient)
            .build()
    }

    single(named("RetrofitRest")) {
        (get(named("RetrofitRestBuilder")) as Retrofit).create(Api::class.java)
    }

    single(named("RetrofitGraph")) {
        (get(named("RetrofitGraphBuilder")) as Retrofit).create(Api::class.java)
    }
}
