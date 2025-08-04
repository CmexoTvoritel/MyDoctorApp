package com.asc.mydoctorapp.ui.records.viewmodel

import com.asc.mydoctorapp.R
import com.asc.mydoctorapp.core.data.mapper.toEntity
import com.asc.mydoctorapp.core.data.mapper.toRecordUi
import com.asc.mydoctorapp.core.data.remote.RecordUI
import com.asc.mydoctorapp.core.domain.model.Clinic
import com.asc.mydoctorapp.core.domain.usecase.GetClinicByQueryUseCase
import com.asc.mydoctorapp.core.domain.usecase.GetUserRecordsUseCase
import com.asc.mydoctorapp.core.domain.usecase.RecordsDatabaseUseCase
import com.asc.mydoctorapp.core.domain.usecase.UserInfoUseCase
import com.asc.mydoctorapp.navigation.AppRoutes
import com.asc.mydoctorapp.ui.records.viewmodel.model.RecordsAction
import com.asc.mydoctorapp.ui.records.viewmodel.model.RecordsEvent
import com.asc.mydoctorapp.ui.records.viewmodel.model.RecordsTab
import com.asc.mydoctorapp.ui.records.viewmodel.model.RecordsUIState
import com.diveomedia.little.stories.bedtime.books.kids.core.ui.viewmodel.BaseSharedViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import javax.inject.Inject

@HiltViewModel
class RecordsViewModel @Inject constructor(
    private val getUserRecordsUseCase: GetUserRecordsUseCase,
    private val getClinicByQueryUseCase: GetClinicByQueryUseCase,
    private val recordsDatabaseUseCase: RecordsDatabaseUseCase,
    private val userInfoUseCase: UserInfoUseCase
) : BaseSharedViewModel<RecordsUIState, RecordsAction, RecordsEvent>(
    initialState = RecordsUIState(
        isLoading = true,
        selectedTab = RecordsTab.CURRENT,
        current = emptyList(),
        past = listOf(),
        cancelled = emptyList()
    )
) {

    init {
        loadRecords()
    }

    override fun obtainEvent(viewEvent: RecordsEvent) {
        when (viewEvent) {
            is RecordsEvent.OnTabSelected -> handleTabSelected(viewEvent.tab)
            is RecordsEvent.OnFavoriteToggle -> toggleFavorite(viewEvent.recordId, viewEvent.newValue)
            is RecordsEvent.OnPrimaryButtonClick -> handlePrimaryButtonClick()
            is RecordsEvent.OnRecordClick -> openRecordDetails(viewEvent.recordId)
            is RecordsEvent.OnRefresh -> refreshRecords()
        }
    }
    
    private fun loadRecords(isRefresh: Boolean = false) {
        viewModelScope.launch {
            if (isRefresh) {
                updateViewState { state -> state.copy(isRefreshing = true) }
            } else {
                updateViewState { state -> state.copy(isLoading = true) }
            }
            
            try {
                val userEmail = userInfoUseCase.getUserEmail()
                val userEvents = getUserRecordsUseCase.invoke()
                val setOfClinics = userEvents.map { it.clinicName }.toSet()
                val clinics = mutableSetOf<Clinic?>()
                setOfClinics.forEach { clinicName ->
                    clinics += getClinicByQueryUseCase.invoke(clinicName ?: "").firstOrNull()
                }
                val currentTime = LocalDateTime.now()
                val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm")
                
                val records = userEvents.map {
                    RecordUI(
                        id = it.email ?: "",
                        doctorName = "${it.docName} ${it.docSurname}",
                        time = it.start ?: "",
                        specialty = it.docSpecialty ?: "Врач",
                        photoRes = R.drawable.ic_doctor_placeholder,
                        isFavorite = false,
                        address = clinics.find { clinic -> clinic?.name == it.clinicName }?.address ?: "",
                        clinic = it.clinicName ?: "",
                        isConfirmed = it.isConfirmed
                    )
                }
                
                // Save records to database and update cancelled status
                val recordEntities = records.map { it.toEntity(userEmail) }
                recordsDatabaseUseCase.saveRecords(recordEntities)
                
                // Update cancelled status based on server response
                val serverRecordIds = records.map { it.id }
                recordsDatabaseUseCase.updateCancelledStatus(serverRecordIds, userEmail)
                
                // Get cancelled records from database
                val cancelledEntities = recordsDatabaseUseCase.getCancelledRecordsByUser(userEmail)
                val cancelledRecords = cancelledEntities.map { entity ->
                    entity.toRecordUi().copy(isConfirmed = false) // Cancelled records show as not confirmed
                }
                
                val currentRecords = records.filter { record ->
                    try {
                        val recordTime = LocalDateTime.parse(record.time, formatter)
                        recordTime.isAfter(currentTime) || recordTime.isEqual(currentTime)
                    } catch (e: DateTimeParseException) {
                        false
                    }
                }
                
                val pastRecords = records.filter { record ->
                    try {
                        val recordTime = LocalDateTime.parse(record.time, formatter)
                        recordTime.isBefore(currentTime)
                    } catch (e: DateTimeParseException) {
                        false
                    }
                }
                
                updateViewState { state ->
                    state.copy(
                        isLoading = false,
                        isRefreshing = false,
                        current = currentRecords,
                        past = pastRecords,
                        cancelled = cancelledRecords
                    )
                }
            } catch (e: Exception) {
                updateViewState { state ->
                    state.copy(
                        isLoading = false,
                        isRefreshing = false
                    )
                }
            }
        }
    }
    
    private fun refreshRecords() {
        loadRecords(isRefresh = true)
    }
    
    private fun handleTabSelected(tab: RecordsTab) {
        updateViewState { state ->
            state.copy(selectedTab = tab)
        }
    }
    
    private fun toggleFavorite(recordId: String, newValue: Boolean) {
        // Handle favorite toggle logic here
        updateViewState { state ->
            val updatedCurrent = state.current.map { record ->
                if (record.id == recordId) record.copy(isFavorite = newValue) else record
            }
            val updatedPast = state.past.map { record ->
                if (record.id == recordId) record.copy(isFavorite = newValue) else record
            }
            val updatedCancelled = state.cancelled.map { record ->
                if (record.id == recordId) record.copy(isFavorite = newValue) else record
            }
            state.copy(
                current = updatedCurrent,
                past = updatedPast,
                cancelled = updatedCancelled
            )
        }
    }
    
    private fun handlePrimaryButtonClick() {
        val selectedTab = viewStates().value?.selectedTab ?: RecordsTab.CURRENT
        when (selectedTab) {
            RecordsTab.CURRENT -> navigateToSearchDoctor()
            RecordsTab.PAST -> navigateToSearchDoctor()
            RecordsTab.CANCELLED -> navigateToSearchDoctor()
        }
    }
    
    private fun openRecordDetails(recordId: String) {
        sendViewAction(RecordsAction.NavigateToRecordDetails(AppRoutes.ClinicList.route))
    }
    
    private fun navigateToSearchDoctor() {
        sendViewAction(RecordsAction.NavigateToSearchDoctor(AppRoutes.ClinicList.route))
    }
}
