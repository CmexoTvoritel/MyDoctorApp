package com.asc.mydoctorapp.ui.splash.viewmodel.model

sealed interface SplashUIState {
}

sealed interface SplashAction {
    data class Navigate(val route: String): SplashAction
}

sealed interface SplashEvent {
}