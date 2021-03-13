package com.santiagobattaglino.mvvm.codebase.data.repository.notification

import com.santiagobattaglino.mvvm.codebase.domain.entity.Notification
import kotlinx.coroutines.flow.Flow

interface NotificationRepo {
    fun getNotifications(): Flow<List<Notification>>
    fun deleteNotificationByIncidentId(incidentId: String?)
    fun deleteNotificationById(id: Int)
}
