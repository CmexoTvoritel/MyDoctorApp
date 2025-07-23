package com.asc.mydoctorapp.ui.login.viewmodel

import android.util.Patterns
import com.asc.mydoctorapp.core.domain.usecase.LoginUserUseCase
import com.asc.mydoctorapp.core.utils.PreferencesManager
import com.asc.mydoctorapp.ui.login.viewmodel.model.LoginAction
import com.asc.mydoctorapp.ui.login.viewmodel.model.LoginEvent
import com.asc.mydoctorapp.ui.login.viewmodel.model.LoginUIState
import com.diveomedia.little.stories.bedtime.books.kids.core.ui.viewmodel.BaseSharedViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager,
    private val loginUserUseCase: LoginUserUseCase
): BaseSharedViewModel<LoginUIState, LoginAction, LoginEvent>(
    initialState = LoginUIState()
) {

    override fun obtainEvent(viewEvent: LoginEvent) {
        when (viewEvent) {
            is LoginEvent.OnLoginChanged -> handleLoginChange(viewEvent.login)
            is LoginEvent.OnPasswordChanged -> handlePasswordChange(viewEvent.password)
            is LoginEvent.OnPasswordVisibilityToggle -> togglePasswordVisibility()
            is LoginEvent.OnConsentToggle -> handleConsentToggle(viewEvent.checked)
            is LoginEvent.OnLoginClick -> handleLoginClick()
            is LoginEvent.OnRegisterClick -> {
                // Handle registration navigation in the UI layer
            }
            is LoginEvent.OnVkLoginClick -> {
                // Handle VK login in the UI layer
            }
            else -> {}
        }
    }

    private fun handleLoginChange(login: String) {
        updateViewState { state ->
            state.copy(
                login = login,
                isLoginError = false
            )
        }
    }

    private fun handlePasswordChange(password: String) {
        updateViewState { state ->
            state.copy(
                password = password,
                isPasswordError = false
            )
        }
    }

    private fun togglePasswordVisibility() {
        updateViewState { state ->
            state.copy(
                isPasswordVisible = !state.isPasswordVisible
            )
        }
    }

    private fun handleConsentToggle(checked: Boolean) {
        updateViewState { state ->
            state.copy(
                consentGiven = checked
            )
        }
    }

    private fun handleLoginClick() {
        val login = viewStates().value?.login
        val password = viewStates().value?.password
        val isConsentGiven = viewStates().value?.consentGiven

        if (login == null || password == null || isConsentGiven == null)
            return

        if (!isValidLogin(login)) {
            updateViewState { state ->
                state.copy(
                    isLoginError = true
                )
            }
            sendViewAction(action = LoginAction.ShowError(
                "Неверный формат логина"
            ))
            return
        }

        if (password.isEmpty()) {
            updateViewState { state ->
                state.copy(
                    isPasswordError = true
                )
            }
            sendViewAction(action = LoginAction.ShowError(
                "Пароль не может быть пустым"
            ))
            return
        }

        if (isConsentGiven == false) {
            sendViewAction(action = LoginAction.ShowError(
                "Необходимо дать согласие на обработку персональных данных"
            ))
            return
        }

        // Start login process
        updateViewState { state ->
            state.copy(
                isLoading = true
            )
        }

        // Perform real authentication using the domain layer
        viewModelScope.launch {
            try {
                val userToken = loginUserUseCase(login, password)
                
                // Save token to preferences
                preferencesManager.userToken = userToken.value
                
                updateViewState { state ->
                    state.copy(isLoading = false)
                }
                
                // Navigate to home screen on successful login
                sendViewAction(action = LoginAction.NavigateToHome)
            } catch (e: Exception) {
                updateViewState { state ->
                    state.copy(isLoading = false)
                }
                sendViewAction(action = LoginAction.ShowError(
                    e.message ?: "Ошибка авторизации"
                ))
            }
        }
    }

    private fun isValidLogin(login: String): Boolean {
        return login.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(login).matches()
    }
}