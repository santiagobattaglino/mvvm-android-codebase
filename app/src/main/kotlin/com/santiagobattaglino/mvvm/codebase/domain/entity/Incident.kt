package com.santiagobattaglino.mvvm.codebase.domain.entity

import com.santiagobattaglino.mvvm.codebase.domain.model.Media
import com.santiagobattaglino.mvvm.codebase.domain.model.User
import com.santiagobattaglino.mvvm.codebase.domain.model.VideoStream
import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "incidents")
data class Incident(
    @PrimaryKey
    val id: Int = 0,
    var at: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val address: String? = "",
    val title: String = "",
    val description: String = "",
    val photo: String? = null,
    val thumbnail: String? = null,
    val video: String? = null,
    val city: String? = "",
    val updatesCount: Int = 0,
    val commentsCount: Int = 0,
    @Embedded(prefix = "user_")
    val userCreate: User? = null,
    var fearfulFaceReactions: Int = 0,
    var handsPressedReactions: Int = 0,
    var redAngryFaceReactions: Int = 0,
    var redHeartReactions: Int = 0,
    // Used only for creating live incidents (CreateIncident request)
    val isStreamingLive: Boolean = false,
    @Embedded(prefix = "video_stream_")
    val videoStream: VideoStream? = null,
    var distance: String? = null,
    val media: List<Media>? = emptyList(),
    val updatedAt: String = "",
    val incidentsCategoryId: Int? = null,
    val deletedAt: String? = null,
    val finishedAt: String? = null
) : Parcelable