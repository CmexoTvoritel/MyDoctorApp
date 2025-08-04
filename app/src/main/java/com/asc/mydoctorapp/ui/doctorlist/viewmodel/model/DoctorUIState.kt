package com.asc.mydoctorapp.ui.doctorlist.viewmodel.model

import com.asc.mydoctorapp.core.domain.model.Clinic
import com.asc.mydoctorapp.core.data.remote.DoctorUIItem

data class DoctorUIState (
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val doctorList: List<DoctorUIItem> = emptyList(),
    val clinic: Clinic? = null
)

sealed interface DoctorAction {
    data class NavigateToDoctorDetails(val doctorEmail: String): DoctorAction
    data object NavigateBack: DoctorAction
}

sealed interface DoctorEvent {
    data class InitLoad(val clinicName: String) : DoctorEvent
    object OnRefresh : DoctorEvent
    data object OnBackClick : DoctorEvent
    data class OnDoctorClick(val doctorId: String) : DoctorEvent
    data class OnFavoriteToggle(val doctorId: String, val newValue: Boolean) : DoctorEvent
}