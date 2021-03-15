package com.santiagobattaglino.mvvm.codebase.domain.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "colors")
data class Color(
    @PrimaryKey
    val id: Int = 0,
    val name: String = "",
    val dateCreated: String = ""
) : Parcelable