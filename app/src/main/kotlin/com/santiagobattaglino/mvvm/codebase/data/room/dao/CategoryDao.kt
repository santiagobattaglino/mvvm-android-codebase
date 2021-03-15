package com.santiagobattaglino.mvvm.codebase.data.room.dao

import androidx.room.*
import com.santiagobattaglino.mvvm.codebase.domain.entity.Category
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDAO {

    @Delete
    suspend fun delete(data: Category)

    @Query("DELETE FROM categories")
    suspend fun deleteAll()

    @Query("DELETE FROM categories WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(data: Category)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveList(dataList: List<Category>)

    @Query("SELECT * FROM categories WHERE id = :id")
    suspend fun get(id: Int): Category

    @Query("SELECT * FROM categories ORDER BY name")
    suspend fun getList(): List<Category>

    @Query("SELECT * FROM categories ORDER BY id DESC")
    fun getListFlow(): Flow<List<Category>>

    @Query("SELECT * FROM categories WHERE id = :id")
    fun getFlow(id: Int): Flow<Category>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(data: Category)
}