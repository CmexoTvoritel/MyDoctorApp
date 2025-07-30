package com.asc.mydoctorapp.ui.cliniclist.viewmodel.model

import com.asc.mydoctorapp.core.domain.model.Clinic

data class ClinicListUIState(
    val isLoading: Boolean = true,
    val clinics: List<Clinic> = emptyList(),
    val error: String? = null
)

sealed interface ClinicListEvent {
    object LoadClinics : ClinicListEvent
    data class OnClinicClick(val clinicName: String) : ClinicListEvent
    object OnBackClick : ClinicListEvent
}

sealed interface ClinicListAction {
    data class NavigateToClinic(val route: String) : ClinicListAction
    object NavigateBack : ClinicListAction
    data class ShowError(val message: String) : ClinicListAction
}
