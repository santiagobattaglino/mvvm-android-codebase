package com.santiagobattaglino.mvvm.codebase.domain.entity

import com.santiagobattaglino.mvvm.codebase.domain.model.User
import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "comments")
data class Comment(
    @PrimaryKey
    val id: Int = 0,
    val at: String = "",
    val body: String = "",
    @Embedded(prefix = "user_")
    val user: User? = null,
    var incidentId: Int = 0,
    val isReported: Boolean = false
) : Parcelable