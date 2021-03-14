package com.santiagobattaglino.mvvm.codebase.data.room.dao

import androidx.room.*
import com.santiagobattaglino.mvvm.codebase.domain.entity.Incident
import com.santiagobattaglino.mvvm.codebase.domain.entity.Product
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDAO {

    @Delete
    suspend fun delete(data: Product)

    @Query("DELETE FROM products")
    suspend fun deleteAll()

    @Query("DELETE FROM products WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(data: Product)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveList(dataList: List<Product>)

    @Query("SELECT * FROM products WHERE id = :id")
    suspend fun get(id: Int): Product

    @Query("SELECT * FROM products ORDER BY name DESC")
    suspend fun getList(): List<Product>

    @Query("SELECT * FROM products ORDER BY id DESC")
    fun getListFlow(): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE id = :id")
    fun getFlow(id: Int): Flow<Product>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(data: Product)
}