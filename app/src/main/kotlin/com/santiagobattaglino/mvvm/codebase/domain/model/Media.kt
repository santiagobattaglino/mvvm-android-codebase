package com.santiagobattaglino.mvvm.codebase.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Media(
    val id: Int? = null,
    val typeContent: String = "",
    val url: String? = null,
    val thumbnailUrl: String? = null,
    val videoStream: VideoStream? = null
) : Parcelable {
    companion object {
        const val TYPE_CONTENT_MAP = "map"
        const val TYPE_CONTENT_PHOTO = "image"
        const val TYPE_CONTENT_VIDEO = "video"
        const val TYPE_CONTENT_CLOUD_RECORDING = "cloudRecording"
        const val TYPE_CONTENT_FILE = "file"
    }
}