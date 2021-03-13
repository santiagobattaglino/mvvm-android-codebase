package com.santiagobattaglino.mvvm.codebase.data.room.dao

import com.santiagobattaglino.mvvm.codebase.domain.entity.Incident
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface IncidentDAO {

    @Delete
    suspend fun delete(incident: Incident)

    @Query("DELETE FROM incidents")
    suspend fun deleteAll()

    @Query("DELETE FROM incidents WHERE video_stream_channel IS NOT NULL")
    suspend fun deleteAllTrendingIncidents()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(data: Incident)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveList(dataList: List<Incident>)

    @Query("SELECT * FROM incidents WHERE id = :id")
    suspend fun get(id: Int): Incident

    @Query("SELECT * FROM incidents ORDER BY updatedAt DESC")
    suspend fun getList(): List<Incident>

    @Query("SELECT * FROM incidents ORDER BY updatedAt DESC")
    fun getListFlow(): Flow<List<Incident>>

    @Query("SELECT * FROM incidents WHERE id = :id")
    fun getFlow(id: Int): Flow<Incident>

    @Query("UPDATE incidents SET commentsCount = :commentsCount WHERE id = :incidentId")
    suspend fun updateIncidentCommentsCount(incidentId: Int, commentsCount: Int)

    @Query("UPDATE incidents SET commentsCount = commentsCount+1 WHERE id = :incidentId")
    suspend fun increaseCommentsCount(incidentId: Int)

    @Query("UPDATE incidents SET commentsCount = commentsCount-1 WHERE id = :incidentId")
    suspend fun decreaseCommentsCount(incidentId: Int)

    @Query("SELECT COUNT(id) FROM comments WHERE isReported = 0 AND incidentId = :incidentId")
    suspend fun getCommentsCount(incidentId: Int): Int

    @Query("UPDATE incidents SET fearfulFaceReactions = :fearfulFaceReactions, handsPressedReactions = :handsPressedReactions, redAngryFaceReactions = :redAngryFaceReactions, redHeartReactions = :redHeartReactions WHERE id = :incidentId")
    suspend fun updateIncidentReactions(
        fearfulFaceReactions: Int,
        handsPressedReactions: Int,
        redAngryFaceReactions: Int,
        redHeartReactions: Int,
        incidentId: Int
    )

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateIncident(incident: Incident)

    @Query("SELECT * FROM incidents WHERE video_stream_channel IS NOT NULL ORDER BY updatedAt DESC")
    fun getTrendingIncidents(): Flow<List<Incident>>

    @Query("UPDATE incidents SET video_stream_uid = null, video_stream_token = null, video_stream_channel = null, video_stream_sid = null WHERE id = :incidentId")
    suspend fun setLiveStopped(incidentId: Int)

    @Query("DELETE FROM incidents WHERE id = :incidentId")
    suspend fun deleteIncident(incidentId: Int)
}