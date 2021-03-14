package com.santiagobattaglino.mvvm.codebase.presentation.ui.stock.adapter

import android.content.Context
import android.location.Location
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.recyclerview.widget.RecyclerView
import com.santiagobattaglino.mvvm.codebase.R
import com.santiagobattaglino.mvvm.codebase.domain.entity.Stock
import com.santiagobattaglino.mvvm.codebase.presentation.ui.AutoUpdatableAdapter
import kotlinx.android.synthetic.main.item_incident_normal.view.*
import kotlin.properties.Delegates

class StockAdapter(
    private val onViewHolderClick: OnViewHolderClick?
) : RecyclerView.Adapter<StockAdapter.ViewHolder>(), AutoUpdatableAdapter {

    companion object {
        const val VIEW_TYPE_NORMAL = 0
        const val VIEW_TYPE_LIVE = 1
        const val VIEW_TYPE_HEADER = 2
    }

    var lastKnownLocation: Location? = null
    var popupWindow: PopupWindow? = null

    var mData: List<Stock> by Delegates.observable(emptyList()) { _, oldList, newList ->
        autoNotify(oldList, newList) { o, n -> o.id == n.id }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            createView(parent.context, parent, viewType),
            onViewHolderClick
        )
    }

    override fun getItemViewType(position: Int): Int {
        val stock = getItem(position)
        if (stock.id == -1) {
            return VIEW_TYPE_HEADER
        }

        return if (stock.quantity > 5) {
            VIEW_TYPE_NORMAL
        } else {
            VIEW_TYPE_NORMAL
        }
    }

    override fun getItemCount() = mData.size

    private fun getItem(index: Int): Stock {
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
        fun dataViewClickFromList(view: View, position: Int, data: Stock)
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

        fun bind(stock: Stock) = with(itemView) {
            when (itemViewType) {
                VIEW_TYPE_NORMAL -> {
                    setStockNormal(stock, this)
                }
                VIEW_TYPE_LIVE -> {
                    setStockNormal(stock, this)
                }
            }
        }

        private fun setStockNormal(stock: Stock, view: View) {
            view.normal_title.text = stock.quantity.toString()
            /*view.normal_title.text = incident.title
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
            }*/
        }
    }
}