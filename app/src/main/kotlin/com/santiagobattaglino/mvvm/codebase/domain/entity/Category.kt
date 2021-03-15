package com.santiagobattaglino.mvvm.codebase.domain.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "categories")
data class Category(
    @PrimaryKey
    var id: Int = 0,
    var name: String = "",
    var dateCreated: String = "",
    @Ignore
    var isSelected: Boolean = false
) : Parcelable