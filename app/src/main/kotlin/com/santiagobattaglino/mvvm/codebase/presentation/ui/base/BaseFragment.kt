package com.santiagobattaglino.mvvm.codebase.presentation.ui.base

import com.santiagobattaglino.mvvm.codebase.data.network.error.ErrorObject
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.santiagobattaglino.mvvm.codebase.BuildConfig

abstract class BaseFragment : Fragment() {

    private val mTag = javaClass.simpleName

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observe()
        setUpViews()
    }

    internal fun handleError(tag: String, error: ErrorObject) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, error.toString())
            //context?.longToast(error.toString())
        }
    }

    abstract fun observe()

    abstract fun setUpViews()
}