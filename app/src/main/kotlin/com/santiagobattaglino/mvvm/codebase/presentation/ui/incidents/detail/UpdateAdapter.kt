package com.santiagobattaglino.mvvm.codebase.presentation.ui.incidents.detail

import com.santiagobattaglino.mvvm.codebase.domain.entity.Update
import com.santiagobattaglino.mvvm.codebase.presentation.ui.AutoUpdatableAdapter
import com.santiagobattaglino.mvvm.codebase.util.getAtLocalTime
import android.content.Context
import android.location.Location
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.santiagobattaglino.mvvm.codebase.R
import kotlinx.android.synthetic.main.card_update.view.*
import kotlin.properties.Delegates

class UpdateAdapter(
    private val onViewHolderClick: OnViewHolderClick?
) : RecyclerView.Adapter<UpdateAdapter.ViewHolder>(), AutoUpdatableAdapter {

    companion object {
        const val VIEW_TYPE_NORMAL = 0
    }

    var lastKnownLocation: Location? = null

    var mData: List<Update> by Delegates.observable(emptyList()) { _, oldList, newList ->
        autoNotify(oldList, newList) { o, n -> o.id == n.id }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            createView(parent.context, parent, viewType),
            onViewHolderClick
        )
    }

    override fun getItemViewType(position: Int): Int {
        return VIEW_TYPE_NORMAL
    }

    override fun getItemCount() = mData.size

    private fun getItem(index: Int): Update {
        return mData[index]
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mData[position])
    }

    private fun createView(context: Context, viewGroup: ViewGroup, viewType: Int): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        return when (viewType) {
            VIEW_TYPE_NORMAL -> inflater.inflate(R.layout.card_update, viewGroup, false)
            else -> {
                inflater.inflate(R.layout.card_update, viewGroup, false)
            }
        }
    }

    interface OnViewHolderClick {
        fun dataViewClickFromList(view: View, position: Int, data: Update)
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
            onViewHolderClick?.dataViewClickFromList(
                view,
                adapterPosition,
                getItem(adapterPosition)
            )
        }

        fun bind(update: Update) = with(itemView) {
            if (itemViewType == VIEW_TYPE_NORMAL) {
                card_update.setOnClickListener {
                    //context?.startActivity(context?.intentFor<UpdateDetailActivity>(Arguments.ARG_Update to Update.id))
                }

                update.at.let {
                    update_time.text = getAtLocalTime(it)
                }
                update_title.text = update.title
            }
        }
    }
}