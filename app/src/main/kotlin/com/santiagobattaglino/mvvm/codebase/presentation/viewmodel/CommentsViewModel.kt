package com.santiagobattaglino.mvvm.codebase.presentation.viewmodel

import com.santiagobattaglino.mvvm.codebase.data.network.error.ErrorObject
import com.santiagobattaglino.mvvm.codebase.data.repository.comment.CommentRepo
import com.santiagobattaglino.mvvm.codebase.domain.entity.Comment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CommentsViewModel(
    private val repo: CommentRepo
) : ViewModel() {

    val commentsUiData = MutableLiveData<ResultComments>()
    val commentUiData = MutableLiveData<ResultComment>()
    val deleteCommentUiData = MutableLiveData<ResultDeleteComment>()
    val reportCommentUiData = MutableLiveData<ResultComment>()

    fun getComments(incidentId: Int) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                repo.getComments(incidentId)
            }

            result.comments?.let {
                commentsUiData.value =
                    ResultComments(it)
            }

            result.error?.let {
                commentsUiData.value =
                    ResultComments(error = it)
            }
        }
    }

    fun createComment(comment: Comment) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                repo.createComment(comment)
            }

            result.comment?.let {
                commentUiData.value = ResultComment(it)
            }

            result.error?.let {
                commentUiData.value = ResultComment(error = it)
            }
        }
    }

    fun deleteComment(incidentId: Int, comment: Comment) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                repo.deleteComment(incidentId, comment)
            }

            result.int?.let {
                deleteCommentUiData.value = ResultDeleteComment(it)
            }

            result.error?.let {
                deleteCommentUiData.value = ResultDeleteComment(error = it)
            }
        }
    }

    fun reportComment(incidentId: Int, commentId: Int) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                repo.reportComment(incidentId, commentId)
            }

            result.comment?.let {
                reportCommentUiData.value = ResultComment(it)
            }

            result.error?.let {
                reportCommentUiData.value = ResultComment(error = it)
            }
        }
    }
}

data class ResultComments(val comments: List<Comment>? = null, val error: ErrorObject? = null)
data class ResultComment(val comment: Comment? = null, val error: ErrorObject? = null)
data class ResultDeleteComment(val int: Int? = null, val error: ErrorObject? = null)