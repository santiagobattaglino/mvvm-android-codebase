package com.santiagobattaglino.mvvm.codebase.data.repository.incident

import android.util.Log
import com.haroldadmin.cnradapter.NetworkResponse
import com.santiagobattaglino.mvvm.codebase.data.network.api.Api
import com.santiagobattaglino.mvvm.codebase.data.network.error.ErrorObject
import com.santiagobattaglino.mvvm.codebase.data.room.dao.IncidentDAO
import com.santiagobattaglino.mvvm.codebase.data.room.dao.LoginDAO
import com.santiagobattaglino.mvvm.codebase.domain.entity.Incident
import com.santiagobattaglino.mvvm.codebase.domain.model.*
import com.santiagobattaglino.mvvm.codebase.presentation.viewmodel.*
import com.santiagobattaglino.mvvm.codebase.util.getCurrentTime
import io.github.wax911.library.model.request.QueryContainerBuilder
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class IncidentRepoRoomImpl(
    private val apiRest: Api,
    private val apiGraphql: Api,
    private val loginDAO: LoginDAO,
    private val incidentDAO: IncidentDAO
) : IncidentRepo {

    private val tag = javaClass.simpleName

    // TODO implement Paging 3 with Flow https://codelabs.developers.google.com/codelabs/android-paging/#5
    override suspend fun getIncidents(
        lastKnownLocation: String?,
        first: Int,
        baseKey: String?,
        onlyLive: Boolean?
    ): ResultIncidents {
        val requestBody = QueryContainerBuilder()
        requestBody.putVariable("first", first)
        requestBody.putVariable("baseKey", baseKey)
        requestBody.putVariable("onlyLive", onlyLive)

        val token = "Bearer " + loginDAO.getToken()

        return when (val networkResponse = apiGraphql.getIncidents(token, requestBody)) {
            is NetworkResponse.Success -> {
                val incidents = networkResponse.body.data?.getIncidents
                incidents?.let {
                    // TODO ASK backend to retrieve distance here, so we don't need to calculate this client side
                    lastKnownLocation?.let { lastKnownLocation ->
                        incidents.forEach { incident ->
                            if (incident.distance == null) {
                                val latLng = lastKnownLocation.split(",")
                            }
                        }
                        saveIncidentsLocal(it)
                    }
                }
                ResultIncidents(incidentDAO.getList(), null)
            }
            is NetworkResponse.ServerError -> {
                Log.d(
                    tag,
                    ErrorObject(networkResponse.code, networkResponse.body?.message).toString()
                )
                ResultIncidents(
                    incidentDAO.getList(),
                    ErrorObject(networkResponse.code, networkResponse.body?.message)
                )
            }
            is NetworkResponse.NetworkError -> {
                Log.d(tag, ErrorObject(0, networkResponse.error.message).toString())
                ResultIncidents(incidentDAO.getList(), null)
            }
        }
    }

    override suspend fun getIncidentReactions(
        id: Int
    ): ResultIncident {
        val requestBody = QueryContainerBuilder()
        requestBody.putVariable("id", id)

        val token = "Bearer " + loginDAO.getToken()

        return when (val networkResponse = apiGraphql.getIncidentReactions(token, requestBody)) {
            is NetworkResponse.Success -> {
                val incident = networkResponse.body.data?.getIncidentById
                val localIncident = incidentDAO.get(id)
                incident?.let {
                    localIncident.redAngryFaceReactions = it.redAngryFaceReactions
                    localIncident.redHeartReactions = it.redHeartReactions
                    localIncident.handsPressedReactions = it.handsPressedReactions
                    localIncident.fearfulFaceReactions = it.fearfulFaceReactions
                }
                updateIncidentReactions(localIncident)
                ResultIncident(localIncident, null)
            }
            is NetworkResponse.ServerError -> {
                Log.d(
                    tag,
                    ErrorObject(networkResponse.code, networkResponse.body?.message).toString()
                )
                ResultIncident(
                    incidentDAO.get(id),
                    ErrorObject(networkResponse.code, networkResponse.body?.message)
                )
            }
            is NetworkResponse.NetworkError -> {
                Log.d(tag, ErrorObject(0, networkResponse.error.message).toString())
                ResultIncident(incidentDAO.get(id), null)
            }
        }
    }

    override fun getIncidentsLocal(): Flow<List<Incident>> {
        return incidentDAO.getListFlow()
    }

    override fun getIncidentLocal(incidentId: Int): Flow<Incident> {
        return incidentDAO.getFlow(incidentId)
    }

    override suspend fun createIncident(incident: Incident): ResultIncident {
        val token = "Bearer " + loginDAO.getToken()
        val requestBody = QueryContainerBuilder()

        if (incident.at.isEmpty())
            incident.at = getCurrentTime()

        requestBody.putVariable(
            "input", CreateIncidentRequest(
                title = incident.title,
                description = incident.description,
                latitude = incident.latitude,
                longitude = incident.longitude,
                address = incident.address,
                city = incident.city,
                isStreamingLive = incident.isStreamingLive,
                incidentsCategoryId = incident.incidentsCategoryId ?: 1,
                at = incident.at
            )
        )

        return when (val networkResponse = apiGraphql.createIncident(token, requestBody)) {
            is NetworkResponse.Success -> {
                var incidentResponse = networkResponse.body.data?.createIncident
                val errors = networkResponse.body.errors
                var errorObject: ErrorObject? = null

                incidentResponse?.let {
                    saveIncidentLocal(it)
                }

                errors?.let {
                    if (errors.isNotEmpty()) {
                        incidentResponse = null
                        errorObject = ErrorObject(0, it[0].message)
                    }
                }

                ResultIncident(incidentResponse, errorObject)
            }

            is NetworkResponse.ServerError -> {
                Log.d(
                    tag,
                    ErrorObject(networkResponse.code, networkResponse.body?.message).toString()
                )
                ResultIncident(
                    null,
                    ErrorObject(networkResponse.code, networkResponse.body?.message)
                )
            }
            is NetworkResponse.NetworkError -> {
                Log.e(tag, ErrorObject(0, networkResponse.error.message).toString())
                ResultIncident(
                    null,
                    ErrorObject(0, networkResponse.error.message)
                )
            }
        }
    }

    override suspend fun addPhoto(
        id: Int,
        photo: File,
        tWidth: Int,
        tHeight: Int
    ): ResultAddMedia {
        val token = "Bearer " + loginDAO.getToken()

        val photoPart = MultipartBody.Part.createFormData(
            "photo",
            photo.name,
            photo.asRequestBody("image/*".toMediaTypeOrNull())
        )

        return when (val networkResponse =
            apiGraphql.addPhoto(token, id, photoPart, tWidth, tHeight)) {
            is NetworkResponse.Success -> {
                val response: AddMediaResponse = networkResponse.body
                ResultAddMedia(listOf(response), null)
            }
            is NetworkResponse.ServerError -> {
                Log.d(
                    tag,
                    ErrorObject(networkResponse.code, networkResponse.body?.message).toString()
                )
                ResultAddMedia(
                    null,
                    ErrorObject(networkResponse.code, networkResponse.body?.message)
                )
            }
            is NetworkResponse.NetworkError -> {
                Log.e(tag, ErrorObject(0, networkResponse.error.message).toString())
                ResultAddMedia(
                    null,
                    ErrorObject(0, networkResponse.error.message)
                )
            }
        }
    }

    override suspend fun addVideo(id: Int, video: File): ResultAddMedia {
        val token = "Bearer " + loginDAO.getToken()

        val videoPart = MultipartBody.Part.createFormData(
            "video",
            video.name,
            video.asRequestBody("video/*".toMediaTypeOrNull())
        )

        return when (val networkResponse =
            apiGraphql.addVideo(token, id, videoPart)) {
            is NetworkResponse.Success -> {
                val response: AddMediaResponse = networkResponse.body
                ResultAddMedia(listOf(response), null)
            }
            is NetworkResponse.ServerError -> {
                Log.d(
                    tag,
                    ErrorObject(networkResponse.code, networkResponse.body?.message).toString()
                )
                ResultAddMedia(
                    null,
                    ErrorObject(networkResponse.code, networkResponse.body?.message)
                )
            }
            is NetworkResponse.NetworkError -> {
                Log.e(tag, ErrorObject(0, networkResponse.error.message).toString())
                ResultAddMedia(
                    null,
                    ErrorObject(0, networkResponse.error.message)
                )
            }
        }
    }

    override suspend fun saveIncidentsLocal(incidents: List<Incident>) {
        incidentDAO.saveList(incidents)
    }

    override suspend fun deleteAllIncidents() {
        incidentDAO.deleteAll()
    }

    override suspend fun deleteAllTrendingIncidents() {
        incidentDAO.deleteAllTrendingIncidents()
    }

    override suspend fun saveIncidentLocal(incident: Incident) {
        incidentDAO.save(incident)
    }

    override suspend fun getIncident(incidentId: Int): ResultIncident {
        return ResultIncident(incidentDAO.get(incidentId), null)
    }

    override suspend fun getIncidentById(incidentId: Int): ResultIncident {
        val requestBody = QueryContainerBuilder()
        requestBody.putVariable("id", incidentId)
        val token = "Bearer " + loginDAO.getToken()

        return when (val networkResponse = apiGraphql.getIncidentById(token, requestBody)) {
            is NetworkResponse.Success -> {
                val incident = networkResponse.body.data?.getIncidentById
                incident?.let {
                    if (it.finishedAt == null && it.deletedAt == null) {
                        saveIncidentLocal(incident)
                        ResultIncident(incidentDAO.get(incidentId), null)
                    } else {
                        ResultIncident(
                            null,
                            ErrorObject(ErrorObject.NO_CONTENT, incidentId.toString())
                        )
                    }
                } ?: run {
                    ResultIncident(null, ErrorObject(ErrorObject.NO_CONTENT, incidentId.toString()))
                }
            }
            is NetworkResponse.ServerError -> {
                Log.d(
                    tag,
                    ErrorObject(networkResponse.code, networkResponse.body?.message).toString()
                )
                ResultIncident(
                    incidentDAO.get(incidentId),
                    ErrorObject(networkResponse.code, networkResponse.body?.message)
                )
            }
            is NetworkResponse.NetworkError -> {
                Log.d(tag, ErrorObject(0, networkResponse.error.message).toString())
                ResultIncident(incidentDAO.get(incidentId), null)
            }
        }
    }

    override suspend fun getCommentsCount(incidentId: Int): Int {
        return incidentDAO.getCommentsCount(incidentId)
    }

    override suspend fun updateIncidentReactions(incident: Incident) {
        incidentDAO.updateIncidentReactions(
            incident.fearfulFaceReactions,
            incident.handsPressedReactions,
            incident.redAngryFaceReactions,
            incident.redHeartReactions,
            incident.id
        )
    }

    override suspend fun addReaction(addReactionRequest: AddReactionRequest): ResultIncidents {
        val token = "Bearer " + loginDAO.getToken()
        val requestBody = QueryContainerBuilder()
        requestBody.putVariable(
            "input", addReactionRequest
        )

        return when (val networkResponse = apiGraphql.addReaction(token, requestBody)) {
            is NetworkResponse.Success -> {
                val incident = networkResponse.body.data?.addReaction
                incident?.let {
                    updateIncidentReactions(it)
                }
                ResultIncidents(incidentDAO.getList(), null)
            }
            is NetworkResponse.ServerError -> {
                Log.d(
                    tag,
                    ErrorObject(networkResponse.code, networkResponse.body?.message).toString()
                )
                ResultIncidents(
                    null,
                    ErrorObject(networkResponse.code, networkResponse.body?.message)
                )
            }
            is NetworkResponse.NetworkError -> {
                Log.e(tag, ErrorObject(0, networkResponse.error.message).toString())
                ResultIncidents(
                    null,
                    ErrorObject(0, networkResponse.error.message)
                )
            }
        }
    }

    override suspend fun addReactionSingleIncident(addReactionRequest: AddReactionRequest): ResultIncident {
        val token = "Bearer " + loginDAO.getToken()
        val requestBody = QueryContainerBuilder()
        requestBody.putVariable(
            "input", addReactionRequest
        )

        return when (val networkResponse = apiGraphql.addReaction(token, requestBody)) {
            is NetworkResponse.Success -> {
                val incident = networkResponse.body.data?.addReaction
                incident?.let {
                    updateIncidentReactions(it)
                }
                ResultIncident(incident, null)
            }
            is NetworkResponse.ServerError -> {
                Log.d(
                    tag,
                    ErrorObject(networkResponse.code, networkResponse.body?.message).toString()
                )
                ResultIncident(
                    null,
                    ErrorObject(networkResponse.code, networkResponse.body?.message)
                )
            }
            is NetworkResponse.NetworkError -> {
                Log.e(tag, ErrorObject(0, networkResponse.error.message).toString())
                ResultIncident(
                    null,
                    ErrorObject(0, networkResponse.error.message)
                )
            }
        }
    }

    override suspend fun joinLiveChannel(channel: String): ResultJoinLiveChannel {
        val requestBody = QueryContainerBuilder()
        val token = "Bearer " + loginDAO.getToken()
        requestBody.putVariable("channel", channel)

        return when (val networkResponse = apiGraphql.joinLiveChannel(token, requestBody)) {
            is NetworkResponse.Success -> {
                val videoStream = networkResponse.body.data?.joinLiveChannel
                /*videoStream?.let {
                    saveVideoStreamLocal(it)
                }*/
                ResultJoinLiveChannel(videoStream, null)
            }
            is NetworkResponse.ServerError -> {
                Log.d(
                    tag,
                    ErrorObject(networkResponse.code, networkResponse.body?.message).toString()
                )
                ResultJoinLiveChannel(
                    null,
                    ErrorObject(networkResponse.code, networkResponse.body?.message)
                )
            }
            is NetworkResponse.NetworkError -> {
                Log.d(tag, ErrorObject(0, networkResponse.error.message).toString())
                ResultJoinLiveChannel(null, null)
            }
        }
    }

    override fun getTrendingIncidentsLocal(): Flow<List<Incident>> {
        return incidentDAO.getTrendingIncidents()
    }

    override suspend fun postPlayVideo(playVideo: PlayVideo): ResultPlayVideo {
        val token = "Bearer " + loginDAO.getToken()
        return when (val result = apiRest.postPlayVideo(token, playVideo)) {
            is NetworkResponse.Success -> {
                ResultPlayVideo(result.body, null)
            }
            is NetworkResponse.ServerError -> {
                ResultPlayVideo(null, ErrorObject(result.code, result.body?.message))
            }
            is NetworkResponse.NetworkError -> {
                ResultPlayVideo(null, ErrorObject(0, result.error.message))
            }
        }
    }

    override suspend fun postCheckPlace(checkPlaceRequest: CheckPlace): ResultCheckPlace {
        val token = "Bearer " + loginDAO.getToken()
        return when (val result = apiRest.postCheckPlace(token, checkPlaceRequest)) {
            is NetworkResponse.Success -> {
                ResultCheckPlace(result.body, null)
            }
            is NetworkResponse.ServerError -> {
                ResultCheckPlace(null, ErrorObject(result.code, result.body?.message))
            }
            is NetworkResponse.NetworkError -> {
                ResultCheckPlace(null, ErrorObject(0, result.error.message))
            }
        }
    }

    override suspend fun getCategories(): ResultCategories {
        val requestBody = QueryContainerBuilder()
        val token = "Bearer " + loginDAO.getToken()

        return when (val networkResponse = apiGraphql.getCategories(token, requestBody)) {
            is NetworkResponse.Success -> {
                val result = networkResponse.body.data?.getIncidentsCategories
                ResultCategories(result, null)
            }
            is NetworkResponse.ServerError -> {
                Log.d(
                    tag,
                    ErrorObject(networkResponse.code, networkResponse.body?.message).toString()
                )
                ResultCategories(
                    null,
                    ErrorObject(networkResponse.code, networkResponse.body?.message)
                )
            }
            is NetworkResponse.NetworkError -> {
                Log.d(tag, ErrorObject(0, networkResponse.error.message).toString())
                ResultCategories(null, null)
            }
        }
    }

    override suspend fun deleteIncident(incidentId: Int) {
        incidentDAO.deleteIncident(incidentId)
    }

    override suspend fun setLiveStopped(incidentId: Int) {
        incidentDAO.setLiveStopped(incidentId)
    }
}