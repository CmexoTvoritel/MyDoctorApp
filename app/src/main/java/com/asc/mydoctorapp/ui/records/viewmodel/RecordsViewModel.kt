package com.asc.mydoctorapp.ui.records.viewmodel

import com.asc.mydoctorapp.R
import com.asc.mydoctorapp.core.data.mapper.toEntity
import com.asc.mydoctorapp.core.data.mapper.toRecordUi
import com.asc.mydoctorapp.core.data.remote.RecordUI
import com.asc.mydoctorapp.core.domain.model.Clinic
import com.asc.mydoctorapp.core.domain.usecase.FavoriteDoctorsUseCase
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
    private val userInfoUseCase: UserInfoUseCase,
    private val favoriteDoctorsUseCase: FavoriteDoctorsUseCase
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
                    val doctorFullName = "${it.docName} ${it.docSurname}"
                    val nameParts = doctorFullName.split(" ")
                    val doctorFirstName = nameParts.firstOrNull() ?: ""
                    val doctorLastName = nameParts.getOrNull(1) ?: ""
                    val favoriteKey = favoriteDoctorsUseCase.generateDoctorEmail(doctorFirstName, doctorLastName)
                    val isFavorite = favoriteDoctorsUseCase.isFavoriteDoctor(userEmail, favoriteKey)
                    
                    RecordUI(
                        id = it.email ?: "",
                        doctorName = doctorFullName,
                        doctorEmail = it.email ?: "", // Используем email врача из записи
                        time = it.start ?: "",
                        specialty = it.docSpecialty ?: "Врач",
                        photoRes = R.drawable.ic_doctor_placeholder,
                        isFavorite = isFavorite,
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
                
                // Разделение прошедших записей по статусу подтверждения
                val pastRecords = records.filter { record ->
                    try {
                        val recordTime = LocalDateTime.parse(record.time, formatter)
                        recordTime.isBefore(currentTime) && record.isConfirmed
                    } catch (e: DateTimeParseException) {
                        false
                    }
                }
                
                // Прошедшие неподтвержденные записи добавляем к отмененным
                val pastUnconfirmedRecords = records.filter { record ->
                    try {
                        val recordTime = LocalDateTime.parse(record.time, formatter)
                        recordTime.isBefore(currentTime) && !record.isConfirmed
                    } catch (e: DateTimeParseException) {
                        false
                    }
                }.map { it.copy(isConfirmed = false) } // Устанавливаем isConfirmed = false для отображения как отмененные
                
                // Объединяем отмененные записи из базы с прошедшими неподтвержденными
                val allCancelledRecords = cancelledRecords + pastUnconfirmedRecords
                
                updateViewState { state ->
                    state.copy(
                        isLoading = false,
                        isRefreshing = false,
                        current = currentRecords,
                        past = pastRecords,
                        cancelled = allCancelledRecords
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
        viewModelScope.launch {
            try {
                val userEmail = userInfoUseCase.invoke().login ?: ""
                
                // Находим запись с указанным ID во всех списках
                val allRecords = (viewStates().value?.current ?: emptyList()) +
                                (viewStates().value?.past ?: emptyList()) +
                                (viewStates().value?.cancelled ?: emptyList())
                
                val record = allRecords.find { it.id == recordId }
                
                if (record != null) {
                    // Извлекаем имя и фамилию врача из полного имени
                    val nameParts = record.doctorName.split(" ")
                    val doctorFirstName = nameParts.firstOrNull() ?: ""
                    val doctorLastName = nameParts.getOrNull(1) ?: ""
                    val favoriteKey = favoriteDoctorsUseCase.generateDoctorEmail(doctorFirstName, doctorLastName)
                    
                    if (newValue) {
                        favoriteDoctorsUseCase.addFavoriteDoctor(
                            userEmail = userEmail,
                            doctorEmail = record.doctorEmail, // Оригинальный email из записи!
                            favoriteKey = favoriteKey,
                            name = doctorFirstName,
                            surname = doctorLastName,
                            specialty = record.specialty,
                            rating = 5.0f,
                            photoRes = record.photoRes,
                            clinic = record.clinic
                        )
                    } else {
                        favoriteDoctorsUseCase.removeFavoriteDoctor(userEmail, favoriteKey)
                    }
                    
                    // Обновляем UI
                    updateViewState { state ->
                        val updatedCurrent = state.current.map { r ->
                            if (r.id == recordId) r.copy(isFavorite = newValue) else r
                        }
                        val updatedPast = state.past.map { r ->
                            if (r.id == recordId) r.copy(isFavorite = newValue) else r
                        }
                        val updatedCancelled = state.cancelled.map { r ->
                            if (r.id == recordId) r.copy(isFavorite = newValue) else r
                        }
                        state.copy(
                            current = updatedCurrent,
                            past = updatedPast,
                            cancelled = updatedCancelled
                        )
                    }
                }
            } catch (e: Exception) {
                // В случае ошибки логируем или показываем пользователю
            }
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
