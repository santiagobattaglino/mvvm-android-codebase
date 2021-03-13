package com.santiagobattaglino.mvvm.codebase.presentation.viewmodel

import com.santiagobattaglino.mvvm.codebase.data.network.error.ErrorObject
import com.santiagobattaglino.mvvm.codebase.data.repository.notification.NotificationRepo
import com.santiagobattaglino.mvvm.codebase.domain.entity.Notification
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NotificationsViewModel(
    private val repo: NotificationRepo
) : ViewModel() {

    val notificationsUiData = MutableLiveData<ResultNotifications>()

    fun getNotifications() {
        viewModelScope.launch {
            repo.getNotifications().collect {
                notificationsUiData.value = ResultNotifications(it)
            }
        }
    }

    fun deleteNotificationByIncidentId(incidentId: String?) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repo.deleteNotificationByIncidentId(incidentId)
            }
        }
    }

    fun deleteNotificationById(id: Int) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repo.deleteNotificationById(id)
            }
        }
    }
}

data class ResultNotifications(
    val notifications: List<Notification>? = null,
    val error: ErrorObject? = null
)