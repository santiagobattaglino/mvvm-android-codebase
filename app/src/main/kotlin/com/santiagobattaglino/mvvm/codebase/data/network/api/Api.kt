package com.santiagobattaglino.mvvm.codebase.data.network.api

import com.santiagobattaglino.mvvm.codebase.data.network.error.ErrorResponse
import com.santiagobattaglino.mvvm.codebase.domain.entity.Login
import com.haroldadmin.cnradapter.NetworkResponse
import com.santiagobattaglino.mvvm.codebase.domain.model.*
import io.github.wax911.library.annotation.GraphQuery
import io.github.wax911.library.model.body.GraphContainer
import io.github.wax911.library.model.request.QueryContainerBuilder
import okhttp3.MultipartBody
import retrofit2.http.*

interface Api {

    @POST("/auth/login")
    suspend fun postLogin(
        @Body loginRequest: LoginRequest
    ): NetworkResponse<Login, ErrorResponse>

    @POST("/auth/register")
    suspend fun postRegister(
        @Body request: RegisterRequest
    ): NetworkResponse<Login, ErrorResponse>

    @POST("/graphql")
    @GraphQuery("GetIncidents")
    @Headers("Content-Type: application/json")
    suspend fun getIncidents(
        @Header("Authorization") token: String,
        @Body body: QueryContainerBuilder
    ): NetworkResponse<GraphContainer<GetIncidentsResponse>, ErrorResponse>

    @POST("/graphql")
    @GraphQuery("GetIncidentById")
    @Headers("Content-Type: application/json")
    suspend fun getIncidentById(
        @Header("Authorization") token: String,
        @Body body: QueryContainerBuilder
    ): NetworkResponse<GraphContainer<GetIncidentByIdResponse>, ErrorResponse>

    @POST("/graphql")
    @GraphQuery("GetIncidentReactions")
    @Headers("Content-Type: application/json")
    suspend fun getIncidentReactions(
        @Header("Authorization") token: String,
        @Body body: QueryContainerBuilder
    ): NetworkResponse<GraphContainer<GetIncidentByIdResponse>, ErrorResponse>

    @POST("/graphql")
    @GraphQuery("CreateIncident")
    @Headers("Content-Type: application/json")
    suspend fun createIncident(
        @Header("Authorization") token: String,
        @Body body: QueryContainerBuilder
    ): NetworkResponse<GraphContainer<CreateIncidentResponse>, ErrorResponse>

    @Multipart
    @POST("api/incidents/{id}/addvideo")
    suspend fun addVideo(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Part video: MultipartBody.Part
    ): NetworkResponse<AddMediaResponse, ErrorResponse>

    @Multipart
    @POST("api/incidents/{id}/addphoto")
    suspend fun addPhoto(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Part photo: MultipartBody.Part,
        @Query("tWidth") tWidth: Int,
        @Query("tHeight") tHeight: Int
    ): NetworkResponse<AddMediaResponse, ErrorResponse>

    @POST("/graphql")
    @GraphQuery("GetComments")
    @Headers("Content-Type: application/json")
    suspend fun getComments(
        @Header("Authorization") token: String,
        @Body body: QueryContainerBuilder
    ): NetworkResponse<GraphContainer<GetCommentsResponse>, ErrorResponse>

    @POST("/graphql")
    @GraphQuery("GetUpdates")
    @Headers("Content-Type: application/json")
    suspend fun getUpdates(
        @Header("Authorization") token: String,
        @Body body: QueryContainerBuilder
    ): NetworkResponse<GraphContainer<GetUpdatesResponse>, ErrorResponse>

    @POST("/graphql")
    @GraphQuery("CreateComment")
    @Headers("Content-Type: application/json")
    suspend fun createComment(
        @Header("Authorization") token: String,
        @Body body: QueryContainerBuilder
    ): NetworkResponse<GraphContainer<CreateCommentResponse>, ErrorResponse>

    @POST("/graphql")
    @GraphQuery("DeleteComment")
    @Headers("Content-Type: application/json")
    suspend fun deleteCommentFromIncident(
        @Header("Authorization") token: String,
        @Body body: QueryContainerBuilder
    ): NetworkResponse<GraphContainer<DeleteCommentResponse>, ErrorResponse>

    @POST("/graphql")
    @GraphQuery("AddReaction")
    @Headers("Content-Type: application/json")
    suspend fun addReaction(
        @Header("Authorization") token: String,
        @Body body: QueryContainerBuilder
    ): NetworkResponse<GraphContainer<AddReactionResponse>, ErrorResponse>

    @POST("/graphql")
    @GraphQuery("UpdateUser")
    @Headers("Content-Type: application/json")
    suspend fun updateUser(
        @Header("Authorization") token: String,
        @Body body: QueryContainerBuilder
    ): NetworkResponse<GraphContainer<UpdateUserResponse>, ErrorResponse>

    @POST("/graphql")
    @GraphQuery("JoinLiveChannel")
    @Headers("Content-Type: application/json")
    suspend fun joinLiveChannel(
        @Header("Authorization") token: String,
        @Body body: QueryContainerBuilder
    ): NetworkResponse<GraphContainer<JoinLiveChannelResponse>, ErrorResponse>

    @POST("/api/incidents/playvideo")
    suspend fun postPlayVideo(
        @Header("Authorization") token: String,
        @Body playVideo: PlayVideo
    ): NetworkResponse<PlayVideo, ErrorResponse>

    @POST("/api/checkplace")
    suspend fun postCheckPlace(
        @Header("Authorization") token: String,
        @Body checkPlaceRequest: CheckPlace
    ): NetworkResponse<CheckPlace, ErrorResponse>

    @POST("/graphql")
    @GraphQuery("GetCategories")
    @Headers("Content-Type: application/json")
    suspend fun getCategories(
        @Header("Authorization") token: String,
        @Body body: QueryContainerBuilder
    ): NetworkResponse<GraphContainer<GetCategoriesResponse>, ErrorResponse>

    @POST("/graphql")
    @GraphQuery("SetReportedComment")
    @Headers("Content-Type: application/json")
    suspend fun setReportedComment(
        @Header("Authorization") token: String,
        @Body body: QueryContainerBuilder
    ): NetworkResponse<GraphContainer<ReportCommentResponse>, ErrorResponse>

    @POST("/graphql")
    @GraphQuery("CreateRateApp")
    @Headers("Content-Type: application/json")
    suspend fun createRateApp(
        @Header("Authorization") token: String,
        @Body body: QueryContainerBuilder
    ): NetworkResponse<GraphContainer<CreateRateAppResponse>, ErrorResponse>
}