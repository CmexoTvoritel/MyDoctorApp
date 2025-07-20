package com.asc.mydoctorapp.ui.records.viewmodel

import com.asc.mydoctorapp.R
import com.asc.mydoctorapp.ui.records.viewmodel.model.RecordUi
import com.asc.mydoctorapp.ui.records.viewmodel.model.RecordsAction
import com.asc.mydoctorapp.ui.records.viewmodel.model.RecordsEvent
import com.asc.mydoctorapp.ui.records.viewmodel.model.RecordsTab
import com.asc.mydoctorapp.ui.records.viewmodel.model.RecordsUIState
import com.diveomedia.little.stories.bedtime.books.kids.core.ui.viewmodel.BaseSharedViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RecordsViewModel @Inject constructor() : BaseSharedViewModel<RecordsUIState, RecordsAction, RecordsEvent>(
    initialState = RecordsUIState(
        selectedTab = RecordsTab.CURRENT,
        current = listOf(
            RecordUi(
                id = "1",
                doctorName = "Иван Сидоров",
                specialty = "кардиолог",
                time = "15:00",
                address = "Вавилова, 15",
                clinic = "Клиника \"Здоровье\"",
                photoRes = R.drawable.ic_doctor_placeholder,
                isFavorite = true
            ),
            RecordUi(
                id = "2",
                doctorName = "Иван Сидоров",
                specialty = "кардиолог",
                time = "15:00",
                address = "Вавилова, 15",
                clinic = "Клиника \"Здоровье\"",
                photoRes = R.drawable.ic_doctor_placeholder,
                isFavorite = false
            )
        ),
        past = listOf(
            RecordUi(
                id = "3",
                doctorName = "Иван Сидоров",
                specialty = "кардиолог",
                time = "15:00",
                address = "Вавилова, 15",
                clinic = "Клиника \"Здоровье\"",
                photoRes = R.drawable.ic_doctor_placeholder,
                isFavorite = true
            ),
            RecordUi(
                id = "4",
                doctorName = "Иван Сидоров",
                specialty = "кардиолог",
                time = "15:00",
                address = "Вавилова, 15",
                clinic = "Клиника \"Здоровье\"",
                photoRes = R.drawable.ic_doctor_placeholder,
                isFavorite = false
            )
        )
    )
) {

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
            RecordsTab.PAST -> navigateToSearchDoctor() // аналогичная логика для повторной записи
        }
    }
    
    private fun openRecordDetails(recordId: String) {
        sendViewAction(RecordsAction.NavigateToRecordDetails(recordId))
    }
    
    private fun navigateToSearchDoctor() {
        sendViewAction(RecordsAction.NavigateToSearchDoctor("search_doctor"))
    }
}
