package com.asc.mydoctorapp.ui.doctorlist.viewmodel.model

import com.asc.mydoctorapp.ui.doctorlist.model.DoctorUIItem

data class DoctorUIState (
    val doctorList: List<DoctorUIItem> = emptyList()
)

sealed interface DoctorAction {
    data object NavigateToDoctorDetails: DoctorAction
}

sealed interface DoctorEvent {
    data object OnBackClick : DoctorEvent
    data class OnDoctorClick(val doctorId: String) : DoctorEvent
    data class OnFavoriteToggle(val doctorId: String, val newValue: Boolean) : DoctorEvent
}