package com.asc.mydoctorapp.ui.doctorrecord.model

import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth

data class DoctorRecordUIState(
    val displayedMonth: YearMonth = YearMonth.now(),
    val selectedDate: LocalDate? = null,
    val selectedTime: LocalTime? = null,
    val availableDates: Set<LocalDate> = emptySet(),
    val availableTimeSlots: Map<LocalDate, List<LocalTime>> = emptyMap(),
    val canContinue: Boolean = false
)

sealed interface DoctorRecordEvent {
    data object OnBackClick : DoctorRecordEvent
    data object OnPrevMonth : DoctorRecordEvent
    data object OnNextMonth : DoctorRecordEvent
    data class OnDateSelected(val date: LocalDate) : DoctorRecordEvent
    data class OnTimeSelected(val time: LocalTime) : DoctorRecordEvent
    data class OnContinueClick(val date: LocalDate, val time: LocalTime) : DoctorRecordEvent
}

sealed interface DoctorRecordAction {
    data object NavigateBack : DoctorRecordAction
    data class NavigateToConfirmation(val date: LocalDate, val time: LocalTime) : DoctorRecordAction
}
