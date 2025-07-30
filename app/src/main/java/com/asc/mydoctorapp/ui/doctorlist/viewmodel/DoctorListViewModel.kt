package com.asc.mydoctorapp.ui.doctorlist.viewmodel

import com.asc.mydoctorapp.R
import com.asc.mydoctorapp.core.domain.usecase.GetClinicByQueryUseCase
import com.asc.mydoctorapp.core.domain.usecase.GetDoctorsUseCase
import com.asc.mydoctorapp.ui.doctorlist.model.DoctorUIItem
import com.asc.mydoctorapp.ui.doctorlist.viewmodel.model.DoctorAction
import com.asc.mydoctorapp.ui.doctorlist.viewmodel.model.DoctorEvent
import com.asc.mydoctorapp.ui.doctorlist.viewmodel.model.DoctorUIState
import com.diveomedia.little.stories.bedtime.books.kids.core.ui.viewmodel.BaseSharedViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DoctorListViewModel @Inject constructor(
    private val getDoctorsUseCase: GetDoctorsUseCase,
    private val getClinicByQueryUseCase: GetClinicByQueryUseCase
): BaseSharedViewModel<DoctorUIState, DoctorAction, DoctorEvent>(
    initialState = DoctorUIState(isLoading = true)
) {
    
    private fun loadDoctors(clinicName: String) {
        viewModelScope.launch {
            updateViewState { state ->
                state.copy(isLoading = true)
            }
            
            try {
                val clinic = getClinicByQueryUseCase(clinicName)
                val doctors = getDoctorsUseCase(clinicName).map { doctor ->
                    DoctorUIItem(
                        id = doctor.email,
                        name = doctor.name,
                        surname = doctor.surname,
                        specialty = doctor.specialty,
                        rating = 5.0f,
                        photoRes = R.drawable.ic_doctor_placeholder,
                        isFavorite = false
                    )
                }
                
                updateViewState { state ->
                    state.copy(
                        isLoading = false,
                        doctorList = doctors,
                        clinic = clinic.firstOrNull()
                    )
                }
            } catch (e: Exception) {
                // Обработка ошибок загрузки
                updateViewState { state ->
                    state.copy(isLoading = false)
                }
            }
        }
    }
    
    private fun navigateToDoctorDetails(doctorEmail: String) {
        sendViewAction(DoctorAction.NavigateToDoctorDetails(doctorEmail))
    }

    override fun obtainEvent(viewEvent: DoctorEvent) {
        when (viewEvent) {
            is DoctorEvent.InitLoad -> {
                loadDoctors(clinicName = viewEvent.clinicName)
            }
            DoctorEvent.OnBackClick -> {
                sendViewAction(DoctorAction.NavigateBack)
            }
            is DoctorEvent.OnDoctorClick -> {
                navigateToDoctorDetails(viewEvent.doctorId)
            }
            is DoctorEvent.OnFavoriteToggle -> {
                val updatedDoctors = viewStates().value?.doctorList?.map {
                    if (it.id == viewEvent.doctorId) it.copy(isFavorite = viewEvent.newValue) else it
                }
                updateViewState {
                    state -> state.copy(doctorList = updatedDoctors ?: emptyList())
                }
            }
        }
    }
}