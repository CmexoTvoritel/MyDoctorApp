package com.asc.mydoctorapp.ui.records.viewmodel

import com.asc.mydoctorapp.R
import com.asc.mydoctorapp.core.domain.usecase.GetUserRecordsUseCase
import com.asc.mydoctorapp.navigation.AppRoutes
import com.asc.mydoctorapp.ui.records.viewmodel.model.RecordUi
import com.asc.mydoctorapp.ui.records.viewmodel.model.RecordsAction
import com.asc.mydoctorapp.ui.records.viewmodel.model.RecordsEvent
import com.asc.mydoctorapp.ui.records.viewmodel.model.RecordsTab
import com.asc.mydoctorapp.ui.records.viewmodel.model.RecordsUIState
import com.diveomedia.little.stories.bedtime.books.kids.core.ui.viewmodel.BaseSharedViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecordsViewModel @Inject constructor(
    private val getUserRecordsUseCase: GetUserRecordsUseCase
) : BaseSharedViewModel<RecordsUIState, RecordsAction, RecordsEvent>(
    initialState = RecordsUIState(
        selectedTab = RecordsTab.CURRENT,
        current = emptyList(),
        past = listOf()
    )
) {

    init {
        viewModelScope.launch {
            val userEvents = getUserRecordsUseCase.invoke()
            updateViewState { state ->
                state.copy(
                    current = userEvents.map {
                        RecordUi(
                            id = it.email ?: "",
                            doctorName = "${it.docName} ${it.docSurname}",
                            time = it.start ?: "",
                            specialty = it.docSpecialty ?: "Врач",
                            photoRes = R.drawable.ic_doctor_placeholder,
                            isFavorite = false,
                            address = "Вавилова, 15",
                            clinic = "Клиника Здоровье"
                        )
                    }
                )
            }
        }
    }

    override fun obtainEvent(viewEvent: RecordsEvent) {
        when (viewEvent) {
            is RecordsEvent.OnTabSelected -> handleTabSelected(viewEvent.tab)
            is RecordsEvent.OnFavoriteToggle -> toggleFavorite(viewEvent.recordId, viewEvent.newValue)
            is RecordsEvent.OnPrimaryButtonClick -> handlePrimaryButtonClick()
            is RecordsEvent.OnRecordClick -> openRecordDetails(viewEvent.recordId)
        }
    }
    
    private fun handleTabSelected(tab: RecordsTab) {
        updateViewState { state ->
            state.copy(selectedTab = tab)
        }
    }
    
    private fun toggleFavorite(recordId: String, newValue: Boolean) {
        updateViewState { state ->
            // Обновляем текущие записи
            val updatedCurrent = state.current.map { record ->
                if (record.id == recordId) record.copy(isFavorite = newValue) else record
            }
            
            // Обновляем прошедшие записи
            val updatedPast = state.past.map { record ->
                if (record.id == recordId) record.copy(isFavorite = newValue) else record
            }
            
            state.copy(
                current = updatedCurrent,
                past = updatedPast
            )
        }
    }
    
    private fun handlePrimaryButtonClick() {
        val selectedTab = viewStates().value?.selectedTab ?: RecordsTab.CURRENT
        when (selectedTab) {
            RecordsTab.CURRENT -> navigateToSearchDoctor()
            RecordsTab.PAST -> navigateToSearchDoctor()
        }
    }
    
    private fun openRecordDetails(recordId: String) {
        sendViewAction(RecordsAction.NavigateToRecordDetails(recordId))
    }
    
    private fun navigateToSearchDoctor() {
        sendViewAction(RecordsAction.NavigateToSearchDoctor(AppRoutes.DoctorList.route))
    }
}
