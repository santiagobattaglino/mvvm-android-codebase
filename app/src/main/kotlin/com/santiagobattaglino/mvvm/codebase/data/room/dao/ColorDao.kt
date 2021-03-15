package com.santiagobattaglino.mvvm.codebase.data.room.dao

import androidx.room.*
import com.santiagobattaglino.mvvm.codebase.domain.entity.Color
import kotlinx.coroutines.flow.Flow

@Dao
interface ColorDAO {

    @Delete
    suspend fun delete(data: Color)

    @Query("DELETE FROM colors")
    suspend fun deleteAll()

    @Query("DELETE FROM colors WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(data: Color)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveList(dataList: List<Color>)

    @Query("SELECT * FROM colors WHERE id = :id")
    suspend fun get(id: Int): Color

    @Query("SELECT * FROM colors ORDER BY name")
    suspend fun getList(): List<Color>

    @Query("SELECT * FROM colors ORDER BY id DESC")
    fun getListFlow(): Flow<List<Color>>

    @Query("SELECT * FROM colors WHERE id = :id")
    fun getFlow(id: Int): Flow<Color>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(data: Color)
}