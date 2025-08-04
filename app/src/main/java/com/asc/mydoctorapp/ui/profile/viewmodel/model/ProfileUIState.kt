package com.asc.mydoctorapp.ui.profile.viewmodel.model

data class ProfileUIState(
    val userName: String? = null,
    val userLogin: String? = null,
    val userBirth: String? = null,
    val avatarRes: Int? = null,      // drawable или null -> серый circle placeholder
    val favoritesCount: Int = 0,     // 0 = пусто
    val reviewsCount: Int = 0,       // 0 = пусто
    val hasMedicalBook: Boolean = false,
    val isRefreshing: Boolean = false
)

sealed interface ProfileEvent {
    data object OnRefresh : ProfileEvent
    data object OnSettingsClick : ProfileEvent
    data object OnFavoritesClick : ProfileEvent
    data object OnReviewsClick : ProfileEvent
    data object OnMedicalBookClick : ProfileEvent
    data object OnSupportClick : ProfileEvent
    data object OnAvatarClick : ProfileEvent
    data object OnLogoutClick : ProfileEvent
}

sealed interface ProfileAction {
    data object NavigateToSettings : ProfileAction
    data object NavigateToFavorites : ProfileAction
    data object NavigateToReviews : ProfileAction
    data object NavigateToMedicalBookView : ProfileAction
    data object NavigateToMedicalBookCreate : ProfileAction
    data object NavigateToSupportChat : ProfileAction
    data object NavigateToAvatarSelection : ProfileAction
    data object Logout : ProfileAction
}
