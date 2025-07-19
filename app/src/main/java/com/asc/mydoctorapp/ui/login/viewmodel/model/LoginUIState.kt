package com.asc.mydoctorapp.ui.login.viewmodel.model

data class LoginUIState(
    val login: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val consentGiven: Boolean = false,
    val isLoginError: Boolean = false,
    val isPasswordError: Boolean = false,
    val isPasswordVisible: Boolean = false
)

sealed interface LoginAction {
    object NavigateToHome : LoginAction
    data class ShowError(val message: String) : LoginAction
}

sealed interface LoginEvent {
    data class OnLoginChanged(val login: String) : LoginEvent
    data class OnPasswordChanged(val password: String) : LoginEvent
    object OnPasswordVisibilityToggle : LoginEvent
    object OnLoginClick : LoginEvent
    data class OnConsentToggle(val checked: Boolean) : LoginEvent
    object OnRegisterClick : LoginEvent
    object OnVkLoginClick : LoginEvent
}