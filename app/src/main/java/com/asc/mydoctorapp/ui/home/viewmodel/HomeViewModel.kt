package com.asc.mydoctorapp.ui.home.viewmodel

import com.asc.mydoctorapp.R
import com.asc.mydoctorapp.core.utils.PreferencesManager
import com.asc.mydoctorapp.ui.home.viewmodel.model.DoctorUi
import com.asc.mydoctorapp.ui.home.viewmodel.model.HomeAction
import com.asc.mydoctorapp.ui.home.viewmodel.model.HomeEvent
import com.asc.mydoctorapp.ui.home.viewmodel.model.HomeUIState
import com.diveomedia.little.stories.bedtime.books.kids.core.ui.viewmodel.BaseSharedViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager
) : BaseSharedViewModel<HomeUIState, HomeAction, HomeEvent>(
    initialState = HomeUIState(
        doctors = listOf(
            DoctorUi(
                id = "1",
                name = "Иван Сидоров",
                specialty = "кардиолог",
                rating = 5.0f,
                photoRes = R.drawable.ic_doctor_placeholder
            ),
            DoctorUi(
                id = "2",
                name = "Мария Петрова",
                specialty = "гинеколог",
                rating = 5.0f,
                photoRes = R.drawable.ic_doctor_placeholder
            ),
            DoctorUi(
                id = "3",
                name = "Илья Петров",
                specialty = "гастроэнтеролог",
                rating = 4.9f,
                photoRes = R.drawable.ic_doctor_placeholder
            ),
            DoctorUi(
                id = "4",
                name = "Анна Смирнова",
                specialty = "невролог",
                rating = 4.8f,
                photoRes = R.drawable.ic_doctor_placeholder
            ),
            DoctorUi(
                id = "5",
                name = "Сергей Козлов",
                specialty = "окулист",
                rating = 4.7f,
                photoRes = R.drawable.ic_doctor_placeholder
            )
        )
    )
) {

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
        sendViewAction(HomeAction.NavigateToChat("ai_chat"))
    }

    private fun handleSeeAllSpecialists() {
        sendViewAction(HomeAction.NavigateToSpecialistsList("all_specialists"))
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
        sendViewAction(HomeAction.NavigateToFaq("faq"))
    }
}
