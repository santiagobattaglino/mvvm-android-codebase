package com.santiagobattaglino.mvvm.codebase.presentation.ui.base

import com.santiagobattaglino.mvvm.codebase.data.network.error.ErrorObject
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.santiagobattaglino.mvvm.codebase.BuildConfig

abstract class BaseActivity : AppCompatActivity() {

    private val tag = javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        observe()
    }

    internal fun handleError(tag: String, error: ErrorObject) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, error.toString())
            //longToast(error.toString())
        }
    }

    abstract fun observe()
}