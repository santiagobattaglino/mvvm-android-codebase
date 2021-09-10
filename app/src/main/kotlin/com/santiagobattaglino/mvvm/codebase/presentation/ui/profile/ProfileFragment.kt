package com.santiagobattaglino.mvvm.codebase.presentation.ui.profile

import com.santiagobattaglino.mvvm.codebase.data.repository.SharedPreferenceUtils
import com.santiagobattaglino.mvvm.codebase.domain.entity.Login
import com.santiagobattaglino.mvvm.codebase.domain.model.RateApp
import com.santiagobattaglino.mvvm.codebase.presentation.ui.base.BaseFragment
import com.santiagobattaglino.mvvm.codebase.presentation.ui.splash.SplashActivity
import com.santiagobattaglino.mvvm.codebase.presentation.viewmodel.LoginViewModel
import com.santiagobattaglino.mvvm.codebase.util.Arguments
import com.santiagobattaglino.mvvm.codebase.util.Constants
import com.santiagobattaglino.mvvm.codebase.util.GlideUrlCustomCacheKey
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.core.view.ViewCompat
import androidx.core.view.updatePadding
import com.bumptech.glide.request.RequestOptions
import com.santiagobattaglino.mvvm.codebase.R
import com.santiagobattaglino.mvvm.codebase.util.GlideApp
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.popup_rate_app.view.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProfileFragment : BaseFragment() {

    private val mTag = javaClass.simpleName

    private val loginViewModel: LoginViewModel by viewModel()
    private val sp: SharedPreferenceUtils by inject()
    private var popupWindow: PopupWindow? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginViewModel.getLogin()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun observe() {
        observeLogin()
        observeRateApp()
    }

    private fun observeLogin() {
        loginViewModel.loginUiData.observe(this, {
            it.login?.let { login ->
                setLoginSessionInfo(login)
            }
        })
    }

    private fun observeRateApp() {
        loginViewModel.rateAppUiData.observe(this, {
            it.rateApp?.let { rateApp ->
                //setLoginSessionInfo(login)
                Log.d(mTag, "$rateApp")
                //context?.toast(R.string.thanks)
            }
        })
    }

    private fun setLoginSessionInfo(login: Login) {
        login.thumbnail?.let {
            context?.let { context ->
                GlideApp.with(context)
                    .load(GlideUrlCustomCacheKey(it))
                    .apply(RequestOptions.circleCropTransform())
                    .into(photo)
            }
        }

        full_name.text = String.format("%s %s", login.name, login.lastName)
        email.text = login.email
        friends_count.text = "0"
        account_private.isChecked = login.isPrivateAccount

        support_center.setOnClickListener {
            /*context?.email(
                Constants.SUPPORT_EMAIL,
                String.format(getString(R.string.support_subject), login.email)
            )*/
        }
    }

    @SuppressLint("InflateParams")
    override fun setUpViews() {
        ViewCompat.setOnApplyWindowInsetsListener(profile_container) { _, insets ->
            profile_container.updatePadding(top = insets.stableInsetTop + 70)
            insets
        }

        setUpAppBar()

        account_private.setOnCheckedChangeListener { _, isChecked ->
            loginViewModel.setAccountPrivate(isChecked)
        }

        rate_the_app.setOnClickListener {
            val inflater: LayoutInflater =
                context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

            val popupWindowView = inflater.inflate(R.layout.popup_rate_app, null).apply {
                this.positive.setOnClickListener {
                    loginViewModel.rateApp(RateApp.RATE_POSITIVE)
                    popupWindow?.dismiss()
                }

                this.negative.setOnClickListener {
                    loginViewModel.rateApp(RateApp.RATE_NEGATIVE)
                    popupWindow?.dismiss()
                }
            }

            popupWindow = PopupWindow(
                popupWindowView,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            popupWindow?.isOutsideTouchable = true
            popupWindow?.showAtLocation(it, Gravity.CENTER, 0, 0)
        }

        logout.setOnClickListener {
            logout()
        }
    }

    private fun logout() {
        sp.removeString(Arguments.ARG_USER_ID)
        loginViewModel.deleteLogin(1)
        activity?.finish()
        //activity?.startActivity<SplashActivity>()
    }

    private fun setUpAppBar() {
        topAppBar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }
    }
}