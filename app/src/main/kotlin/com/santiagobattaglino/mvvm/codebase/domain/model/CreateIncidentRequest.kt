package com.santiagobattaglino.mvvm.codebase.domain.model

class CreateIncidentRequest(
    val title: String = "",
    val description: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val address: String? = "",
    val city: String? = "",
    val isStreamingLive: Boolean = false,
    val incidentsCategoryId: Int = 0,
    val at: String = ""
)