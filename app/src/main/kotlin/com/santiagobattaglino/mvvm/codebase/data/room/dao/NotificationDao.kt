package com.santiagobattaglino.mvvm.codebase.data.room.dao

import com.santiagobattaglino.mvvm.codebase.domain.entity.Notification
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(data: Notification)

    @Query("SELECT * FROM notifications ORDER BY at DESC")
    fun getList(): Flow<List<Notification>>

    @Query("DELETE FROM notifications WHERE incidentId = :incidentId OR incidentId IS NULL")
    fun deleteNotificationByIncidentId(incidentId: String?)

    @Query("DELETE FROM notifications WHERE id = :id")
    fun deleteNotificationById(id: Int)
}