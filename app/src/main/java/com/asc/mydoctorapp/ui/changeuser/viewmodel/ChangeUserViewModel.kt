package com.asc.mydoctorapp.ui.changeuser.viewmodel

import android.util.Patterns
import com.asc.mydoctorapp.core.domain.usecase.ChangeUserInfoUseCase
import com.asc.mydoctorapp.core.domain.usecase.RecordsDatabaseUseCase
import com.asc.mydoctorapp.core.domain.usecase.UserInfoUseCase
import com.asc.mydoctorapp.ui.changeuser.viewmodel.model.ChangeUserAction
import com.asc.mydoctorapp.ui.changeuser.viewmodel.model.ChangeUserEvent
import com.asc.mydoctorapp.ui.changeuser.viewmodel.model.ChangeUserUIState
import com.diveomedia.little.stories.bedtime.books.kids.core.ui.viewmodel.BaseSharedViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ChangeUserViewModel @Inject constructor(
    private val userInfoUseCase: UserInfoUseCase,
    private val changeUserInfoUseCase: ChangeUserInfoUseCase,
    private val recordsDatabaseUseCase: RecordsDatabaseUseCase
): BaseSharedViewModel<ChangeUserUIState, ChangeUserAction, ChangeUserEvent>(
    initialState = ChangeUserUIState()
) {

    private var originalEmail: String = ""

    init {
        viewModelScope.launch {
            val userInfo = userInfoUseCase.invoke()
            originalEmail = userInfo.login ?: ""
            updateViewState { state ->
                state.copy(
                    name = userInfo.name ?: "",
                    email = userInfo.login ?: "",
                    dateOfBirth = formatDateFromServer(userInfo.birth ?: "")
                )
            }
        }
    }

    private fun formatDateFromServer(serverDate: String): String {
        if (serverDate.isEmpty()) return ""
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            val date = inputFormat.parse(serverDate)
            outputFormat.format(date ?: Date())
        } catch (e: Exception) {
            serverDate
        }
    }

    private fun formatDateForServer(displayDate: String): String {
        if (displayDate.isEmpty()) return ""
        return try {
            val inputFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = inputFormat.parse(displayDate)
            outputFormat.format(date ?: Date())
        } catch (e: Exception) {
            displayDate
        }
    }

    private fun handleNameChange(name: String) {
        val filteredName = name.filter { char ->
            char.isLetter() && (char in 'а'..'я' || char in 'А'..'Я' || char in 'ё'..'ё' || char in 'Ё'..'Ё' || char.isWhitespace())
        }
        updateViewState { state ->
            state.copy(name = filteredName)
        }
    }

    private fun handleEmailChange(email: String) {
        val filteredEmail = email.filter { char ->
            char.isLetterOrDigit() || char in "@.-_"
        }
        updateViewState { state ->
            state.copy(email = filteredEmail)
        }
    }

    private fun handleDateOfBirthChange(dateOfBirth: String) {
        val filteredDate = dateOfBirth.filter { char ->
            char.isDigit() || char == '.'
        }
        updateViewState { state ->
            state.copy(dateOfBirth = filteredDate)
        }
    }

    private fun handleSaveClick() {
        val currentState = viewStates().value ?: return
        
        // Validate name (Cyrillic only)
        if (!isValidName(currentState.name)) {
            sendViewAction(ChangeUserAction.ShowError("Имя должно содержать только кириллицу"))
            return
        }
        
        // Validate email
        if (!isValidEmail(currentState.email)) {
            sendViewAction(ChangeUserAction.ShowError("Введите корректный email"))
            return
        }
        
        // Validate date
        if (!isValidDate(currentState.dateOfBirth)) {
            sendViewAction(ChangeUserAction.ShowError("Введите корректную дату рождения в формате ДД.ММ.ГГГГ"))
            return
        }
        
        viewModelScope.launch {
            val result = changeUserInfoUseCase.invoke(
                name = currentState.name,
                login = currentState.email,
                birth = formatDateForServer(currentState.dateOfBirth)
            )
            if (result) {
                // Обновляем email в базе данных записей если он изменился
                if (currentState.email != originalEmail) {
                    recordsDatabaseUseCase.updateUserEmail(originalEmail, currentState.email)
                }
                sendViewAction(ChangeUserAction.OnNavigateAfterSave)
            } else {
                sendViewAction(ChangeUserAction.ShowError("Ошибка при сохранении данных"))
            }
        }
    }

    private fun isValidName(name: String): Boolean {
        if (name.isEmpty()) return false
        return name.all { char ->
            char.isWhitespace() || char in 'а'..'я' || char in 'А'..'Я' || char in 'ё'..'ё' || char in 'Ё'..'Ё'
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isValidDate(date: String): Boolean {
        if (date.isEmpty()) return false
        return try {
            val format = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            format.isLenient = false
            val parsedDate = format.parse(date) ?: return false
            
            // Check if date is not in the future
            val today = Calendar.getInstance()
            val inputDate = Calendar.getInstance()
            inputDate.time = parsedDate
            
            // Date should be before today (not today or future)
            inputDate.before(today)
        } catch (e: Exception) {
            false
        }
    }

    private fun handleErrorShown() {
        updateViewState { state ->
            state.copy(errorMessage = null)
        }
    }

    override fun obtainEvent(viewEvent: ChangeUserEvent) {
        when (viewEvent) {
            is ChangeUserEvent.OnNameChange -> handleNameChange(viewEvent.name)
            is ChangeUserEvent.OnEmailChange -> handleEmailChange(viewEvent.email)
            is ChangeUserEvent.OnDateOfBirthChange -> handleDateOfBirthChange(viewEvent.dateOfBirth)
            is ChangeUserEvent.OnSaveClick -> handleSaveClick()
            is ChangeUserEvent.OnErrorShown -> handleErrorShown()
        }
    }
}