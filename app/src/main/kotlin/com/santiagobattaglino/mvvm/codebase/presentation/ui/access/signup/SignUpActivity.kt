package com.santiagobattaglino.mvvm.codebase.presentation.ui.access.signup

import com.santiagobattaglino.mvvm.codebase.data.repository.SharedPreferenceUtils
import com.santiagobattaglino.mvvm.codebase.domain.model.RegisterRequest
import com.santiagobattaglino.mvvm.codebase.presentation.ui.BottomNavActivity
import com.santiagobattaglino.mvvm.codebase.presentation.ui.access.Validator
import com.santiagobattaglino.mvvm.codebase.presentation.ui.base.BaseActivity
import com.santiagobattaglino.mvvm.codebase.presentation.viewmodel.LoginViewModel
import com.santiagobattaglino.mvvm.codebase.util.Arguments
import android.os.Bundle
import android.view.View
import com.santiagobattaglino.mvvm.codebase.R
import kotlinx.android.synthetic.main.activity_signup.*
import org.jetbrains.anko.startActivity
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class SignUpActivity : BaseActivity() {

    private val tag = javaClass.simpleName

    private val viewModel: LoginViewModel by viewModel()
    private val sp: SharedPreferenceUtils by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        setUpViews()
    }

    override fun observe() {
        viewModel.loginUiData.observe(this, {
            it.login?.let { login ->
                sp.saveString(Arguments.ARG_USER_ID, login.id)
                sp.saveString(Arguments.ARG_USER_EMAIL, login.email)
                sp.saveString(Arguments.ARG_USER_TOKEN, login.token)
                startActivity<BottomNavActivity>()
                finish()
            }

            it.error?.let { errorObject ->
                errorObject.msg?.let {
                    signup.isEnabled = true
                    if (errorObject.isBadRequest()) {
                        error.visibility = View.VISIBLE
                        when (errorObject.msg) {
                            "Validation failed: User already exists" -> error.text =
                                getString(R.string.error_user_already_exists)
                            "Validation failed: PASSWORD_TOO_LONG" -> error.text =
                                getString(R.string.error_password_too_long)
                            "Validation failed: PASSWORD_TOO_SHORT" -> error.text =
                                getString(R.string.error_password_too_short)
                            "Validation failed: lastName should not be empty" -> error.text =
                                getString(R.string.error_empty_last_name)
                            "Validation failed: name should not be empty" -> error.text =
                                getString(R.string.error_empty_name)
                            //"Validation failed: email must be an email" -> error.text = getString(R.string.error_invalid_email)
                            else -> error.text = getString(R.string.error_generic)
                        }
                    } else {
                        handleError(tag, errorObject)
                    }
                }
            }
        })
    }

    private fun setUpViews() {
        setUpAppBar()
        signUpBtn()
    }

    private fun setUpAppBar() {
        topAppBar.title = getString(R.string.action_signup)
        topAppBar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun signUpBtn() {
        signup.setOnClickListener {
            error.visibility = View.GONE
            if (
                Validator.isValidEmail(email, true)
                &&
                Validator.isValidPassword(password, true)
                &&
                Validator.isValidPassword(confirm_password, true)
                &&
                password.text.toString() == confirm_password.text.toString()
            ) {
                signup.isEnabled = false
                viewModel.postRegister(
                    RegisterRequest(
                        first_name.text.toString(),
                        last_name.text.toString(),
                        email.text.toString(),
                        password.text.toString()
                    )
                )
            } else if (Validator.isValidPassword(
                    password,
                    false
                ) && password.text.toString() != confirm_password.text.toString()
            ) {
                password_layout.error = getString(R.string.error_wrong_password_confirmation)
                confirm_password_layout.error =
                    getString(R.string.error_wrong_password_confirmation)
            }
        }
    }
}