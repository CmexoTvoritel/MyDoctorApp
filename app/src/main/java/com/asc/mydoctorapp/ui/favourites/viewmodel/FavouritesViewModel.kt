package com.asc.mydoctorapp.ui.favourites.viewmodel

import com.asc.mydoctorapp.core.domain.usecase.FavoriteDoctorsUseCase
import com.asc.mydoctorapp.core.domain.usecase.UserInfoUseCase
import com.asc.mydoctorapp.ui.favourites.viewmodel.model.FavouritesAction
import com.asc.mydoctorapp.ui.favourites.viewmodel.model.FavouritesEvent
import com.asc.mydoctorapp.ui.favourites.viewmodel.model.FavouritesUIState
import com.diveomedia.little.stories.bedtime.books.kids.core.ui.viewmodel.BaseSharedViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavouritesViewModel @Inject constructor(
    private val favoriteDoctorsUseCase: FavoriteDoctorsUseCase,
    private val userInfoUseCase: UserInfoUseCase
) : BaseSharedViewModel<FavouritesUIState, FavouritesAction, FavouritesEvent>(
    initialState = FavouritesUIState()
) {

    init {
        loadFavourites()
    }

    override fun obtainEvent(viewEvent: FavouritesEvent) {
        when (viewEvent) {
            is FavouritesEvent.LoadFavourites -> loadFavourites()
            is FavouritesEvent.OnBackClick -> handleBackClick()
            is FavouritesEvent.OnDoctorClick -> handleDoctorClick(viewEvent.doctorEmail)
            is FavouritesEvent.OnFavoriteToggle -> handleFavoriteToggle(viewEvent.doctorEmail, viewEvent.newValue)
        }
    }

    private fun loadFavourites() {
        viewModelScope.launch {
            updateViewState { state ->
                state.copy(isLoading = true, error = null)
            }
            
            try {
                val userEmail = userInfoUseCase.invoke().login ?: ""
                val favoriteDoctors = favoriteDoctorsUseCase.getFavoriteDoctorsByUser(userEmail)
                
                updateViewState { state ->
                    state.copy(
                        isLoading = false,
                        doctors = favoriteDoctors,
                        error = null
                    )
                }
            } catch (e: Exception) {
                updateViewState { state ->
                    state.copy(
                        isLoading = false,
                        error = e.message ?: "Ошибка загрузки избранных врачей"
                    )
                }
                sendViewAction(FavouritesAction.ShowError(e.message ?: "Ошибка загрузки"))
            }
        }
    }

    private fun handleBackClick() {
        sendViewAction(FavouritesAction.NavigateBack)
    }

    private fun handleDoctorClick(doctorEmail: String) {
        val doctor = viewStates().value?.doctors?.find { it.id == doctorEmail }
        if (doctor != null) {
            sendViewAction(FavouritesAction.NavigateToDoctorDetails(doctor.id, doctor.clinic))
        }
    }

    private fun handleFavoriteToggle(doctorEmail: String, newValue: Boolean) {
        viewModelScope.launch {
            try {
                val userEmail = userInfoUseCase.invoke().login ?: ""
                val doctor = viewStates().value?.doctors?.find { it.id == doctorEmail }
                
                if (!newValue && doctor != null) {
                    // Удаляем из избранного используя favoriteKey
                    favoriteDoctorsUseCase.removeFavoriteDoctor(userEmail, doctor.favoriteKey)
                    
                    // Обновляем UI - убираем врача из списка
                    updateViewState { state ->
                        state.copy(
                            doctors = state.doctors.filter { it.id != doctorEmail }
                        )
                    }
                }
            } catch (e: Exception) {
                sendViewAction(FavouritesAction.ShowError("Ошибка при удалении из избранного"))
            }
        }
    }
}
