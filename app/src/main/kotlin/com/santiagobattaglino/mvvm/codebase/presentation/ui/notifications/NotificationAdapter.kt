package com.santiagobattaglino.mvvm.codebase.presentation.ui.notifications

import com.santiagobattaglino.mvvm.codebase.domain.entity.Notification
import com.santiagobattaglino.mvvm.codebase.presentation.ui.AutoUpdatableAdapter
import com.santiagobattaglino.mvvm.codebase.util.getAtLocalTime
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.santiagobattaglino.mvvm.codebase.R
import kotlinx.android.synthetic.main.item_notification.view.*
import kotlin.properties.Delegates

class NotificationAdapter(
    private val onViewHolderClick: OnViewHolderClick?,
) : RecyclerView.Adapter<NotificationAdapter.ViewHolder>(), AutoUpdatableAdapter {

    companion object {
        const val VIEW_TYPE_NORMAL = 0
    }

    var mData: List<Notification> by Delegates.observable(emptyList()) { _, oldList, newList ->
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

    private fun getItem(index: Int): Notification {
        return mData[index]
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mData[position])
    }

    private fun createView(context: Context, viewGroup: ViewGroup, viewType: Int): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        return when (viewType) {
            VIEW_TYPE_NORMAL -> inflater.inflate(R.layout.item_notification, viewGroup, false)
            else -> {
                inflater.inflate(R.layout.item_notification, viewGroup, false)
            }
        }
    }

    interface OnViewHolderClick {
        fun dataViewClickFromList(view: View, position: Int, data: Notification)
        fun deleteNotificationById(id: Int)
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

        fun bind(notification: Notification) = with(itemView) {
            when (itemViewType) {
                VIEW_TYPE_NORMAL -> {
                    setNotification(notification, this)
                }
            }
        }

        private fun setNotification(notification: Notification, view: View) {
            view.normal_close.setOnClickListener {
                onViewHolderClick?.deleteNotificationById(notification.id)
            }

            view.normal_title.text = notification.title
            view.normal_received_time.text = String.format(
                "%s %s",
                view.context.getString(R.string.occurred_on),
                getAtLocalTime(notification.at)
            )
            view.normal_description.text = notification.body
        }
    }
}