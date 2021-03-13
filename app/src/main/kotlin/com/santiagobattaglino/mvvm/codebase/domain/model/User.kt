package com.santiagobattaglino.mvvm.codebase.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    val id: String = "",
    val name: String = "",
    val lastName: String = "",
    val email: String = "",
    val isPrivateAccount: Boolean = false,
    val thumbnail: String? = null
) : Parcelable