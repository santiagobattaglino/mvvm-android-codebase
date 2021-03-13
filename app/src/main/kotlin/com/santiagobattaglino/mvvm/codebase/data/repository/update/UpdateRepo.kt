package com.santiagobattaglino.mvvm.codebase.data.repository.update

import com.santiagobattaglino.mvvm.codebase.domain.entity.Update
import com.santiagobattaglino.mvvm.codebase.presentation.viewmodel.ResultUpdates

interface UpdateRepo {
    suspend fun getUpdates(incidentId: Int): ResultUpdates
    suspend fun saveUpdatesLocal(updates: List<Update>)
    suspend fun deleteIncidentUpdates(incidentId: Int)
}