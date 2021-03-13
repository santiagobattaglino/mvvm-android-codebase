package com.santiagobattaglino.mvvm.codebase.data.repository.incident

import com.santiagobattaglino.mvvm.codebase.domain.entity.Incident
import com.santiagobattaglino.mvvm.codebase.presentation.viewmodel.*
import com.santiagobattaglino.mvvm.codebase.domain.model.AddReactionRequest
import com.santiagobattaglino.mvvm.codebase.domain.model.CheckPlace
import com.santiagobattaglino.mvvm.codebase.domain.model.PlayVideo
import kotlinx.coroutines.flow.Flow
import java.io.File

interface IncidentRepo {
    suspend fun getIncidents(
        lastKnownLocation: String?,
        first: Int,
        baseKey: String?,
        onlyLive: Boolean? = false
    ): ResultIncidents

    suspend fun getIncidentReactions(id: Int): ResultIncident
    suspend fun deleteAllIncidents()
    suspend fun deleteAllTrendingIncidents()
    fun getIncidentsLocal(): Flow<List<Incident>>
    fun getIncidentLocal(incidentId: Int): Flow<Incident>
    suspend fun saveIncidentsLocal(incidents: List<Incident>)
    suspend fun saveIncidentLocal(incident: Incident)
    suspend fun getIncident(incidentId: Int): ResultIncident
    suspend fun getIncidentById(incidentId: Int): ResultIncident
    suspend fun createIncident(incident: Incident): ResultIncident
    suspend fun addPhoto(id: Int, photo: File, tWidth: Int, tHeight: Int): ResultAddMedia
    suspend fun addVideo(id: Int, video: File): ResultAddMedia
    suspend fun getCommentsCount(incidentId: Int): Int
    suspend fun updateIncidentReactions(incident: Incident)
    suspend fun addReaction(addReactionRequest: AddReactionRequest): ResultIncidents
    suspend fun addReactionSingleIncident(addReactionRequest: AddReactionRequest): ResultIncident
    suspend fun joinLiveChannel(channel: String): ResultJoinLiveChannel
    fun getTrendingIncidentsLocal(): Flow<List<Incident>>
    suspend fun postPlayVideo(playVideo: PlayVideo): ResultPlayVideo
    suspend fun setLiveStopped(incidentId: Int)
    suspend fun postCheckPlace(checkPlaceRequest: CheckPlace): ResultCheckPlace
    suspend fun getCategories(): ResultCategories
    suspend fun deleteIncident(incidentId: Int)
}