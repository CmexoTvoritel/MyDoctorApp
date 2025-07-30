package com.asc.mydoctorapp.ui.home.viewmodel

import androidx.lifecycle.viewModelScope
import com.asc.mydoctorapp.R
import com.asc.mydoctorapp.core.domain.usecase.GetAllClinicsUseCase
import com.asc.mydoctorapp.core.domain.usecase.GetClinicByQueryUseCase
import com.asc.mydoctorapp.core.domain.usecase.GetDoctorsUseCase
import com.asc.mydoctorapp.core.domain.usecase.UserInfoUseCase
import com.asc.mydoctorapp.core.utils.PreferencesManager
import com.asc.mydoctorapp.navigation.AppRoutes
import com.asc.mydoctorapp.ui.home.viewmodel.model.DoctorUi
import com.asc.mydoctorapp.ui.home.viewmodel.model.HomeAction
import com.asc.mydoctorapp.ui.home.viewmodel.model.HomeEvent
import com.asc.mydoctorapp.ui.home.viewmodel.model.HomeUIState
import com.asc.mydoctorapp.ui.home.viewmodel.model.HomeUIStateType
import com.diveomedia.little.stories.bedtime.books.kids.core.ui.viewmodel.BaseSharedViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager,
    private val getDoctorsUseCase: GetDoctorsUseCase,
    private val getAllClinicsUseCase: GetAllClinicsUseCase,
    private val userInfoUseCase: UserInfoUseCase,
    private val getClinicByQueryUseCase: GetClinicByQueryUseCase
) : BaseSharedViewModel<HomeUIState, HomeAction, HomeEvent>(
    initialState = HomeUIState()
) {

    private var searchJob: Job? = null
    
    init {
        loadClinics()
        loadUserInfo()
    }

    private fun loadUserInfo() = viewModelScope.launch {
        userInfoUseCase.invoke()
    }
    
    private fun loadClinics() {
        viewModelScope.launch {
            try {
                val clinics = getAllClinicsUseCase()
//                val doctors = getDoctorsUseCase("Clinic1")
//                    .take(5) // Показываем только первые 5 докторов
//                    .map { doctor ->
//                        DoctorUi(
//                            id = doctor.email, // Используем email как ID
//                            name = doctor.name,
//                            surname = doctor.surname,
//                            specialty = doctor.specialty,
//                            rating = 5.0f,
//                            photoRes = R.drawable.ic_doctor_placeholder
//                        )
//                    }
                
                updateViewState { state ->
                    state.copy(clinicsMain = clinics)
                }
            } catch (e: Exception) {
                // Обработка ошибок загрузки
                // Можно добавить состояние ошибки в HomeUIState если нужно
            }
        }
    }

    override fun obtainEvent(viewEvent: HomeEvent) {
        when (viewEvent) {
            is HomeEvent.OnQueryChanged -> handleQueryChange(viewEvent.text)
            is HomeEvent.OnSearchSubmit -> handleSearchSubmit()
            is HomeEvent.OnAiChatStartClick -> handleAiChatStart()
            is HomeEvent.OnSeeAllSpecialistsClick -> handleSeeAllSpecialists()
            is HomeEvent.OnDoctorCardClick -> handleDoctorCardClick(viewEvent.doctorId)
            is HomeEvent.OnDoctorFavoriteToggle -> handleDoctorFavoriteToggle(viewEvent.doctorId, viewEvent.isFavorite)
            is HomeEvent.OnFaqClick -> handleFaqClick()
            is HomeEvent.OnClinicCardClick -> handleClinicCardClick(viewEvent.clinicName)
        }
    }

    private fun handleQueryChange(text: String) = viewModelScope.launch {
        updateViewState { state ->
            state.copy(
                query = text,
                screenState = if (text.isEmpty()) HomeUIStateType.MAIN else HomeUIStateType.SEARCH
            )
        }
        searchJob?.cancel()

        // если строка пустая — чистим результаты и выходим
        if (text.isBlank()) {
            updateViewState { it.copy(clinics = emptyList() /*, isSearching = false */) }
            return@launch
        }

        searchJob = viewModelScope.launch(Dispatchers.IO) {
            try {
                val clinics = getClinicByQueryUseCase(text)
                withContext(Dispatchers.Main) {
                    updateViewState { it.copy(clinics = clinics /*, isSearching = false */) }
                }
            } catch (ce: CancellationException) {
                throw ce
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    updateViewState { it.copy(clinics = emptyList() /*, isSearching = false */) }
                }
            }
        }
    }

    private fun handleSearchSubmit() {
        val query = viewStates().value?.query ?: ""
        if (query.isNotEmpty()) {
            updateViewState { state ->
                state.copy(
                    isSearching = true
                )
            }
            
            // Здесь был бы запрос к API с поиском докторов
            // Для демо просто имитируем задержку и обновляем состояние
            
            // Симулируем завершение поиска
            updateViewState { state ->
                state.copy(
                    isSearching = false
                )
            }
        }
    }

    private fun handleAiChatStart() {
        sendViewAction(HomeAction.NavigateToChat(AppRoutes.Chat.route))
    }

    private fun handleSeeAllSpecialists() {
        //val route = AppRoutes.DoctorList.route.replace("{clinicName}", "Clinic1")
        sendViewAction(action = HomeAction.NavigateToSpecialistsList(AppRoutes.ClinicList.route))
    }

    private fun handleDoctorCardClick(doctorId: String) {
        sendViewAction(HomeAction.NavigateToDoctorProfile(doctorId))
    }

    private fun handleDoctorFavoriteToggle(doctorId: String, isFavorite: Boolean) {
        updateViewState { state ->
            val currentFavorites = state.favorites.toMutableSet()
            if (isFavorite) {
                currentFavorites.add(doctorId)
            } else {
                currentFavorites.remove(doctorId)
            }
            state.copy(
                favorites = currentFavorites
            )
        }
    }

    private fun handleFaqClick() {

    }

    private fun handleClinicCardClick(clinicName: String) {
        val route = AppRoutes.DoctorList.route.replace("{clinicName}", clinicName)
        sendViewAction(action = HomeAction.NavigateToSpecialistsList(route))
    }
}
