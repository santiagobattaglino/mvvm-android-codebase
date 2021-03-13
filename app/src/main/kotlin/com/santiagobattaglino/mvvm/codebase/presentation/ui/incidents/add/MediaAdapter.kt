package com.santiagobattaglino.mvvm.codebase.presentation.ui.incidents.add

import com.santiagobattaglino.mvvm.codebase.domain.model.Media
import com.santiagobattaglino.mvvm.codebase.presentation.ui.AutoUpdatableAdapter
import com.santiagobattaglino.mvvm.codebase.util.GlideUrlCustomCacheKey
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import com.santiagobattaglino.mvvm.codebase.R
import com.santiagobattaglino.mvvm.codebase.util.GlideApp
import kotlinx.android.synthetic.main.item_media_photo.view.*
import kotlin.properties.Delegates

class MediaAdapter(
    private val onViewHolderClick: OnViewHolderClick?,
    private val isFromDetail: Boolean = false
) : RecyclerView.Adapter<MediaAdapter.ViewHolder>(), AutoUpdatableAdapter {

    companion object {
        const val VIEW_TYPE_MAP = 0
        const val VIEW_TYPE_PHOTO = 1
        const val VIEW_TYPE_VIDEO = 2
        const val VIEW_TYPE_CLOUD_RECORDING = 3
    }

    var mData: List<Media> by Delegates.observable(emptyList()) { _, oldList, newList ->
        autoNotify(oldList, newList) { o, n -> o.id == n.id }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            createView(parent.context, parent, viewType),
            onViewHolderClick
        )
    }

    override fun getItemViewType(position: Int): Int {
        val media = getItem(position)
        return when (media.typeContent) {
            Media.TYPE_CONTENT_MAP -> VIEW_TYPE_MAP
            Media.TYPE_CONTENT_PHOTO -> VIEW_TYPE_PHOTO
            Media.TYPE_CONTENT_VIDEO -> VIEW_TYPE_VIDEO
            Media.TYPE_CONTENT_CLOUD_RECORDING -> VIEW_TYPE_CLOUD_RECORDING
            else -> VIEW_TYPE_PHOTO
        }
    }

    override fun getItemCount() = mData.size

    private fun getItem(index: Int): Media {
        return mData[index]
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mData[position])
    }

    private fun createView(context: Context, viewGroup: ViewGroup, viewType: Int): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        return when (viewType) {
            VIEW_TYPE_MAP -> inflater.inflate(R.layout.item_media_map, viewGroup, false)
            VIEW_TYPE_PHOTO -> inflater.inflate(R.layout.item_media_photo, viewGroup, false)
            VIEW_TYPE_VIDEO -> inflater.inflate(R.layout.item_media_video, viewGroup, false)
            VIEW_TYPE_CLOUD_RECORDING -> inflater.inflate(
                R.layout.item_media_cloud_recording,
                viewGroup,
                false
            )
            else -> {
                inflater.inflate(R.layout.item_media_map, viewGroup, false)
            }
        }
    }

    interface OnViewHolderClick {
        fun dataViewClickFromList(view: View, position: Int, data: Media)
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

        fun bind(media: Media) = with(itemView) {
            when (itemViewType) {
                VIEW_TYPE_MAP -> {

                }
                VIEW_TYPE_PHOTO -> {
                    if (!isFromDetail) {
                        remove_media.setOnClickListener {
                            onViewHolderClick?.dataViewClickFromList(
                                it,
                                adapterPosition,
                                getItem(adapterPosition)
                            )
                        }
                        setPhotoLocal(media, this)
                    } else {
                        setPhoto(media, this)
                    }
                }
                VIEW_TYPE_VIDEO -> {
                    if (!isFromDetail) {
                        remove_media.setOnClickListener {
                            onViewHolderClick?.dataViewClickFromList(
                                it,
                                adapterPosition,
                                getItem(adapterPosition)
                            )
                        }
                        remove_media.isVisible = true
                    } else {
                        remove_media.isVisible = false
                    }
                }
                VIEW_TYPE_CLOUD_RECORDING -> {

                }
            }
        }

        private fun setPhotoLocal(media: Media, view: View) {
            GlideApp.with(view.context)
                .load(media.url)
                .apply(RequestOptions.centerCropTransform())
                .into(view.photo)
        }

        private fun setPhoto(media: Media, view: View) {
            view.remove_media.isVisible = false
            media.thumbnailUrl?.let {
                GlideApp.with(view.context)
                    .load(GlideUrlCustomCacheKey(it))
                    .apply(RequestOptions.centerCropTransform())
                    .into(view.photo)
            }
        }
    }
}