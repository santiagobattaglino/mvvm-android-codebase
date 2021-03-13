package com.santiagobattaglino.mvvm.codebase.data.room.dao

import com.santiagobattaglino.mvvm.codebase.domain.entity.Login
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface LoginDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveLogin(login: Login)

    @Query("SELECT * FROM login WHERE defaultUserId = 1")
    suspend fun getLogin(): Login?

    @Query("DELETE FROM login WHERE defaultUserId = :id")
    suspend fun deleteLogin(id: Int)

    @Query("SELECT token FROM login WHERE defaultUserId = 1")
    suspend fun getToken(): String

    @Query("UPDATE login SET isPrivateAccount = :privateAccount")
    suspend fun updateUserPrivateStatus(privateAccount: Boolean)
}