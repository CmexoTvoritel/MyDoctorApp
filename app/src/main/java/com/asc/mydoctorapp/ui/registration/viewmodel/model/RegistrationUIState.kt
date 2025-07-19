package com.asc.mydoctorapp.ui.registration.viewmodel.model

data class RegistrationUIState(
    val login: String = "",
    val name: String = "",
    val birthDate: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val consentGiven: Boolean = false,
    val isLoginError: Boolean = false,
    val isNameError: Boolean = false,
    val isBirthDateError: Boolean = false,
    val isPasswordError: Boolean = false,
    val isPasswordVisible: Boolean = false
)

sealed interface RegistrationAction {
    object NavigateToHome : RegistrationAction
    data class ShowError(val message: String) : RegistrationAction
}

sealed interface RegistrationEvent {
    data class OnLoginChanged(val login: String) : RegistrationEvent
    data class OnNameChanged(val name: String) : RegistrationEvent
    data class OnBirthDateChanged(val birthDate: String) : RegistrationEvent
    data class OnPasswordChanged(val password: String) : RegistrationEvent
    object OnPasswordVisibilityToggle : RegistrationEvent
    object OnRegisterClick : RegistrationEvent
    data class OnConsentToggle(val checked: Boolean) : RegistrationEvent
}
