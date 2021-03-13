package com.santiagobattaglino.mvvm.codebase.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class VideoStream(
    val uid: Int = 0,
    val token: String = "",
    val channel: String = "",
    val sid: String = ""
) : Parcelable