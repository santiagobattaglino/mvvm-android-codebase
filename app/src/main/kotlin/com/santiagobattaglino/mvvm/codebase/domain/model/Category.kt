package com.santiagobattaglino.mvvm.codebase.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Category(
    val id: Int = 0,
    val title: String = "",
    var isSelected: Boolean = false
) : Parcelable