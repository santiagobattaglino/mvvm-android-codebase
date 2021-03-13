package com.santiagobattaglino.mvvm.codebase.presentation.ui.incidents.map

import com.santiagobattaglino.mvvm.codebase.data.repository.SharedPreferenceUtils
import com.santiagobattaglino.mvvm.codebase.domain.entity.Incident
import com.santiagobattaglino.mvvm.codebase.domain.model.AddReactionRequest
import com.santiagobattaglino.mvvm.codebase.presentation.ui.FullScreenDialog
import com.santiagobattaglino.mvvm.codebase.presentation.ui.base.BasePermissionFragment
import com.santiagobattaglino.mvvm.codebase.presentation.ui.comments.CommentsActivity
import com.santiagobattaglino.mvvm.codebase.presentation.ui.incidents.detail.IncidentDetailActivity
import com.santiagobattaglino.mvvm.codebase.presentation.viewmodel.IncidentsViewModel
import com.santiagobattaglino.mvvm.codebase.util.Constants.PAGE_SIZE
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.appbar.AppBarLayout
import com.santiagobattaglino.mvvm.codebase.BuildConfig
import com.santiagobattaglino.mvvm.codebase.R
import com.santiagobattaglino.mvvm.codebase.util.*
import kotlinx.android.synthetic.main.fragment_incidents.*
import kotlinx.android.synthetic.main.include_incident_bottom_bar.*
import kotlinx.android.synthetic.main.include_layout_comments.*
import kotlinx.android.synthetic.main.include_layout_reactions.*
import kotlinx.android.synthetic.main.item_incident_normal.*
import kotlinx.android.synthetic.main.popup_reactions.view.*
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.share
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.ocpsoft.prettytime.PrettyTime
import pub.devrel.easypermissions.EasyPermissions
import java.text.DateFormat
import java.util.*

class IncidentsFragment : BasePermissionFragment(), IncidentAdapter.OnViewHolderClick,
    FullScreenDialog.OnFullScreenDialogViewClickListener {

    private val mTag = javaClass.simpleName

    private val incidentsViewModel: IncidentsViewModel by viewModel()
    private val sp: SharedPreferenceUtils by inject()

    private var incidents = mutableListOf<Incident>()
    private var incident: Incident? = null
    private var lastKnownLocation: Location? = null
    private var lastKnownLocationTime: String? = null

    private lateinit var adapter: IncidentAdapter

    private var popupWindowView: View? = null
    private var popupWindow: PopupWindow? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_incidents, container, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        incidents_list.adapter = null
    }

    override fun onPrimaryNavigationFragmentChanged(isPrimaryNavigationFragment: Boolean) {
        super.onPrimaryNavigationFragmentChanged(isPrimaryNavigationFragment)
        if (isPrimaryNavigationFragment) {
            val location = sp.getString(Arguments.ARG_LAST_KNOWN_LOCATION)
            location?.let {
                incidentsViewModel.getIncidents(
                    location,
                    PAGE_SIZE,
                    null
                )
            }
        }
    }

    override fun setUpViews() {
        setUpAppBar()

        adapter = IncidentAdapter(this)
        incidents_list.adapter = adapter
        val layoutManager = LinearLayoutManager(
            context,
            RecyclerView.VERTICAL,
            false
        )
        incidents_list.layoutManager = layoutManager
        incidents_list.setHasFixedSize(false)

        nested_scroll_view.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, _, scrollY, _, oldScrollY ->
            if (v.getChildAt(v.childCount - 1) != null) {
                if (scrollY >= v.getChildAt(v.childCount - 1).measuredHeight - v.measuredHeight && scrollY > oldScrollY
                ) {
                    incidentsViewModel.getIncidents(
                        sp.getString(Arguments.ARG_LAST_KNOWN_LOCATION),
                        PAGE_SIZE,
                        incidents[incidents.lastIndex].updatedAt
                    )
                }
            }
        })
    }

    private fun observeIncidents() {
        incidentsViewModel.incidentsUiData.observe(this, {
            it.error?.let { error ->
                handleError(mTag, error)
            }

            it.incidents?.let { incidents ->
                this.incidents = incidents.toMutableList()

                // Mocked Incident for Header Type
                this.incidents.add(0, Incident(-1))

                adapter.mData = this.incidents.toList()
            }
        })
    }

    override fun observe() {
        observeIncidents()
        observeIncident()
        observeReactionsSingleIncident()
        observeLocation()
    }

    private fun observeLocation() {
        incidentsViewModel.locationUiData.observe(this, {
            it?.let {
                Log.d(
                    mTag,
                    "fusedLocationClient.lastLocation.addOnSuccessListener: $it"
                )

                lastKnownLocation = it
                sp.saveString(Arguments.ARG_LAST_KNOWN_LOCATION, "${it.latitude},${it.longitude}")
                lastKnownLocationTime = DateFormat.getTimeInstance().format(Date())
                adapter.lastKnownLocation = lastKnownLocation

                incidentsViewModel.getIncidents(
                    "${it.latitude},${it.longitude}",
                    PAGE_SIZE,
                    null
                )
                //adapter.notifyDataSetChanged()
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(
            mTag,
            "onActivityResult requestCode: $requestCode, resultCode: $resultCode, data: $data"
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            locationPermReq -> {
                if (hasLocationPermission()) {

                } else {
                    EasyPermissions.onRequestPermissionsResult(
                        requestCode,
                        permissions,
                        grantResults, this
                    )
                }
            }
        }
    }

    override fun dataViewClickFromList(view: View, position: Int, data: Incident) {
        adapter.popupWindow?.dismiss()
        when (view.id) {
            R.id.item_incident_normal_container -> context?.startActivity(
                context?.intentFor<IncidentDetailActivity>(
                    Arguments.ARG_INCIDENT_ID to data.id
                )
            )
            R.id.item_incident_live_container -> {

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

    private fun setUpAppBar() {
        val params = app_bar_layout.layoutParams as CoordinatorLayout.LayoutParams
        val behavior = AppBarLayout.Behavior()
        behavior.setDragCallback(object : AppBarLayout.Behavior.DragCallback() {
            override fun canDrag(appBarLayout: AppBarLayout): Boolean {
                return false
            }
        })
        context?.let {
            params.height = it.resources.displayMetrics.heightPixels - convertDpToPixel(110, it)
        }
        params.behavior = behavior
    }

    private fun observeIncident() {
        incidentsViewModel.incidentUiData.observe(this, {
            it.error?.let { error ->
                handleError(mTag, error)
            }

            it.incident?.let { incident ->
                this.incident = incident
                showIncidentCard(incident)
            }
        })
    }

    private fun observeReactionsSingleIncident() {
        incidentsViewModel.reactionsSingleUiData.observe(this, {
            it.error?.let { error ->
                handleError(mTag, error)
            }

            it.incident?.let { incident ->
                this.incident?.fearfulFaceReactions = incident.fearfulFaceReactions
                this.incident?.handsPressedReactions = incident.handsPressedReactions
                this.incident?.redAngryFaceReactions = incident.redAngryFaceReactions
                this.incident?.redHeartReactions = incident.redHeartReactions
                context?.let { context ->
                    setUpReactionsSingleIncident(context, incident)
                }
            }
        })
    }

    private fun showIncidentCard(incident: Incident) {
        normal_close.visibility = View.VISIBLE
        normal_close.setOnClickListener {
            hideCard()
        }
        //app_bar_layout.setExpanded(true, true)
        card_incident_layout.visibility = View.VISIBLE
        card_incident_layout.setOnClickListener {
            context?.startActivity(context?.intentFor<IncidentDetailActivity>(Arguments.ARG_INCIDENT_ID to incident.id))
        }

        normal_title.text = incident.title
        normal_time.text = String.format(
            " · %s %s",
            getString(R.string.updated),
            PrettyTime().format(incident.updatedAt.toDate())
        )

        normal_ocurred_time.isVisible = true
        normal_ocurred_time.text = String.format(
            "%s %s",
            getString(R.string.occurred_on),
            getAtLocalTime(incident.at)
        )

        incident.distance?.let {
            normal_distance.text = it
        } ?: run {
            lastKnownLocation?.let {
                normal_distance.text = "0"
            }
        }

        normal_address.text = incident.address
        normal_description.text = incident.description

        if (incident.thumbnail != null) {
            normal_image.visibility = View.VISIBLE
            GlideApp.with(this)
                .load(GlideUrlCustomCacheKey(incident.thumbnail))
                .apply(RequestOptions.centerCropTransform())
                .into(normal_image)
        } else {
            normal_image.visibility = View.GONE
        }

        comments.text = incident.commentsCount.toString()
        comments_layout.setOnClickListener {
            context?.startActivity(
                context?.intentFor<CommentsActivity>(
                    Arguments.ARG_INCIDENT_TITLE to incident.title,
                    Arguments.ARG_INCIDENT_ID to incident.id
                )
            )
        }

        context?.let {
            setUpReactions(it, incident)
        }

        share_layout.setOnClickListener {
            val time = getAtLocalTime(incident.at)
            context?.share(
                "${incident.title}\n" +
                        "${incident.description}\n" +
                        "${incident.address}\n" +
                        "$time\n" +
                        "\n" +
                        "${BuildConfig.HQ_BASE_URL}/#/deeplinks/incidents/${incident.id}"
            )
        }
    }

    private fun setUpReactionsSingleIncident(context: Context, incident: Incident) {
        default_reaction.text = getMostSelectedReaction(context, incident)
        reactions.text = getReactionsCount(incident).toString()
        setPopupWindowReactions(
            popupWindow?.contentView,
            incident.fearfulFaceReactions,
            incident.handsPressedReactions,
            incident.redAngryFaceReactions,
            incident.redHeartReactions
        )
    }

    private fun setUpReactions(context: Context, incident: Incident) {
        default_reaction.text = getMostSelectedReaction(context, incident)
        reactions.text = getReactionsCount(incident).toString()
        reactions_layout.setOnClickListener {
            setUpReactionPopupWindow(incident.id)
            setPopupWindowReactions(
                popupWindowView,
                incident.fearfulFaceReactions,
                incident.handsPressedReactions,
                incident.redAngryFaceReactions,
                incident.redHeartReactions
            )
        }
    }

    @SuppressLint("InflateParams")
    private fun setUpReactionPopupWindow(incidentId: Int) {
        val inflater: LayoutInflater =
            context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        popupWindowView = inflater.inflate(R.layout.popup_reactions, null)

        popupWindow = PopupWindow(
            popupWindowView,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        popupWindow?.isOutsideTouchable = true
        popupWindow?.showAsDropDown(reactions_layout, 16, -100)

        popupWindowView?.close?.setOnClickListener {
            popupWindow?.dismiss()
        }

        popupWindowView?.layout_reaction_1?.setOnClickListener {
            popupWindow?.dismiss()
            incidentsViewModel.addReactionSingleIncident(
                AddReactionRequest(
                    AddReactionRequest.FEARFUL_FACE,
                    incidentId
                )
            )
        }
        popupWindowView?.layout_reaction_2?.setOnClickListener {
            popupWindow?.dismiss()
            incidentsViewModel.addReactionSingleIncident(
                AddReactionRequest(
                    AddReactionRequest.HANDS_PRESSED_TOGETHER,
                    incidentId
                )
            )
        }
        popupWindowView?.layout_reaction_3?.setOnClickListener {
            popupWindow?.dismiss()
            incidentsViewModel.addReactionSingleIncident(
                AddReactionRequest(
                    AddReactionRequest.RED_ANGRY_FACE,
                    incidentId
                )
            )
        }
        popupWindowView?.layout_reaction_4?.setOnClickListener {
            popupWindow?.dismiss()
            incidentsViewModel.addReactionSingleIncident(
                AddReactionRequest(
                    AddReactionRequest.RED_HEART,
                    incidentId
                )
            )
        }
    }

    private fun hideCard() {
        incident = null
        normal_close.visibility = View.GONE
        card_incident_layout.visibility = View.INVISIBLE
    }

    override fun onFullScreenDialogViewClicked(view: View) {

    }
}