package com.santiagobattaglino.mvvm.codebase.domain.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val id: Int = 0,
    val role: String = "",
    val firstName: String = "",
    val lastName: String? = null,
    val address: String? = null,
    val phone: String? = null,
    val email: String? = null,
    val link: String? = null,
    val notes: String? = null,
    val dateCreated: String = ""
) : Parcelable {
    companion object {
        const val ROLE_PROVEEDOR = "PROVEEDOR"
        const val RATE_CLIENTE = "CLIENTE"
        const val RATE_ADMIN = "ADMIN"
        const val RATE_VENDEDOR = "VENDEDOR"
    }
}