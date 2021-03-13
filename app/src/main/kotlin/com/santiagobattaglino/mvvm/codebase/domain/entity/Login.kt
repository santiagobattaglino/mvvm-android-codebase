package com.santiagobattaglino.mvvm.codebase.domain.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

//@SuppressWarnings(RoomWarnings.PRIMARY_KEY_FROM_EMBEDDED_IS_DROPPED)
@Parcelize
@Entity(tableName = "login")
class Login(
    @PrimaryKey
    var id: String = "",
    var defaultUserId: String = "1", // We only have one row on this table
    val name: String = "",
    val lastName: String = "",
    val email: String = "",
    val photo: String? = null,
    val thumbnail: String? = null,
    val isPrivateAccount: Boolean = false,
    val token: String = "",
    var time: Long = 0
) : Parcelable {
    companion object {
        const val ROLE_USER = "user"
    }
}