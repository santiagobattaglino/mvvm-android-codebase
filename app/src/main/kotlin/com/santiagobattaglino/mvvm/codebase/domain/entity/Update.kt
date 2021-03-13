package com.santiagobattaglino.mvvm.codebase.domain.entity

import com.santiagobattaglino.mvvm.codebase.domain.model.User
import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "updates")
data class Update(
    @PrimaryKey
    val id: Int = 0,
    val at: String = "",
    val title: String = "",
    @Embedded(prefix = "user_")
    val user: User? = null,
    var incidentId: Int = 0
) : Parcelable