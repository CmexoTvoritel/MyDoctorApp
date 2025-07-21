package com.asc.mydoctorapp.ui.doctorlist.viewmodel

import com.asc.mydoctorapp.ui.doctorlist.model.DoctorUIItem
import com.asc.mydoctorapp.ui.doctorlist.viewmodel.model.DoctorAction
import com.asc.mydoctorapp.ui.doctorlist.viewmodel.model.DoctorEvent
import com.asc.mydoctorapp.ui.doctorlist.viewmodel.model.DoctorUIState
import com.diveomedia.little.stories.bedtime.books.kids.core.ui.viewmodel.BaseSharedViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DoctorListViewModel @Inject constructor(

): BaseSharedViewModel<DoctorUIState, DoctorAction, DoctorEvent>(
    initialState = DoctorUIState(mockDoctors)
) {
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

// Тестовые данные для превью
private val mockDoctors = listOf(
    DoctorUIItem(
        id = "1",
        name = "Иван Сидоров",
        specialty = "кардиолог",
        rating = 5.0f,
        photoRes = null,
        isFavorite = false
    ),
    DoctorUIItem(
        id = "2",
        name = "Елена Петрова",
        specialty = "невролог",
        rating = 4.5f,
        photoRes = null,
        isFavorite = true
    ),
    DoctorUIItem(
        id = "3",
        name = "Михаил Иванов",
        specialty = "терапевт",
        rating = 4.8f,
        photoRes = null,
        isFavorite = false
    )
)