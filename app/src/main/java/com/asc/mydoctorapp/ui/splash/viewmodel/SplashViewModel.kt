package com.asc.mydoctorapp.ui.splash.viewmodel

import com.asc.mydoctorapp.core.utils.PreferencesManager
import com.asc.mydoctorapp.navigation.AppRoutes
import com.asc.mydoctorapp.ui.splash.viewmodel.model.SplashAction
import com.asc.mydoctorapp.ui.splash.viewmodel.model.SplashEvent
import com.asc.mydoctorapp.ui.splash.viewmodel.model.SplashUIState
import com.diveomedia.little.stories.bedtime.books.kids.core.ui.viewmodel.BaseSharedViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager
) : BaseSharedViewModel<SplashUIState, SplashAction, SplashEvent>() {

    init {
        val startDestination = if (preferencesManager.userToken != null) {
            AppRoutes.Home.route
        } else if (preferencesManager.isOnboardingShown) {
            AppRoutes.Login.route
        } else {
            AppRoutes.OnBoarding.route
        }
        sendViewAction(action = SplashAction.Navigate(startDestination))
    }

    override fun obtainEvent(viewEvent: SplashEvent) {
        when (viewEvent) {
            else -> {}
        }
    }
}