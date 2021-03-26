package com.santiagobattaglino.mvvm.codebase.domain.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "products")
data class Product(
    @PrimaryKey
    val id: Int = 0,
    val catId: Int = 0,
    val name: String = "",
    val material: String = "",
    val colorId: Int = 0,
    val idMl: Int? = null,
    val priceId: Int = 0,
    val manufacturingCost: Int = 0,
    val notes: String? = null,
    val dateCreated: String = "",
    val categoryName: String? = null,
    val colorName: String? = null
) : Parcelable