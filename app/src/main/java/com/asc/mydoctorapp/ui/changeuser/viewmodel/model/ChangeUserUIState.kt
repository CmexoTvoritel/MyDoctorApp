package com.asc.mydoctorapp.ui.changeuser.viewmodel.model

data class ChangeUserUIState (
    val name: String = "",
    val email: String = "",
    val dateOfBirth: String = "",
)

sealed interface ChangeUserAction {
    data object OnNavigateAfterSave: ChangeUserAction
}

sealed interface ChangeUserEvent {
    data class OnNameChange(val name: String): ChangeUserEvent
    data class OnEmailChange(val email: String): ChangeUserEvent
    data class OnDateOfBirthChange(val dateOfBirth: String): ChangeUserEvent
    data object OnSaveClick: ChangeUserEvent
}