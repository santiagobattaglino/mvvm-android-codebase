package com.santiagobattaglino.mvvm.codebase.presentation.ui.comments

import com.santiagobattaglino.mvvm.codebase.domain.entity.Comment
import com.santiagobattaglino.mvvm.codebase.presentation.ui.AutoUpdatableAdapter
import com.santiagobattaglino.mvvm.codebase.util.GlideUrlCustomCacheKey
import com.santiagobattaglino.mvvm.codebase.util.getAtLocalTime
import android.content.Context
import android.location.Location
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import com.santiagobattaglino.mvvm.codebase.R
import com.santiagobattaglino.mvvm.codebase.util.GlideApp
import kotlinx.android.synthetic.main.card_comment.view.*
import kotlin.properties.Delegates


class CommentAdapter(
    private val onViewHolderClick: OnViewHolderClick?,
    private val userId: String?
) : RecyclerView.Adapter<CommentAdapter.ViewHolder>(), AutoUpdatableAdapter {

    companion object {
        const val VIEW_TYPE_NORMAL = 0
    }

    var lastKnownLocation: Location? = null

    var mData: List<Comment> by Delegates.observable(emptyList()) { _, oldList, newList ->
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

    private fun getItem(index: Int): Comment {
        return mData[index]
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mData[position])
    }

    private fun createView(context: Context, viewGroup: ViewGroup, viewType: Int): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        return when (viewType) {
            VIEW_TYPE_NORMAL -> inflater.inflate(R.layout.card_comment, viewGroup, false)
            else -> {
                inflater.inflate(R.layout.card_comment, viewGroup, false)
            }
        }
    }

    interface OnViewHolderClick {
        fun dataViewClickFromList(view: View, position: Int, data: Comment)
    }

    inner class ViewHolder(
        itemView: View,
        viewHolderClickListener: OnViewHolderClick?
    ) :
        RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        /*init {
            if (viewHolderClickListener != null)
                itemView.setOnClickListener(this)
        }*/

        override fun onClick(view: View) {
            onViewHolderClick?.dataViewClickFromList(
                view,
                adapterPosition,
                getItem(adapterPosition)
            )
        }

        fun bind(comment: Comment) = with(itemView) {
            if (itemViewType == VIEW_TYPE_NORMAL) {
                comment.user?.let { user ->

                    user.thumbnail?.let {
                        context?.let { context ->
                            GlideApp.with(context)
                                .load(GlideUrlCustomCacheKey(it))
                                .apply(RequestOptions.circleCropTransform())
                                .into(dot)
                        }
                    }

                    if (userId == user.id) {
                        menu_layout.setOnClickListener {
                            val popup = PopupMenu(context, menu)
                            popup.menuInflater.inflate(R.menu.delete_comment_menu, popup.menu)
                            popup.setOnMenuItemClickListener { item ->
                                when (item.itemId) {
                                    R.id.delete_comment -> {
                                        onViewHolderClick?.dataViewClickFromList(
                                            itemView,
                                            adapterPosition,
                                            getItem(adapterPosition)
                                        )
                                    }
                                }
                                true
                            }
                            popup.show()
                        }
                    } else {
                        menu_layout.setOnClickListener {
                            val popup = PopupMenu(context, menu)
                            popup.menuInflater.inflate(R.menu.report_comment_menu, popup.menu)
                            popup.setOnMenuItemClickListener { item ->
                                when (item.itemId) {
                                    R.id.report_comment -> {
                                        onViewHolderClick?.dataViewClickFromList(
                                            itemView,
                                            adapterPosition,
                                            getItem(adapterPosition)
                                        )
                                    }
                                }
                                true
                            }
                            popup.show()
                        }
                    }

                    if (user.isPrivateAccount) {
                        comment_user_name.text = context.getString(R.string.anonymous)
                    } else {
                        comment_user_name.text = user.name
                    }
                }

                comment_time.text = getAtLocalTime(comment.at)
                comment_title.text = comment.body
            }
        }
    }
}