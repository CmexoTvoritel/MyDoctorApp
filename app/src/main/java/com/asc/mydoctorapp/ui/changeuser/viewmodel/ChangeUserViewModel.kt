package com.asc.mydoctorapp.ui.changeuser.viewmodel

import com.asc.mydoctorapp.core.domain.usecase.ChangeUserInfoUseCase
import com.asc.mydoctorapp.core.domain.usecase.UserInfoUseCase
import com.asc.mydoctorapp.ui.changeuser.viewmodel.model.ChangeUserAction
import com.asc.mydoctorapp.ui.changeuser.viewmodel.model.ChangeUserEvent
import com.asc.mydoctorapp.ui.changeuser.viewmodel.model.ChangeUserUIState
import com.diveomedia.little.stories.bedtime.books.kids.core.ui.viewmodel.BaseSharedViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChangeUserViewModel @Inject constructor(
    private val userInfoUseCase: UserInfoUseCase,
    private val changeUserInfoUseCase: ChangeUserInfoUseCase
): BaseSharedViewModel<ChangeUserUIState, ChangeUserAction, ChangeUserEvent>(
    initialState = ChangeUserUIState()
) {

    init {
        viewModelScope.launch {
            val userInfo = userInfoUseCase.invoke()
            updateViewState { state ->
                state.copy(
                    name = userInfo.name ?: "",
                    email = userInfo.login ?: "",
                    dateOfBirth = userInfo.birth ?: ""
                )
            }
        }
    }

    private fun handleNameChange(name: String) {
        updateViewState { state ->
            state.copy(name = name)
        }
    }

    private fun handleEmailChange(email: String) {
        updateViewState { state ->
            state.copy(email = email)
        }
    }

    private fun handleDateOfBirthChange(dateOfBirth: String) {
        updateViewState { state ->
            state.copy(dateOfBirth = dateOfBirth)
        }
    }

    private fun handleSaveClick() {
        viewModelScope.launch {
            val result = changeUserInfoUseCase.invoke(
                name = viewStates().value?.name,
                login = viewStates().value?.email,
                birth = viewStates().value?.dateOfBirth
            )
            if (result) {
                sendViewAction(ChangeUserAction.OnNavigateAfterSave)
            } else {
                //TODO: show error
            }
        }
    }

    override fun obtainEvent(viewEvent: ChangeUserEvent) {
        when (viewEvent) {
            is ChangeUserEvent.OnNameChange -> handleNameChange(viewEvent.name)
            is ChangeUserEvent.OnEmailChange -> handleEmailChange(viewEvent.email)
            is ChangeUserEvent.OnDateOfBirthChange -> handleDateOfBirthChange(viewEvent.dateOfBirth)
            is ChangeUserEvent.OnSaveClick -> handleSaveClick()
        }
    }
}