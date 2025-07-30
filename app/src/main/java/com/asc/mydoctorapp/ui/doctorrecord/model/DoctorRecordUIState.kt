package com.asc.mydoctorapp.ui.doctorrecord.model

import com.asc.mydoctorapp.core.data.remote.WorkingDays
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth

data class DoctorRecordUIState(
    val displayedMonth: YearMonth = YearMonth.now(),
    val selectedDate: LocalDate? = null,
    val selectedTime: LocalTime? = null,
    val availableDates: Set<LocalDate> = emptySet(),
    val availableTimeSlots: List<LocalTime> = emptyList(),
    val canContinue: Boolean = false,
    val workingDays: WorkingDays? = null,
    val isLoading: Boolean = false,
    val hasError: Boolean = false
)

sealed interface DoctorRecordEvent {
    data class LoadDoctor(val email: String, val clinicName: String) : DoctorRecordEvent
    data object OnBackClick : DoctorRecordEvent
    data object OnPrevMonth : DoctorRecordEvent
    data object OnNextMonth : DoctorRecordEvent
    data class OnDateSelected(val date: LocalDate) : DoctorRecordEvent
    data class OnTimeSelected(val time: LocalTime) : DoctorRecordEvent
    data class OnContinueClick(val date: LocalDate, val time: LocalTime) : DoctorRecordEvent
    data object OnErrorShown : DoctorRecordEvent
}

sealed interface DoctorRecordAction {
    data object NavigateBack : DoctorRecordAction
    data class NavigateToConfirmation(
        val appointmentInfo: String,
        val clinicName: String, 
        val clinicAddress: String
    ) : DoctorRecordAction
    data object ShowError : DoctorRecordAction
}
