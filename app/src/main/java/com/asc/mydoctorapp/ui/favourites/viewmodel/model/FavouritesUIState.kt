package com.asc.mydoctorapp.ui.favourites.viewmodel.model

import com.asc.mydoctorapp.core.data.remote.DoctorUIItem

data class FavouritesUIState(
    val isLoading: Boolean = true,
    val doctors: List<DoctorUIItem> = emptyList(),
    val error: String? = null
)

sealed interface FavouritesEvent {
    object LoadFavourites : FavouritesEvent
    object OnBackClick : FavouritesEvent
    data class OnDoctorClick(val doctorEmail: String) : FavouritesEvent
    data class OnFavoriteToggle(val doctorEmail: String, val newValue: Boolean) : FavouritesEvent
}

sealed interface FavouritesAction {
    object NavigateBack : FavouritesAction
    data class NavigateToDoctorDetails(val doctorEmail: String, val clinicName: String) : FavouritesAction
    data class ShowError(val message: String) : FavouritesAction
}
