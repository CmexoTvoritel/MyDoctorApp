package com.asc.mydoctorapp.ui.profile.viewmodel.model

/**
 * Состояние UI для экрана профиля
 */
data class ProfileUIState(
    val userName: String = "Иван",
    val avatarRes: Int? = null,      // drawable или null -> серый circle placeholder
    val favoritesCount: Int = 0,     // 0 = пусто
    val reviewsCount: Int = 0,       // 0 = пусто
    val hasMedicalBook: Boolean = false
)

/**
 * События, которые могут происходить на экране профиля
 */
sealed interface ProfileEvent {
    object OnSettingsClick : ProfileEvent
    object OnFavoritesClick : ProfileEvent
    object OnReviewsClick : ProfileEvent
    object OnMedicalBookClick : ProfileEvent
    object OnSupportClick : ProfileEvent
    object OnAvatarClick : ProfileEvent
}

/**
 * Действия для навигации из экрана профиля
 */
sealed interface ProfileAction {
    object NavigateToSettings : ProfileAction
    object NavigateToFavorites : ProfileAction
    object NavigateToReviews : ProfileAction
    object NavigateToMedicalBookView : ProfileAction
    object NavigateToMedicalBookCreate : ProfileAction
    object NavigateToSupportChat : ProfileAction
    object NavigateToAvatarSelection : ProfileAction
}
