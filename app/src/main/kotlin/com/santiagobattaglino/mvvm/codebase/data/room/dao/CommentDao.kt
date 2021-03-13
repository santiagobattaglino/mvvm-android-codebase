package com.santiagobattaglino.mvvm.codebase.data.room.dao

import com.santiagobattaglino.mvvm.codebase.domain.entity.Comment
import androidx.room.*

@Dao
interface CommentDAO {

    @Delete
    suspend fun delete(comment: Comment)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(data: Comment)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveList(dataList: List<Comment>)

    @Query("SELECT * FROM comments WHERE id = :id")
    suspend fun get(id: Int): Comment

    @Query("SELECT * FROM comments ORDER BY at DESC")
    suspend fun getList(): List<Comment>

    @Query("SELECT * FROM comments WHERE incidentId = :incidentId AND isReported = 0 ORDER BY at DESC")
    suspend fun getList(incidentId: Int): List<Comment>

    @Query("UPDATE comments SET isReported = 1 WHERE id = :commentId")
    fun setReportedComment(commentId: Int)
}