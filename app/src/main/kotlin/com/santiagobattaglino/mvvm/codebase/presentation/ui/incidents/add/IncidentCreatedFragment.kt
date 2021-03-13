package com.santiagobattaglino.mvvm.codebase.presentation.ui.incidents.add

import com.santiagobattaglino.mvvm.codebase.presentation.ui.base.BaseFragment
import com.santiagobattaglino.mvvm.codebase.util.Arguments
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.santiagobattaglino.mvvm.codebase.R
import kotlinx.android.synthetic.main.fragment_incident_created.*

class IncidentCreatedFragment : BaseFragment() {

    private val mTag = javaClass.simpleName

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_incident_created, container, false)
    }

    override fun observe() {

    }

    override fun setUpViews() {
        arguments?.getInt(Arguments.ARG_INCIDENT_ID)?.let { incidentId ->
            action.setOnClickListener {
                findNavController().navigate(
                    R.id.incident_created_to_incident_detail,
                    bundleOf(Arguments.ARG_INCIDENT_ID to incidentId)
                )
            }

            sub_action.setOnClickListener {
                findNavController().navigate(R.id.incident_created_to_home)
            }
        }
    }
}