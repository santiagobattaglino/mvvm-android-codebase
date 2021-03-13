package com.santiagobattaglino.mvvm.codebase.presentation.ui.incidents.trending

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.core.widget.NestedScrollView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.santiagobattaglino.mvvm.codebase.R
import com.santiagobattaglino.mvvm.codebase.data.repository.SharedPreferenceUtils
import com.santiagobattaglino.mvvm.codebase.domain.entity.Incident
import com.santiagobattaglino.mvvm.codebase.domain.model.AddReactionRequest
import com.santiagobattaglino.mvvm.codebase.domain.model.WebSocketMessage
import com.santiagobattaglino.mvvm.codebase.presentation.ui.base.BaseFragment
import com.santiagobattaglino.mvvm.codebase.presentation.ui.incidents.detail.IncidentDetailActivity
import com.santiagobattaglino.mvvm.codebase.presentation.ui.incidents.map.IncidentAdapter
import com.santiagobattaglino.mvvm.codebase.presentation.viewmodel.IncidentsViewModel
import com.santiagobattaglino.mvvm.codebase.util.Arguments
import com.santiagobattaglino.mvvm.codebase.util.Constants.PAGE_SIZE
import kotlinx.android.synthetic.main.fragment_trending.*
import org.jetbrains.anko.intentFor
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class TrendingFragment : BaseFragment(), IncidentAdapter.OnViewHolderClick {

    private val mTag = javaClass.simpleName

    private val incidentsViewModel: IncidentsViewModel by viewModel()
    private val sp: SharedPreferenceUtils by inject()
    private lateinit var incidents: List<Incident>

    private lateinit var adapter: IncidentAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_trending, container, false)
    }

    override fun onPrimaryNavigationFragmentChanged(isPrimaryNavigationFragment: Boolean) {
        super.onPrimaryNavigationFragmentChanged(isPrimaryNavigationFragment)
        if (isPrimaryNavigationFragment) {
            incidentsViewModel.getTrendingIncidents(
                sp.getString(Arguments.ARG_LAST_KNOWN_LOCATION),
                PAGE_SIZE,
                null
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        trending_list.adapter = null
    }

    override fun observe() {
        observeTrendingIncidents()
    }

    private fun observeTrendingIncidents() {
        incidentsViewModel.trendingIncidentsUiData.observe(this, {
            it.error?.let { error ->
                handleError(mTag, error)
            }

            it.incidents?.let { incidents ->
                this.incidents = incidents
                empty_text.isVisible = incidents.isEmpty()
                adapter.mData = incidents
            }
        })
    }

    override fun setUpViews() {
        ViewCompat.setOnApplyWindowInsetsListener(trending_container) { _, insets ->
            trending_container.updatePadding(top = insets.stableInsetTop + 70)
            insets
        }

        incidentsViewModel.getTrendingIncidentsFromLocal()

        topAppBar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }

        adapter = IncidentAdapter(this)
        trending_list.adapter = adapter
        trending_list.layoutManager = LinearLayoutManager(
            context,
            RecyclerView.VERTICAL,
            false
        )
        trending_list.setHasFixedSize(false)

        nested_scroll_view.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, _, scrollY, _, oldScrollY ->
            if (v.getChildAt(v.childCount - 1) != null) {
                if (scrollY >= v.getChildAt(v.childCount - 1).measuredHeight - v.measuredHeight && scrollY > oldScrollY
                ) {
                    incidentsViewModel.getTrendingIncidents(
                        sp.getString(Arguments.ARG_LAST_KNOWN_LOCATION),
                        PAGE_SIZE,
                        incidents[incidents.lastIndex].updatedAt
                    )
                }
            }
        })
    }

    override fun dataViewClickFromList(view: View, position: Int, data: Incident) {
        adapter.popupWindow?.dismiss()
        when (view.id) {
            R.id.item_incident_live_container -> {
                if (data.videoStream == null) {
                    context?.startActivity(
                        context?.intentFor<IncidentDetailActivity>(
                            Arguments.ARG_INCIDENT_ID to data.id
                        )
                    )
                } else {

                }
            }
            R.id.layout_reaction_1 -> {
                incidentsViewModel.addReaction(
                    AddReactionRequest(
                        AddReactionRequest.FEARFUL_FACE,
                        data.id
                    )
                )
            }
            R.id.layout_reaction_2 -> {
                incidentsViewModel.addReaction(
                    AddReactionRequest(
                        AddReactionRequest.HANDS_PRESSED_TOGETHER,
                        data.id
                    )
                )
            }
            R.id.layout_reaction_3 -> {
                incidentsViewModel.addReaction(
                    AddReactionRequest(
                        AddReactionRequest.RED_ANGRY_FACE,
                        data.id
                    )
                )
            }
            R.id.layout_reaction_4 -> {
                incidentsViewModel.addReaction(
                    AddReactionRequest(
                        AddReactionRequest.RED_HEART,
                        data.id
                    )
                )
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(
            mTag,
            "onActivityResult requestCode: $requestCode, resultCode: $resultCode, data: $data"
        )
        if (requestCode == Arguments.LIVE_REQ_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                when (data?.extras?.getString(Arguments.ARG_STOP_BROADCAST)) {
                    WebSocketMessage.MESSAGE_STOP_HQ -> {
                        Log.d(mTag, "onActivityResult go to stop hq")
                        findNavController().navigate(R.id.trending_to_stoped)
                    }
                    // TODO check if we need to redirect to a fragment here
                    /*WebSocketMessage.MESSAGE_STOP_USER -> {
                        Log.d(mTag, "onActivityResult go to stop user")
                        findNavController().navigate(R.id.broadcast_to_ended)
                    }*/
                }
            }
        }
    }
}