package com.santiagobattaglino.mvvm.codebase.presentation.ui.access.login

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.updatePadding
import com.santiagobattaglino.mvvm.codebase.BuildConfig
import com.santiagobattaglino.mvvm.codebase.R
import com.santiagobattaglino.mvvm.codebase.data.repository.SharedPreferenceUtils
import com.santiagobattaglino.mvvm.codebase.domain.model.LoginRequest
import com.santiagobattaglino.mvvm.codebase.presentation.ui.BottomNavActivity
import com.santiagobattaglino.mvvm.codebase.presentation.ui.access.Validator
import com.santiagobattaglino.mvvm.codebase.presentation.ui.access.signup.SignUpActivity
import com.santiagobattaglino.mvvm.codebase.presentation.ui.base.BaseActivity
import com.santiagobattaglino.mvvm.codebase.presentation.viewmodel.LoginViewModel
import com.santiagobattaglino.mvvm.codebase.util.Arguments
import com.santiagobattaglino.mvvm.codebase.util.Constants
import kotlinx.android.synthetic.main.activity_login.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class LoginActivity : BaseActivity() {

    private val tag = javaClass.simpleName

    private val viewModel: LoginViewModel by viewModel()
    private val sp: SharedPreferenceUtils by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setUpViews()
    }

    override fun observe() {
        viewModel.loginUiData.observe(this, {
            it.login?.let { login ->
                sp.saveString(Arguments.ARG_USER_ID, login.id)
                sp.saveString(Arguments.ARG_USER_EMAIL, login.email)
                sp.saveString(Arguments.ARG_USER_TOKEN, login.token)
                startActivity(Intent(this, BottomNavActivity::class.java))
                finish()
            }

            it.error?.let { errorObject ->
                if (errorObject.isBadRequest()) {
                    errorObject.msg?.let {
                        login.isEnabled = true
                        error.visibility = View.VISIBLE
                        error.text = errorObject.msg
                    }
                } else {
                    login.isEnabled = true
                    error.visibility = View.VISIBLE
                    error.text = getString(R.string.no_internet)
                }
            }
        })
    }

    private fun setUpViews() {
        setUpForgotPassword()
        setUpLogin()
        setUpSignUp()
        setupInsets()

        if (BuildConfig.DEBUG) {
            logo.setOnLongClickListener {
                login.isEnabled = false
                viewModel.postLogin(
                    LoginRequest(
                        "sbattaglino@genium.io",
                        "12345678"
                    )
                )
                return@setOnLongClickListener false
            }
        }
    }

    private fun setUpForgotPassword() {
        forgot_password.setOnClickListener {
            /*if (Validator.isValidEmail(username, true)) {
                email(
                    Constants.SUPPORT_EMAIL,
                    String.format(getString(R.string.forgot_password_subject), username.text),
                    String.format(getString(R.string.forgot_password_body), username.text)
                )
            }*/
        }
    }

    private fun setUpLogin() {
        login.setOnClickListener {
            error.visibility = View.GONE
            if (Validator.isValidEmail(username, true) && Validator.isValidPassword(
                    password,
                    true
                )
            ) {
                login.isEnabled = false
                viewModel.postLogin(
                    LoginRequest(
                        username.text.toString(),
                        password.text.toString()
                    )
                )
            }
        }
    }

    private fun setUpSignUp() {
        val spannableString = SpannableString(getString(R.string.signup))
        val clickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                startActivity(Intent(this@LoginActivity, SignUpActivity::class.java))
            }
        }

        val currentAppLocale = Locale.getDefault().language
        if (currentAppLocale == "pt") {
            spannableString.setSpan(
                clickableSpan,
                18,
                spannableString.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        } else {
            spannableString.setSpan(
                clickableSpan,
                27,
                spannableString.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        signup.movementMethod = LinkMovementMethod.getInstance()
        signup.text = spannableString
    }

    private fun setupInsets() {
        login_activity_container.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION

        ViewCompat.setOnApplyWindowInsetsListener(login_activity_container) { _, insets ->
            login_activity_container.updatePadding(top = -insets.stableInsetTop)
            insets
        }
    }
}