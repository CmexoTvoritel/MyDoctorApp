package com.asc.mydoctorapp.ui.doctorlist.viewmodel.model

import com.asc.mydoctorapp.ui.doctorlist.model.DoctorUIItem

data class DoctorUIState (
    val doctorList: List<DoctorUIItem> = emptyList()
)

sealed interface DoctorAction {
    data class NavigateToDoctorDetails(val doctorEmail: String): DoctorAction
    data object NavigateBack: DoctorAction
}

sealed interface DoctorEvent {
    data class InitLoad(val clinicName: String) : DoctorEvent
    data object OnBackClick : DoctorEvent
    data class OnDoctorClick(val doctorId: String) : DoctorEvent
    data class OnFavoriteToggle(val doctorId: String, val newValue: Boolean) : DoctorEvent
}