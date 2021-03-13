package com.santiagobattaglino.mvvm.codebase.data.repository.comment

import com.santiagobattaglino.mvvm.codebase.domain.entity.Comment
import com.santiagobattaglino.mvvm.codebase.presentation.viewmodel.ResultComment
import com.santiagobattaglino.mvvm.codebase.presentation.viewmodel.ResultComments
import com.santiagobattaglino.mvvm.codebase.presentation.viewmodel.ResultDeleteComment

interface CommentRepo {
    suspend fun getComments(incidentId: Int): ResultComments
    suspend fun saveCommentsLocal(comments: List<Comment>)
    suspend fun createComment(commentInput: Comment): ResultComment
    suspend fun deleteComment(incidentId: Int, comment: Comment): ResultDeleteComment
    suspend fun reportComment(incidentId: Int, commentId: Int): ResultComment
}