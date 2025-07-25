package com.asc.mydoctorapp.ui.registration.viewmodel

import android.util.Patterns
import androidx.lifecycle.viewModelScope
import com.asc.mydoctorapp.core.domain.usecase.RegisterUserUseCase
import com.asc.mydoctorapp.core.utils.PreferencesManager
import com.asc.mydoctorapp.ui.registration.viewmodel.model.RegistrationAction
import com.asc.mydoctorapp.ui.registration.viewmodel.model.RegistrationEvent
import com.asc.mydoctorapp.ui.registration.viewmodel.model.RegistrationUIState
import com.diveomedia.little.stories.bedtime.books.kids.core.ui.viewmodel.BaseSharedViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager,
    private val registerUserUseCase: RegisterUserUseCase
): BaseSharedViewModel<RegistrationUIState, RegistrationAction, RegistrationEvent>(
    initialState = RegistrationUIState()
) {

    override fun obtainEvent(viewEvent: RegistrationEvent) {
        when (viewEvent) {
            is RegistrationEvent.OnLoginChanged -> handleLoginChange(viewEvent.login)
            is RegistrationEvent.OnNameChanged -> handleNameChange(viewEvent.name)
            is RegistrationEvent.OnBirthDateChanged -> handleBirthDateChange(viewEvent.birthDate)
            is RegistrationEvent.OnPasswordChanged -> handlePasswordChange(viewEvent.password)
            is RegistrationEvent.OnPasswordVisibilityToggle -> togglePasswordVisibility()
            is RegistrationEvent.OnConsentToggle -> handleConsentToggle(viewEvent.checked)
            is RegistrationEvent.OnRegisterClick -> handleRegisterClick()
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

    private fun handleNameChange(name: String) {
        updateViewState { state ->
            state.copy(
                name = name,
                isNameError = false
            )
        }
    }

    private fun handleBirthDateChange(birthDate: String) {
        updateViewState { state ->
            state.copy(
                birthDate = birthDate,
                isBirthDateError = false
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

    private fun handleRegisterClick() {
        val login = viewStates().value?.login
        val name = viewStates().value?.name
        val birthDate = viewStates().value?.birthDate
        val password = viewStates().value?.password
        val isConsentGiven = viewStates().value?.consentGiven

        if (login == null || name == null || birthDate == null || password == null || isConsentGiven == null)
            return

        var hasError = false

        if (!isValidLogin(login)) {
            updateViewState { state ->
                state.copy(
                    isLoginError = true
                )
            }
            sendViewAction(action = RegistrationAction.ShowError(
                "Неверный формат логина"
            ))
            hasError = true
        }

        if (name.isEmpty()) {
            updateViewState { state ->
                state.copy(
                    isNameError = true
                )
            }
            sendViewAction(action = RegistrationAction.ShowError(
                "Имя не может быть пустым"
            ))
            hasError = true
        }

        if (birthDate.isEmpty()) {
            updateViewState { state ->
                state.copy(
                    isBirthDateError = true
                )
            }
            sendViewAction(action = RegistrationAction.ShowError(
                "Укажите дату рождения"
            ))
            hasError = true
        }

        if (password.isEmpty()) {
            updateViewState { state ->
                state.copy(
                    isPasswordError = true
                )
            }
            sendViewAction(action = RegistrationAction.ShowError(
                "Пароль не может быть пустым"
            ))
            hasError = true
        }

        if (isConsentGiven == false) {
            sendViewAction(action = RegistrationAction.ShowError(
                "Необходимо дать согласие на обработку персональных данных"
            ))
            hasError = true
        }

        if (hasError) return

        // Start registration process
        updateViewState { state ->
            state.copy(
                isLoading = true
            )
        }

        viewModelScope.launch {
            try {
                // Parse the birth date from string in format DD.MM.YYYY
                val birthDateFormatted = try {
                    LocalDate.parse(birthDate, DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                } catch (e: DateTimeParseException) {
                    updateViewState { state ->
                        state.copy(
                            isLoading = false,
                            isBirthDateError = true
                        )
                    }
                    sendViewAction(action = RegistrationAction.ShowError(
                        "Неверный формат даты рождения. Используйте формат ДД.ММ.ГГГГ"
                    ))
                    return@launch
                }

                // Call the use case to register the user
                val userToken = registerUserUseCase(name, birthDateFormatted, login, password)
                
                // Save the token to preferences
                preferencesManager.userToken = userToken.value
                
                updateViewState { state ->
                    state.copy(isLoading = false)
                }
                
                // Navigate to home screen
                sendViewAction(action = RegistrationAction.NavigateToHome)
            } catch (e: Exception) {
                updateViewState { state ->
                    state.copy(isLoading = false)
                }
                sendViewAction(action = RegistrationAction.ShowError(
                    e.message ?: "Ошибка регистрации"
                ))
            }
        }
    }

    private fun isValidLogin(login: String): Boolean {
        return login.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(login).matches()
    }
}
