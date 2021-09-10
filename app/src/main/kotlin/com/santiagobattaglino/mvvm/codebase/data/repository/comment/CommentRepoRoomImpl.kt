package com.santiagobattaglino.mvvm.codebase.data.repository.comment

import com.santiagobattaglino.mvvm.codebase.data.network.api.Api
import com.santiagobattaglino.mvvm.codebase.data.network.error.ErrorObject
import com.santiagobattaglino.mvvm.codebase.data.room.dao.CommentDAO
import com.santiagobattaglino.mvvm.codebase.data.room.dao.IncidentDAO
import com.santiagobattaglino.mvvm.codebase.data.room.dao.LoginDAO
import com.santiagobattaglino.mvvm.codebase.domain.entity.Comment
import com.santiagobattaglino.mvvm.codebase.domain.model.CreateCommentRequest
import com.santiagobattaglino.mvvm.codebase.presentation.viewmodel.ResultComment
import com.santiagobattaglino.mvvm.codebase.presentation.viewmodel.ResultComments
import com.santiagobattaglino.mvvm.codebase.presentation.viewmodel.ResultDeleteComment
import android.util.Log
import com.haroldadmin.cnradapter.NetworkResponse
import com.santiagobattaglino.mvvm.codebase.presentation.viewmodel.ResultColors
import io.github.wax911.library.model.request.QueryContainerBuilder

class CommentRepoRoomImpl(
    private val api: Api,
    private val loginDAO: LoginDAO,
    private val commentDAO: CommentDAO,
    private val incidentDAO: IncidentDAO
) : CommentRepo {

    private val tag = javaClass.simpleName

    override suspend fun getComments(incidentId: Int): ResultComments {
        val requestBody = QueryContainerBuilder()
        requestBody.putVariable("incidentId", incidentId)

        val token = "Bearer " + loginDAO.getToken()

        return when (val networkResponse = api.getComments(token, requestBody)) {
            is NetworkResponse.Success -> {
                val comments = networkResponse.body.data?.getCommentsByIncidentId
                comments?.let {
                    if (it.isNotEmpty()) {
                        it.forEach { comment ->
                            comment.incidentId = incidentId
                        }
                        saveCommentsLocal(it)
                        incidentDAO.updateIncidentCommentsCount(incidentId, it.size)
                    }
                }
                ResultComments(commentDAO.getList(incidentId), null)
            }
            is NetworkResponse.ServerError -> {
                Log.d(
                    tag,
                    ErrorObject(networkResponse.code, networkResponse.body?.message).toString()
                )
                ResultComments(
                    commentDAO.getList(incidentId),
                    ErrorObject(networkResponse.code, networkResponse.body?.message)
                )
            }
            is NetworkResponse.NetworkError -> {
                Log.d(tag, ErrorObject(0, networkResponse.error.message).toString())
                ResultComments(commentDAO.getList(incidentId), null)
            }
            is NetworkResponse.UnknownError -> {
                val error = ErrorObject(ErrorObject.UNKNOWN, "unknown error")
                Log.d(tag, error.toString())
                ResultComments(null, error)
            }
        }
    }

    override suspend fun createComment(commentInput: Comment): ResultComment {
        val token = "Bearer " + loginDAO.getToken()
        val requestBody = QueryContainerBuilder()
        requestBody.putVariable(
            "input", CreateCommentRequest(
                body = commentInput.body,
                incidentId = commentInput.incidentId
            )
        )

        return when (val networkResponse = api.createComment(token, requestBody)) {
            is NetworkResponse.Success -> {
                val response = networkResponse.body.data?.addCommentToIncident
                response?.let {
                    it.incidentId = commentInput.incidentId
                    commentDAO.save(it)
                    // TODO we need to implement this: https://developer.android.com/training/data-storage/room/relationships#one-to-many
                    incidentDAO.increaseCommentsCount(commentInput.incidentId)
                }
                ResultComment(response, null)
            }
            is NetworkResponse.ServerError -> {
                Log.d(
                    tag,
                    ErrorObject(networkResponse.code, networkResponse.body?.message).toString()
                )
                ResultComment(
                    null,
                    ErrorObject(networkResponse.code, networkResponse.body?.message)
                )
            }
            is NetworkResponse.NetworkError -> {
                Log.e(tag, ErrorObject(0, networkResponse.error.message).toString())
                ResultComment(
                    null,
                    ErrorObject(0, networkResponse.error.message)
                )
            }
            is NetworkResponse.UnknownError -> {
                val error = ErrorObject(ErrorObject.UNKNOWN, "unknown error")
                Log.d(tag, error.toString())
                ResultComment(null, error)
            }
        }
    }

    override suspend fun deleteComment(incidentId: Int, comment: Comment): ResultDeleteComment {
        val token = "Bearer " + loginDAO.getToken()
        val requestBody = QueryContainerBuilder()
        requestBody.putVariable(
            "incidentId", incidentId
        )
        requestBody.putVariable(
            "commentId", comment.id
        )

        return when (val networkResponse = api.deleteCommentFromIncident(token, requestBody)) {
            is NetworkResponse.Success -> {
                val response = networkResponse.body.data?.deleteCommentFromIncident
                response?.let {
                    commentDAO.delete(comment)
                    // TODO we need to implement this: https://developer.android.com/training/data-storage/room/relationships#one-to-many
                    incidentDAO.decreaseCommentsCount(incidentId)
                }
                ResultDeleteComment(response, null)
            }
            is NetworkResponse.ServerError -> {
                Log.d(
                    tag,
                    ErrorObject(networkResponse.code, networkResponse.body?.message).toString()
                )
                ResultDeleteComment(
                    null,
                    ErrorObject(networkResponse.code, networkResponse.body?.message)
                )
            }
            is NetworkResponse.NetworkError -> {
                Log.e(tag, ErrorObject(0, networkResponse.error.message).toString())
                ResultDeleteComment(
                    null,
                    ErrorObject(0, networkResponse.error.message)
                )
            }
            is NetworkResponse.UnknownError -> {
                val error = ErrorObject(ErrorObject.UNKNOWN, "unknown error")
                Log.d(tag, error.toString())
                ResultDeleteComment(null, error)
            }
        }
    }

    override suspend fun reportComment(incidentId: Int, commentId: Int): ResultComment {
        val token = "Bearer " + loginDAO.getToken()
        val requestBody = QueryContainerBuilder()
        requestBody.putVariable(
            "incidentId", incidentId
        )
        requestBody.putVariable(
            "commentId", commentId
        )

        return when (val networkResponse = api.setReportedComment(token, requestBody)) {
            is NetworkResponse.Success -> {
                val response = networkResponse.body.data?.setReportedComment
                response?.let {
                    commentDAO.setReportedComment(commentId)
                    incidentDAO.decreaseCommentsCount(incidentId)
                }
                ResultComment(commentDAO.get(commentId), null)
            }
            is NetworkResponse.ServerError -> {
                Log.d(
                    tag,
                    ErrorObject(networkResponse.code, networkResponse.body?.message).toString()
                )
                ResultComment(
                    null,
                    ErrorObject(networkResponse.code, networkResponse.body?.message)
                )
            }
            is NetworkResponse.NetworkError -> {
                Log.e(tag, ErrorObject(0, networkResponse.error.message).toString())
                ResultComment(
                    null,
                    ErrorObject(0, networkResponse.error.message)
                )
            }
            is NetworkResponse.UnknownError -> {
                val error = ErrorObject(ErrorObject.UNKNOWN, "unknown error")
                Log.d(tag, error.toString())
                ResultComment(null, error)
            }
        }
    }

    override suspend fun saveCommentsLocal(comments: List<Comment>) {
        commentDAO.saveList(comments)
    }
}