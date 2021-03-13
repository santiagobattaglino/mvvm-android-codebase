package com.santiagobattaglino.mvvm.codebase.presentation.ui.splash

import com.santiagobattaglino.mvvm.codebase.presentation.ui.BottomNavActivity
import com.santiagobattaglino.mvvm.codebase.presentation.ui.access.login.LoginActivity
import com.santiagobattaglino.mvvm.codebase.presentation.ui.base.BaseActivity
import com.santiagobattaglino.mvvm.codebase.presentation.viewmodel.LoginViewModel
import com.santiagobattaglino.mvvm.codebase.util.Arguments
import android.net.Uri
import android.os.Bundle
import android.util.Log
import com.santiagobattaglino.mvvm.codebase.R
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.startActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class SplashActivity : BaseActivity() {

    private val tag = javaClass.simpleName

    private val viewModel: LoginViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        //viewModel.getLogin()
        goToMain()
    }

    override fun observe() {
        viewModel.loginUiData.observe(this, {
            it.login?.let { login ->
                Log.d(tag, login.token)

                val data: Uri? = intent?.data
                val deepLinkIncidentId = data?.getQueryParameter("id")?.toInt()

                deepLinkIncidentId?.let { id ->
                    startActivity(intentFor<BottomNavActivity>(Arguments.ARG_INCIDENT_ID to id))
                } ?: run {
                    goToMain()
                }
            } ?: run {
                goToLogin()
            }
        })
    }

    private fun goToLogin() {
        startActivity<LoginActivity>()
    }

    private fun goToMain() {
        intent?.extras?.getInt(Arguments.ARG_INCIDENT_ID)?.let {
            startActivity(intentFor<BottomNavActivity>(Arguments.ARG_INCIDENT_ID to it))
        } ?: run {
            startActivity<BottomNavActivity>()
        }
    }
}