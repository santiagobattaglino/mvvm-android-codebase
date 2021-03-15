package com.santiagobattaglino.mvvm.codebase.presentation.viewmodel

import android.content.Context
import android.location.Location
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.santiagobattaglino.mvvm.codebase.data.network.error.ErrorObject
import com.santiagobattaglino.mvvm.codebase.data.repository.incident.IncidentRepo
import com.santiagobattaglino.mvvm.codebase.domain.entity.Category
import com.santiagobattaglino.mvvm.codebase.domain.entity.Incident
import com.santiagobattaglino.mvvm.codebase.domain.model.*
import com.santiagobattaglino.mvvm.codebase.util.reizeImageFileWithGlide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class IncidentsViewModel(
    private val incidentRepo: IncidentRepo
) : ViewModel() {

    private val mTag = javaClass.simpleName

    val incidentsUiData = MutableLiveData<ResultIncidents>()
    val incidentUiData = MutableLiveData<ResultIncident>()
    val addMediaUiData = MutableLiveData<ResultAddMedia>()
    val resizePhotoUiData = MutableLiveData<Boolean>()
    val commentsCountUiData = MutableLiveData<Int>()
    private val reactionsUiData = MutableLiveData<ResultIncidents>()
    val reactionsSingleUiData = MutableLiveData<ResultIncident>()
    val videoStreamUiData = MutableLiveData<ResultJoinLiveChannel>()
    val sendWebSocketMessageUiData = MutableLiveData<Boolean>()
    val websocketMessagesUiData = MutableLiveData<WebSocketMessage>()
    val trendingIncidentsUiData = MutableLiveData<ResultIncidents>()
    val playVideoUiData = MutableLiveData<ResultPlayVideo>()
    val checkPlaceUiData = MutableLiveData<ResultCheckPlace>()
    val locationUiData = MutableLiveData<Location?>()
    var mediaListUiData = MutableLiveData<List<Media>>()
    val categoriesUiData = MutableLiveData<ResultCategories>()

    fun getIncidents(lastKnownLocation: String?, first: Int, baseKey: String?) {
        viewModelScope.launch {
            val resultIncidents = withContext(Dispatchers.IO) {
                if (baseKey == null) {
                    incidentRepo.deleteAllIncidents()
                }
                incidentRepo.getIncidents(lastKnownLocation, first, baseKey)
            }

            resultIncidents.incidents?.let {
                // we're already observing local changes with flow. Only login chunks of 10 from server.
                Log.d(mTag, "loading $first incidents from remote with baseKey: $baseKey")
            }

            resultIncidents.error?.let {
                incidentsUiData.value = ResultIncidents(error = it)
            }
        }
    }

    fun getIncidentsFromLocal() {
        viewModelScope.launch {
            incidentRepo.getIncidentsLocal().collect {
                incidentsUiData.value = ResultIncidents(it)
            }
        }
    }

    fun getIncidentReactions(id: Int) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                incidentRepo.getIncidentReactions(id)
            }

            result.incident?.let {
                // we're already observing local changes with flow.
                Log.d(mTag, "incident from remote: $it")
            }

            result.error?.let {
                incidentsUiData.value = ResultIncidents(error = it)
            }
        }
    }

    fun getIncidentFromLocal(incidentId: Int) {
        viewModelScope.launch {
            incidentRepo.getIncidentLocal(incidentId).collect {
                incidentUiData.value = ResultIncident(it)
            }
        }
    }

    fun getIncident(incidentId: Int, returnIncidents: Boolean? = true) {
        viewModelScope.launch {
            val resultIncident = withContext(Dispatchers.IO) {
                incidentRepo.getIncident(incidentId)
            }

            resultIncident.incident?.let { incident ->
                returnIncidents?.let {
                    if (it) {
                        incidentUiData.value = ResultIncident(incident)
                    }
                }
            }

            resultIncident.error?.let {
                incidentUiData.value = ResultIncident(error = it)
            }
        }
    }

    fun getIncidentById(incidentId: Int, returnIncidents: Boolean? = true) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                incidentRepo.getIncidentById(incidentId)
            }

            result.incident?.let { incident ->
                returnIncidents?.let {
                    if (it) {
                        incidentUiData.value = ResultIncident(incident)
                    }
                }
            }

            result.error?.let {
                incidentUiData.value = ResultIncident(error = it)
            }
        }
    }

    fun createIncident(incident: Incident) {
        viewModelScope.launch {
            val resultIncident = withContext(Dispatchers.IO) {
                incidentRepo.createIncident(incident)
            }

            resultIncident.incident?.let {
                incidentUiData.value = ResultIncident(it)
            }

            resultIncident.error?.let {
                incidentUiData.value = ResultIncident(error = it)
            }
        }
    }

    fun addMedia(
        incidentId: Int,
        mediaList: List<Media>,
        tWidth: Int,
        tHeight: Int
    ) {
        viewModelScope.launch {
            val resultList = mutableListOf<AddMediaResponse>()

            mediaList.forEach { media ->
                if (media.typeContent != Media.TYPE_CONTENT_MAP) {
                    media.url?.let { url ->
                        when (media.typeContent) {
                            Media.TYPE_CONTENT_PHOTO -> {
                                val result = withContext(Dispatchers.IO) {
                                    incidentRepo.addPhoto(incidentId, File(url), tWidth, tHeight)
                                }
                                result.error?.let {
                                    addMediaUiData.value = ResultAddMedia(error = it)
                                }
                                result.mediaResponseList?.let {
                                    resultList.add(it[0])
                                }
                            }
                            Media.TYPE_CONTENT_VIDEO -> {
                                val result = withContext(Dispatchers.IO) {
                                    incidentRepo.addVideo(incidentId, File(url))
                                }
                                result.error?.let {
                                    addMediaUiData.value = ResultAddMedia(error = it)
                                }
                                result.mediaResponseList?.let {
                                    resultList.add(it[0])
                                }
                            }
                            else -> {
                            }
                        }
                    }

                    if (resultList.size == mediaList.size - 1) {
                        addMediaUiData.value = ResultAddMedia(resultList)
                    }
                }
            }
        }
    }

    fun resizePhotoTaken(
        context: Context,
        file: File,
        outputFile: File,
        size: Int
    ) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    reizeImageFileWithGlide(context, file, outputFile, size)
                }
                resizePhotoUiData.value = true
            } catch (e: Exception) {
                e.printStackTrace()
                resizePhotoUiData.value = false
            }
        }
    }

    fun getCommentsCount(incidentId: Int) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                incidentRepo.getCommentsCount(incidentId)
            }
            commentsCountUiData.value = result
        }
    }

    fun addReaction(addReactionRequest: AddReactionRequest) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                incidentRepo.addReaction(addReactionRequest)
            }

            result.incidents?.let {
                reactionsUiData.value = ResultIncidents(it)
            }

            result.error?.let {
                reactionsUiData.value = ResultIncidents(error = it)
            }
        }
    }

    fun addReactionSingleIncident(addReactionRequest: AddReactionRequest) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                incidentRepo.addReactionSingleIncident(addReactionRequest)
            }

            result.incident?.let {
                reactionsSingleUiData.value = ResultIncident(it)
            }

            result.error?.let {
                reactionsSingleUiData.value = ResultIncident(error = it)
            }
        }
    }

    fun joinLiveChannel(channel: String) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                incidentRepo.joinLiveChannel(channel)
            }

            result.videoStream?.let {
                videoStreamUiData.value = ResultJoinLiveChannel(it)
            }

            result.error?.let {
                videoStreamUiData.value = ResultJoinLiveChannel(error = it)
            }
        }
    }

    fun getTrendingIncidents(lastKnownLocation: String?, first: Int, baseKey: String?) {
        viewModelScope.launch {
            val resultTrendingIncidents = withContext(Dispatchers.IO) {
                if (baseKey == null) {
                    incidentRepo.deleteAllTrendingIncidents()
                }
                incidentRepo.getIncidents(lastKnownLocation, first, baseKey, true)
            }

            resultTrendingIncidents.incidents?.let {
                // we're already observing local changes with flow. Only login chunks of 10 from server.
                Log.d(mTag, "loading $first trending incidents from remote with baseKey: $baseKey")
            }

            resultTrendingIncidents.error?.let {
                trendingIncidentsUiData.value = ResultIncidents(error = it)
            }
        }
    }

    fun getTrendingIncidentsFromLocal() {
        viewModelScope.launch {
            incidentRepo.getTrendingIncidentsLocal().collect {
                trendingIncidentsUiData.value = ResultIncidents(it)
            }
        }
    }

    fun postPlayVideo(playVideo: PlayVideo) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                incidentRepo.postPlayVideo(playVideo)
            }

            result.playVideo?.let {
                playVideoUiData.value = ResultPlayVideo(it)
            }

            result.error?.let {
                playVideoUiData.value = ResultPlayVideo(error = it)
            }
        }
    }

    fun setLiveStopped(incidentId: Int) {
        viewModelScope.launch {
            incidentRepo.setLiveStopped(incidentId)
        }
    }

    fun postCheckPlace(checkPlace: CheckPlace) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                incidentRepo.postCheckPlace(checkPlace)
            }

            result.checkPlace?.let {
                checkPlaceUiData.value = ResultCheckPlace(it)
            }

            result.error?.let {
                checkPlaceUiData.value = ResultCheckPlace(error = it)
            }
        }
    }

    fun setMediaList(mediaList: List<Media>) {
        mediaListUiData.value = mediaList
    }

    fun getCategories() {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                incidentRepo.getCategories()
            }

            result.categories?.let {
                categoriesUiData.value = ResultCategories(it)
            }

            result.error?.let {
                categoriesUiData.value = ResultCategories(error = it)
            }
        }
    }

    fun deleteIncident(toInt: Int) {
        viewModelScope.launch {
            incidentRepo.deleteIncident(toInt)
        }
    }
}

data class ResultIncidents(val incidents: List<Incident>? = null, val error: ErrorObject? = null)
data class ResultIncident(val incident: Incident? = null, val error: ErrorObject? = null)
data class ResultAddMedia(
    val mediaResponseList: List<AddMediaResponse>? = null,
    val error: ErrorObject? = null
)

data class ResultJoinLiveChannel(
    val videoStream: VideoStream? = null,
    val error: ErrorObject? = null
)

data class ResultPlayVideo(val playVideo: PlayVideo? = null, val error: ErrorObject? = null)
data class ResultCheckPlace(val checkPlace: CheckPlace? = null, val error: ErrorObject? = null)
data class ResultCategories(val categories: List<Category>? = null, val error: ErrorObject? = null)