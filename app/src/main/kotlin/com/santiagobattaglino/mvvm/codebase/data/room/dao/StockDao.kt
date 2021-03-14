package com.santiagobattaglino.mvvm.codebase.data.room.dao

import androidx.room.*
import com.santiagobattaglino.mvvm.codebase.domain.entity.Stock
import kotlinx.coroutines.flow.Flow

@Dao
interface StockDAO {

    @Delete
    suspend fun delete(data: Stock)

    @Query("DELETE FROM stock")
    suspend fun deleteAll()

    @Query("DELETE FROM stock WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(data: Stock)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveList(dataList: List<Stock>)

    @Query("SELECT * FROM stock WHERE id = :id")
    suspend fun get(id: Int): Stock

    @Query("SELECT * FROM stock ORDER BY id DESC")
    suspend fun getList(): List<Stock>

    @Query("SELECT * FROM stock WHERE userId = :userId ORDER BY id DESC")
    fun getStockByUserFromLocal(userId: Int): Flow<List<Stock>>

    @Query("SELECT * FROM stock WHERE id = :id")
    fun getFlow(id: Int): Flow<Stock>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(data: Stock)
}