package com.asc.mydoctorapp.ui.profile.viewmodel

import com.asc.mydoctorapp.core.domain.usecase.HardUpdateUserInfoUseCase
import com.asc.mydoctorapp.core.domain.usecase.UserInfoUseCase
import com.asc.mydoctorapp.core.domain.usecase.FavoriteDoctorsUseCase
import com.asc.mydoctorapp.core.utils.PreferencesManager
import com.asc.mydoctorapp.ui.profile.viewmodel.model.ProfileAction
import com.asc.mydoctorapp.ui.profile.viewmodel.model.ProfileEvent
import com.asc.mydoctorapp.ui.profile.viewmodel.model.ProfileUIState
import com.diveomedia.little.stories.bedtime.books.kids.core.ui.viewmodel.BaseSharedViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userInfoUseCase: UserInfoUseCase,
    private val preferencesManager: PreferencesManager,
    private val hardUpdateUserInfo: HardUpdateUserInfoUseCase,
    private val favoriteDoctorsUseCase: FavoriteDoctorsUseCase
) : BaseSharedViewModel<ProfileUIState, ProfileAction, ProfileEvent>(
    initialState = ProfileUIState()
) {

    init {
        loadUserInfo()
    }
    
    fun refreshUserInfo() {
        viewModelScope.launch {
            val profileInfo = hardUpdateUserInfo.invoke()
            val favoritesCount = favoriteDoctorsUseCase.getFavoriteDoctorsCountByUser(profileInfo.login ?: "")
            updateViewState { state ->
                state.copy(
                    userName = profileInfo.name,
                    userLogin = profileInfo.login,
                    userBirth = profileInfo.birth,
                    favoritesCount = favoritesCount
                )
            }
        }
    }
    
    private fun loadUserInfo() {
        viewModelScope.launch {
            val profileInfo = userInfoUseCase.invoke()
            val favoritesCount = favoriteDoctorsUseCase.getFavoriteDoctorsCountByUser(profileInfo.login ?: "")
            updateViewState { state ->
                state.copy(
                    userName = profileInfo.name,
                    userLogin = profileInfo.login,
                    userBirth = profileInfo.birth,
                    favoritesCount = favoritesCount
                )
            }
        }
    }

    private fun refreshProfile() {
        viewModelScope.launch {
            updateViewState { state ->
                state.copy(isRefreshing = true)
            }
            
            try {
                val profileInfo = hardUpdateUserInfo.invoke()
                updateViewState { state ->
                    state.copy(
                        userName = profileInfo.name,
                        userLogin = profileInfo.login,
                        userBirth = profileInfo.birth,
                        isRefreshing = false
                    )
                }
            } catch (e: Exception) {
                updateViewState { state ->
                    state.copy(isRefreshing = false)
                }
            }
        }
    }

    override fun obtainEvent(viewEvent: ProfileEvent) {
        when (viewEvent) {
            is ProfileEvent.OnRefresh -> refreshProfile()
            is ProfileEvent.OnSettingsClick -> navigateToSettings()
            is ProfileEvent.OnFavoritesClick -> navigateToFavorites()
            is ProfileEvent.OnReviewsClick -> navigateToReviews()
            is ProfileEvent.OnMedicalBookClick -> handleMedicalBookClick()
            is ProfileEvent.OnSupportClick -> openChatSupport()
            is ProfileEvent.OnAvatarClick -> chooseNewAvatar()
            is ProfileEvent.OnLogoutClick -> handleLogout()
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

    private fun handleLogout() {
        preferencesManager.userToken = null
        sendViewAction(ProfileAction.Logout)
    }
}
