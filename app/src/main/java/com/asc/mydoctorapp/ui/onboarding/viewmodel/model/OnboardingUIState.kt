package com.asc.mydoctorapp.ui.onboarding.viewmodel.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class OnboardingUIState(
    val currentPage: Int = 0,
    val pages: List<OnboardingPage> = emptyList()
)

data class OnboardingPage(
    @DrawableRes val imageRes: Int,
    @StringRes val title: Int = 0,
    @StringRes val subtitle: Int = 0
)

sealed interface OnboardingAction {
    data object NavigateToLogin : OnboardingAction
}

sealed interface OnboardingEvent {
    data object NextPage : OnboardingEvent
    data class NavigateToPage(val page: Int) : OnboardingEvent
    data object CompleteOnboarding : OnboardingEvent
}