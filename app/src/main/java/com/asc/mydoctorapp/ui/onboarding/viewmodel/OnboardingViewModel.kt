package com.asc.mydoctorapp.ui.onboarding.viewmodel

import com.asc.mydoctorapp.R
import com.asc.mydoctorapp.core.utils.PreferencesManager
import com.asc.mydoctorapp.ui.onboarding.viewmodel.model.OnboardingAction
import com.asc.mydoctorapp.ui.onboarding.viewmodel.model.OnboardingEvent
import com.asc.mydoctorapp.ui.onboarding.viewmodel.model.OnboardingPage
import com.asc.mydoctorapp.ui.onboarding.viewmodel.model.OnboardingUIState
import com.diveomedia.little.stories.bedtime.books.kids.core.ui.viewmodel.BaseSharedViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager
): BaseSharedViewModel<OnboardingUIState, OnboardingAction, OnboardingEvent>(
    initialState = OnboardingUIState()
) {

    init {
        initPages()
    }

    private fun initPages() {
        val pages = listOf(
            OnboardingPage(
                imageRes = R.drawable.ic_onboarding_main_1,
                title = R.string.onboarding_page_1_title,
                subtitle = R.string.onboarding_page_1_subtitle
            ),
            OnboardingPage(
                imageRes = R.drawable.ic_onboarding_main_2,
                title = R.string.onboarding_page_2_title,
                subtitle = R.string.onboarding_page_2_subtitle
            ),
            OnboardingPage(
                imageRes = R.drawable.ic_onboarding_main_3,
                title = R.string.onboarding_page_3_title,
                subtitle = R.string.onboarding_page_3_subtitle
            ),
            OnboardingPage(
                imageRes = R.drawable.ic_onboarding_main_4,
                title = R.string.onboarding_page_4_title,
                subtitle = R.string.onboarding_page_4_subtitle
            ),
            OnboardingPage(
                imageRes = R.drawable.ic_onboarding_main_5,
                title = R.string.onboarding_page_5_title,
                subtitle = R.string.onboarding_page_5_subtitle
            )
        )
        
        updateViewState { state ->
            state.copy(pages = pages)
        }
    }

    override fun obtainEvent(viewEvent: OnboardingEvent) {
        when (viewEvent) {
            is OnboardingEvent.NextPage -> handleNextPage()
            is OnboardingEvent.NavigateToPage -> navigateToPage(viewEvent.page)
            is OnboardingEvent.CompleteOnboarding -> completeOnboarding()
        }
    }

    private fun handleNextPage() {
        val currentPage = viewState?.currentPage ?: 0
        val totalPages = viewState?.pages?.size ?: 0
        
        if (currentPage < totalPages - 1) {
            navigateToPage(currentPage + 1)
        } else {
            completeOnboarding()
        }
    }

    private fun navigateToPage(page: Int) {
        updateViewState { state ->
            state.copy(currentPage = page)
        }
    }

    private fun completeOnboarding() {
        viewModelScope.launch {
            // Mark onboarding as completed in preferences
            preferencesManager.isOnboardingShown = true
            
            // Navigate to login screen
            sendViewAction(action = OnboardingAction.NavigateToLogin)
        }
    }
}