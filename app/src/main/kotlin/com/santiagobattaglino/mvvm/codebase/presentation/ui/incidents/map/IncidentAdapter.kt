package com.santiagobattaglino.mvvm.codebase.presentation.ui.incidents.map

import com.santiagobattaglino.mvvm.codebase.domain.entity.Incident
import com.santiagobattaglino.mvvm.codebase.presentation.ui.AutoUpdatableAdapter
import com.santiagobattaglino.mvvm.codebase.presentation.ui.comments.CommentsActivity
import android.content.Context
import android.location.Location
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import com.santiagobattaglino.mvvm.codebase.BuildConfig
import com.santiagobattaglino.mvvm.codebase.R
import com.santiagobattaglino.mvvm.codebase.util.*
import kotlinx.android.synthetic.main.include_incident_bottom_bar.view.*
import kotlinx.android.synthetic.main.include_layout_comments.view.*
import kotlinx.android.synthetic.main.include_layout_reactions.view.*
import kotlinx.android.synthetic.main.item_incident_live.view.*
import kotlinx.android.synthetic.main.item_incident_normal.view.*
import kotlinx.android.synthetic.main.popup_reactions.view.*
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.share
import org.ocpsoft.prettytime.PrettyTime
import kotlin.properties.Delegates

class IncidentAdapter(
    private val onViewHolderClick: OnViewHolderClick?
) : RecyclerView.Adapter<IncidentAdapter.ViewHolder>(), AutoUpdatableAdapter {

    companion object {
        const val VIEW_TYPE_NORMAL = 0
        const val VIEW_TYPE_LIVE = 1
        const val VIEW_TYPE_HEADER = 2
    }

    var lastKnownLocation: Location? = null
    var popupWindow: PopupWindow? = null

    var mData: List<Incident> by Delegates.observable(emptyList()) { _, oldList, newList ->
        autoNotify(oldList, newList) { o, n -> o.id == n.id }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            createView(parent.context, parent, viewType),
            onViewHolderClick
        )
    }

    override fun getItemViewType(position: Int): Int {
        val incident = getItem(position)
        if (incident.id == -1) {
            return VIEW_TYPE_HEADER
        }

        return if (incident.videoStream != null) {
            VIEW_TYPE_LIVE
        } else {
            VIEW_TYPE_NORMAL
        }
    }

    override fun getItemCount() = mData.size

    private fun getItem(index: Int): Incident {
        return mData[index]
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mData[position])
    }

    private fun createView(context: Context, viewGroup: ViewGroup, viewType: Int): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        return when (viewType) {
            VIEW_TYPE_NORMAL -> inflater.inflate(R.layout.item_incident_normal, viewGroup, false)
            VIEW_TYPE_LIVE -> inflater.inflate(R.layout.item_incident_live, viewGroup, false)
            VIEW_TYPE_HEADER -> inflater.inflate(R.layout.item_incident_header, viewGroup, false)
            else -> {
                inflater.inflate(R.layout.item_incident_normal, viewGroup, false)
            }
        }
    }

    interface OnViewHolderClick {
        fun dataViewClickFromList(view: View, position: Int, data: Incident)
    }

    inner class ViewHolder(
        itemView: View,
        viewHolderClickListener: OnViewHolderClick?
    ) :
        RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        init {
            if (viewHolderClickListener != null)
                itemView.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            if (adapterPosition >= 0) {
                onViewHolderClick?.dataViewClickFromList(
                    view,
                    adapterPosition,
                    getItem(adapterPosition)
                )
            }
        }

        fun bind(incident: Incident) = with(itemView) {
            when (itemViewType) {
                VIEW_TYPE_NORMAL -> {
                    setIncidentNormal(incident, this)
                }
                VIEW_TYPE_LIVE -> {
                    setIncidentLive(incident, this)
                }
            }
        }

        private fun setIncidentNormal(incident: Incident, view: View) {
            view.normal_title.text = incident.title
            view.normal_time.text = String.format(
                " · %s %s",
                view.resources.getString(R.string.updated),
                PrettyTime().format(incident.updatedAt.toDate())
            )

            view.normal_ocurred_time.isVisible = true
            view.normal_ocurred_time.text = String.format(
                "%s %s",
                view.resources.getString(R.string.occurred_on),
                getAtLocalTime(incident.at)
            )

            setIncidentDistance(incident, view.normal_distance, false)
            view.normal_address.text = incident.address
            view.normal_description.text = incident.description

            if (incident.thumbnail != null) {
                view.normal_image.visibility = View.VISIBLE
                view.normal_image.visibility = View.VISIBLE
                GlideApp.with(view.context)
                    .load(GlideUrlCustomCacheKey(incident.thumbnail))
                    .apply(RequestOptions.centerCropTransform())
                    .into(view.normal_image)
            } else {
                view.normal_image.visibility = View.GONE
            }

            view.context.let {
                setUpComments(incident, view, it)
                setUpReactions(incident, view, it)
                setUpShare(incident, view, it)
            }
        }

        private fun setIncidentLive(incident: Incident, view: View) {
            setIncidentDistance(incident, view.live_distance, true)
            view.live_time.text = String.format(
                " · %s %s",
                view.resources.getString(R.string.updated),
                PrettyTime().format(incident.updatedAt.toDate())
            )

            view.normal_ocurred_time.isVisible = true
            view.normal_ocurred_time.text = String.format(
                "%s %s",
                view.resources.getString(R.string.occurred_on),
                getAtLocalTime(incident.at)
            )

            view.live_title.text = incident.title
            view.live_address.text = incident.address
            view.live_description.text = incident.description
            view.context.let {
                setUpComments(incident, view, it)
                setUpReactions(incident, view, it)
                setUpShare(incident, view, it)
            }
        }

        private fun setIncidentDistance(incident: Incident, view: TextView, isLive: Boolean) {
            incident.distance?.let {
                if (isLive) {
                    view.text = String.format(" · %s", it)
                } else {
                    view.text = it
                }
            } ?: run {
                lastKnownLocation?.let {
                    val distance = "0"
                    if (isLive) {
                        view.text = String.format(" · %s", distance)
                    } else {
                        view.text = distance
                    }
                }
            }
        }

        private fun setUpShare(incident: Incident, view: View, context: Context) {
            view.share_layout.setOnClickListener {
                val time = getAtLocalTime(incident.at)
                context.share(
                    "${incident.title}\n" +
                            "${incident.description}\n" +
                            "${incident.address}\n" +
                            "$time\n" +
                            "\n" +
                            "${BuildConfig.HQ_BASE_URL}/#/deeplinks/incidents/${incident.id}"
                )
            }
        }

        private fun setUpReactions(incident: Incident, view: View, context: Context) {
            view.default_reaction.text = getMostSelectedReaction(context, incident)
            view.reactions.text = getReactionsCount(incident).toString()
            view.reactions_layout.setOnClickListener {
                val inflater: LayoutInflater =
                    view.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val popupWindowView = inflater.inflate(R.layout.popup_reactions, null)

                popupWindow = PopupWindow(
                    popupWindowView,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                popupWindow?.isOutsideTouchable = true
                popupWindow?.showAsDropDown(view.reactions_layout, 16, -100)

                popupWindowView.close.setOnClickListener {
                    popupWindow?.dismiss()
                }

                popupWindowView.reaction1_count.text = incident.fearfulFaceReactions.toString()
                popupWindowView.layout_reaction_1.setOnClickListener(this)
                popupWindowView.reaction2_count.text = incident.handsPressedReactions.toString()
                popupWindowView.layout_reaction_2.setOnClickListener(this)
                popupWindowView.reaction3_count.text = incident.redAngryFaceReactions.toString()
                popupWindowView.layout_reaction_3.setOnClickListener(this)
                popupWindowView.reaction4_count.text = incident.redHeartReactions.toString()
                popupWindowView.layout_reaction_4.setOnClickListener(this)
            }
        }

        private fun setUpComments(
            incident: Incident,
            view: View,
            context: Context
        ) {
            view.comments.text = incident.commentsCount.toString()
            view.comments_layout.setOnClickListener {
                context.startActivity(
                    context.intentFor<CommentsActivity>(
                        Arguments.ARG_INCIDENT_TITLE to incident.title,
                        Arguments.ARG_INCIDENT_ID to incident.id
                    )
                )
            }
        }
    }
}