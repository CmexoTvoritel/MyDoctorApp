package com.asc.mydoctorapp.ui.home.viewmodel

import androidx.lifecycle.viewModelScope
import com.asc.mydoctorapp.R
import com.asc.mydoctorapp.core.domain.usecase.GetDoctorsUseCase
import com.asc.mydoctorapp.core.utils.PreferencesManager
import com.asc.mydoctorapp.navigation.AppRoutes
import com.asc.mydoctorapp.ui.home.viewmodel.model.DoctorUi
import com.asc.mydoctorapp.ui.home.viewmodel.model.HomeAction
import com.asc.mydoctorapp.ui.home.viewmodel.model.HomeEvent
import com.asc.mydoctorapp.ui.home.viewmodel.model.HomeUIState
import com.diveomedia.little.stories.bedtime.books.kids.core.ui.viewmodel.BaseSharedViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager,
    private val getDoctorsUseCase: GetDoctorsUseCase
) : BaseSharedViewModel<HomeUIState, HomeAction, HomeEvent>(
    initialState = HomeUIState()
) {
    
    init {
        loadDoctors()
    }
    
    private fun loadDoctors() {
        viewModelScope.launch {
            try {
                val doctors = getDoctorsUseCase("Clinic1")
                    .take(5) // Показываем только первые 5 докторов
                    .map { doctor ->
                        DoctorUi(
                            id = doctor.email, // Используем email как ID
                            name = doctor.name,
                            surname = doctor.surname,
                            specialty = doctor.specialty,
                            rating = 5.0f,
                            photoRes = R.drawable.ic_doctor_placeholder
                        )
                    }
                
                updateViewState { state ->
                    state.copy(doctors = doctors)
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
        }
    }

    private fun handleQueryChange(text: String) {
        updateViewState { state ->
            state.copy(
                query = text
            )
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
        sendViewAction(HomeAction.NavigateToSpecialistsList(AppRoutes.DoctorList.route))
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
}
