package com.santiagobattaglino.mvvm.codebase.presentation.ui.incidents.tutorial

import com.santiagobattaglino.mvvm.codebase.presentation.ui.base.BaseFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.santiagobattaglino.mvvm.codebase.R
import kotlinx.android.synthetic.main.fragment_place_not_allowed.*

class PlaceNotAllowedFragment : BaseFragment() {

    private val mTag = javaClass.simpleName

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_place_not_allowed, container, false)
    }

    override fun observe() {

    }

    override fun setUpViews() {
        action.setOnClickListener {
            findNavController().navigate(R.id.place_not_allowed_to_home)
        }
    }
}