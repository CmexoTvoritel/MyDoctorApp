package com.asc.mydoctorapp.ui.profile.viewmodel

import com.asc.mydoctorapp.ui.profile.viewmodel.model.ProfileAction
import com.asc.mydoctorapp.ui.profile.viewmodel.model.ProfileEvent
import com.asc.mydoctorapp.ui.profile.viewmodel.model.ProfileUIState
import com.diveomedia.little.stories.bedtime.books.kids.core.ui.viewmodel.BaseSharedViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor() : BaseSharedViewModel<ProfileUIState, ProfileAction, ProfileEvent>(
    initialState = ProfileUIState(
        userName = "Иван",
        avatarRes = null,
        favoritesCount = 0,
        reviewsCount = 0,
        hasMedicalBook = false
    )
) {

    override fun obtainEvent(viewEvent: ProfileEvent) {
        when (viewEvent) {
            is ProfileEvent.OnSettingsClick -> navigateToSettings()
            is ProfileEvent.OnFavoritesClick -> navigateToFavorites()
            is ProfileEvent.OnReviewsClick -> navigateToReviews()
            is ProfileEvent.OnMedicalBookClick -> handleMedicalBookClick()
            is ProfileEvent.OnSupportClick -> openChatSupport()
            is ProfileEvent.OnAvatarClick -> chooseNewAvatar()
        }
    }

    private fun navigateToSettings() {
        sendViewAction(ProfileAction.NavigateToSettings)
    }

    private fun navigateToFavorites() {
        sendViewAction(ProfileAction.NavigateToFavorites)
    }

    private fun navigateToReviews() {
        sendViewAction(ProfileAction.NavigateToReviews)
    }

    private fun handleMedicalBookClick() {
        val hasMedicalBook = viewStates().value?.hasMedicalBook ?: false
        if (hasMedicalBook) {
            sendViewAction(ProfileAction.NavigateToMedicalBookView)
        } else {
            sendViewAction(ProfileAction.NavigateToMedicalBookCreate)
        }
    }

    private fun openChatSupport() {
        sendViewAction(ProfileAction.NavigateToSupportChat)
    }

    private fun chooseNewAvatar() {
        sendViewAction(ProfileAction.NavigateToAvatarSelection)
    }

    // Для возможного использования в будущем
    fun updateProfile(
        userName: String? = null,
        avatarRes: Int? = null,
        favoritesCount: Int? = null,
        reviewsCount: Int? = null,
        hasMedicalBook: Boolean? = null
    ) {
        updateViewState { state ->
            state.copy(
                userName = userName ?: state.userName,
                avatarRes = avatarRes ?: state.avatarRes,
                favoritesCount = favoritesCount ?: state.favoritesCount,
                reviewsCount = reviewsCount ?: state.reviewsCount,
                hasMedicalBook = hasMedicalBook ?: state.hasMedicalBook
            )
        }
    }
}
