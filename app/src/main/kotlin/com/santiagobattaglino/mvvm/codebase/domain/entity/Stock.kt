package com.santiagobattaglino.mvvm.codebase.domain.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "stock")
data class Stock(
    @PrimaryKey
    val id: Int = 0,
    val productId: Int = 0,
    val quantity: Int = 0,
    var userId: Int = 0
) : Parcelable