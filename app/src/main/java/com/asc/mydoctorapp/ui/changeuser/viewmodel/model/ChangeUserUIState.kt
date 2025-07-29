package com.asc.mydoctorapp.ui.changeuser.viewmodel.model

data class ChangeUserUIState (
    val name: String = "",
    val email: String = "",
    val dateOfBirth: String = "",
    val errorMessage: String? = null
)

sealed interface ChangeUserAction {
    data object OnNavigateAfterSave: ChangeUserAction
    data class ShowError(val message: String): ChangeUserAction
}

sealed interface ChangeUserEvent {
    data class OnNameChange(val name: String): ChangeUserEvent
    data class OnEmailChange(val email: String): ChangeUserEvent
    data class OnDateOfBirthChange(val dateOfBirth: String): ChangeUserEvent
    data object OnSaveClick: ChangeUserEvent
    data object OnErrorShown: ChangeUserEvent
}