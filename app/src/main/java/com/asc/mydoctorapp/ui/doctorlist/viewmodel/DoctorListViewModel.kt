package com.asc.mydoctorapp.ui.doctorlist.viewmodel

import com.asc.mydoctorapp.R
import com.asc.mydoctorapp.core.domain.usecase.GetClinicByQueryUseCase
import com.asc.mydoctorapp.core.domain.usecase.GetDoctorsUseCase
import com.asc.mydoctorapp.core.domain.usecase.FavoriteDoctorsUseCase
import com.asc.mydoctorapp.core.domain.usecase.UserInfoUseCase
import com.asc.mydoctorapp.core.data.remote.DoctorUIItem
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
    private val getClinicByQueryUseCase: GetClinicByQueryUseCase,
    private val favoriteDoctorsUseCase: FavoriteDoctorsUseCase,
    private val userInfoUseCase: UserInfoUseCase
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
                val userEmail = userInfoUseCase.invoke().login ?: ""
                val doctorsFromApi = getDoctorsUseCase(clinicName)
                
                val doctors = doctorsFromApi.map { doctor ->
                    val favoriteKey = favoriteDoctorsUseCase.generateDoctorEmail(doctor.name, doctor.surname)
                    val isFavorite = favoriteDoctorsUseCase.isFavoriteDoctor(userEmail, favoriteKey)
                    DoctorUIItem(
                        id = doctor.email,
                        name = doctor.name,
                        surname = doctor.surname,
                        specialty = doctor.specialty,
                        rating = 5.0f,
                        photoRes = R.drawable.ic_doctor_placeholder,
                        isFavorite = isFavorite,
                        clinic = clinicName,
                        favoriteKey = favoriteKey
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
    
    private fun refreshDoctors(clinicName: String) {
        viewModelScope.launch {
            updateViewState { state ->
                state.copy(isRefreshing = true)
            }
            
            try {
                val clinic = getClinicByQueryUseCase(clinicName)
                val doctors = getDoctorsUseCase(clinicName).map { doctor ->
                    val favoriteKey = favoriteDoctorsUseCase.generateDoctorEmail(doctor.name, doctor.surname)
                    DoctorUIItem(
                        id = doctor.email,
                        name = doctor.name,
                        surname = doctor.surname,
                        specialty = doctor.specialty,
                        rating = 5.0f,
                        photoRes = R.drawable.ic_doctor_placeholder,
                        isFavorite = false,
                        clinic = clinicName,
                        favoriteKey = favoriteKey
                    )
                }
                
                updateViewState { state ->
                    state.copy(
                        isRefreshing = false,
                        doctorList = doctors,
                        clinic = clinic.firstOrNull()
                    )
                }
            } catch (e: Exception) {
                // Обработка ошибок загрузки
                updateViewState { state ->
                    state.copy(isRefreshing = false)
                }
            }
        }
    }

    override fun obtainEvent(viewEvent: DoctorEvent) {
        when (viewEvent) {
            is DoctorEvent.InitLoad -> {
                loadDoctors(clinicName = viewEvent.clinicName)
            }
            is DoctorEvent.OnRefresh -> {
                refreshDoctors(viewStates().value?.clinic?.name ?: "Clinic1")
            }
            DoctorEvent.OnBackClick -> {
                sendViewAction(DoctorAction.NavigateBack)
            }
            is DoctorEvent.OnDoctorClick -> {
                navigateToDoctorDetails(viewEvent.doctorId)
            }
            is DoctorEvent.OnFavoriteToggle -> {
                viewModelScope.launch {
                    try {
                        val userEmail = userInfoUseCase.invoke().login ?: ""
                        val doctor = viewStates().value?.doctorList?.find { it.id == viewEvent.doctorId }
                        
                        if (doctor != null) {
                            if (viewEvent.newValue) {
                                // Добавляем в избранное
                                favoriteDoctorsUseCase.addFavoriteDoctor(
                                    userEmail = userEmail,
                                    doctorEmail = doctor.id, // Оригинальный email!
                                    favoriteKey = doctor.favoriteKey,
                                    name = doctor.name,
                                    surname = doctor.surname,
                                    specialty = doctor.specialty,
                                    rating = doctor.rating,
                                    photoRes = doctor.photoRes,
                                    clinic = doctor.clinic
                                )
                            } else {
                                // Удаляем из избранного
                                favoriteDoctorsUseCase.removeFavoriteDoctor(userEmail, doctor.favoriteKey)
                            }
                            
                            // Обновляем UI
                            val updatedDoctors = viewStates().value?.doctorList?.map {
                                if (it.id == viewEvent.doctorId) it.copy(isFavorite = viewEvent.newValue) else it
                            }
                            updateViewState {
                                state -> state.copy(doctorList = updatedDoctors ?: emptyList())
                            }
                        }
                    } catch (e: Exception) {
                        // В случае ошибки логируем или показываем пользователю
                    }
                }
            }
        }
    }
}