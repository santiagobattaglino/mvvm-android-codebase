package com.santiagobattaglino.mvvm.codebase.data.room.dao

import com.santiagobattaglino.mvvm.codebase.domain.entity.Update
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UpdateDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(data: Update)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveList(dataList: List<Update>)

    @Query("SELECT * FROM updates WHERE id = :id")
    suspend fun get(id: Int): Update

    @Query("SELECT * FROM updates WHERE incidentId = :incidentId ORDER BY at DESC")
    suspend fun getList(incidentId: Int): List<Update>

    @Query("DELETE FROM updates WHERE incidentId = :incidentId")
    suspend fun delete(incidentId: Int)
}