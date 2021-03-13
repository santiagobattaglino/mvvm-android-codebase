package com.santiagobattaglino.mvvm.codebase.domain.model

import android.os.Parcelable
import androidx.room.Embedded
import kotlinx.android.parcel.Parcelize

@Parcelize
class RateApp(
    val id: Int? = null,
    val at: String? = null,
    val typeRating: String? = null,
    @Embedded(prefix = "user_")
    val user: User? = null
) : Parcelable {
    companion object {
        const val RATE_POSITIVE = "positive"
        const val RATE_NEGATIVE = "negative"
    }
}