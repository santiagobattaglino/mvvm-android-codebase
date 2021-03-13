package com.santiagobattaglino.mvvm.codebase.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class CheckPlace(
    val isAllowed: Boolean? = false,
    val latitude: Double? = null,
    val longitude: Double? = null
) : Parcelable