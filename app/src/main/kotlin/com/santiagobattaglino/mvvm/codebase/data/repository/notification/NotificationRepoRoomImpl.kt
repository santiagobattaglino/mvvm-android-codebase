package com.santiagobattaglino.mvvm.codebase.data.repository.notification

import com.santiagobattaglino.mvvm.codebase.data.room.dao.NotificationDAO
import com.santiagobattaglino.mvvm.codebase.domain.entity.Notification
import kotlinx.coroutines.flow.Flow

class NotificationRepoRoomImpl(
    private val notificationDAO: NotificationDAO
) : NotificationRepo {

    private val tag = javaClass.simpleName

    override fun getNotifications(): Flow<List<Notification>> {
        return notificationDAO.getList()
    }

    override fun deleteNotificationByIncidentId(incidentId: String?) {
        return notificationDAO.deleteNotificationByIncidentId(incidentId)
    }

    override fun deleteNotificationById(id: Int) {
        return notificationDAO.deleteNotificationById(id)
    }
}