package com.asc.mydoctorapp.ui.doctorlist.viewmodel

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
    private val getDoctorsUseCase: GetDoctorsUseCase
): BaseSharedViewModel<DoctorUIState, DoctorAction, DoctorEvent>(
    initialState = DoctorUIState(emptyList())
) {
    init {
        loadDoctors()
    }
    
    private fun loadDoctors() {
        viewModelScope.launch {
            try {
                val doctors = getDoctorsUseCase("Clinic1").map { doctor ->
                    DoctorUIItem(
                        id = doctor.email,
                        name = doctor.name,
                        surname = doctor.surname,
                        specialty = doctor.specialty,
                        rating = 5.0f,
                        photoRes = null,
                        isFavorite = false
                    )
                }
                
                updateViewState { state ->
                    state.copy(doctorList = doctors)
                }
            } catch (e: Exception) {
                // Обработка ошибок загрузки
                // Можно добавить состояние ошибки в DoctorUIState если нужно
            }
        }
    }
    
    private fun navigateToDoctorDetails() {
        sendViewAction(DoctorAction.NavigateToDoctorDetails)
    }

    override fun obtainEvent(viewEvent: DoctorEvent) {
        when (viewEvent) {
            DoctorEvent.OnBackClick -> {}
            is DoctorEvent.OnDoctorClick -> {
                navigateToDoctorDetails()
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