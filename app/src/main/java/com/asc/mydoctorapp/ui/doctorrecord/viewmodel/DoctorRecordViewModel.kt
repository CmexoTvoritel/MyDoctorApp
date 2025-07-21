package com.asc.mydoctorapp.ui.doctorrecord.viewmodel

import com.asc.mydoctorapp.ui.doctorrecord.model.DoctorRecordAction
import com.asc.mydoctorapp.ui.doctorrecord.model.DoctorRecordEvent
import com.asc.mydoctorapp.ui.doctorrecord.model.DoctorRecordUIState
import com.diveomedia.little.stories.bedtime.books.kids.core.ui.viewmodel.BaseSharedViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class DoctorRecordViewModel @Inject constructor(

) : BaseSharedViewModel<DoctorRecordUIState, DoctorRecordAction, DoctorRecordEvent>(
    initialState = DoctorRecordUIState(
        displayedMonth = YearMonth.now(),
        availableDates = emptySet(),
        availableTimeSlots = emptyMap()
    )
) {

    init {
        updateViewState { state ->
            state.copy(
                displayedMonth = YearMonth.now(),
                availableDates = generateAvailableDates(),
                availableTimeSlots = generateTimeSlots()
            )
        }
    }

    override fun obtainEvent(viewEvent: DoctorRecordEvent) {
        when (viewEvent) {
            is DoctorRecordEvent.OnBackClick -> sendViewAction(DoctorRecordAction.NavigateBack)
            is DoctorRecordEvent.OnPrevMonth -> updateMonth(false)
            is DoctorRecordEvent.OnNextMonth -> updateMonth(true)
            is DoctorRecordEvent.OnDateSelected -> selectDate(viewEvent.date)
            is DoctorRecordEvent.OnTimeSelected -> selectTime(viewEvent.time)
            is DoctorRecordEvent.OnContinueClick -> {
                if (viewStates().value?.canContinue == true) {
                    val date = viewStates().value?.selectedDate!!
                    val time = viewStates().value?.selectedTime!!
                    sendViewAction(DoctorRecordAction.NavigateToConfirmation(date, time))
                }
            }
        }
    }

    private fun updateMonth(isNext: Boolean) {
        val currentMonth = viewStates().value?.displayedMonth
        val newMonth = if (isNext) {
            currentMonth?.plusMonths(1)
        } else {
            currentMonth?.minusMonths(1)
        }
        if (newMonth != null) {
            updateViewState { state ->
                state.copy(
                    displayedMonth = newMonth,
                    availableDates = generateAvailableDates(newMonth)
                )
            }
        }
    }

    private fun selectDate(date: LocalDate) {
        if (date in (viewStates().value?.availableDates ?: emptySet())) {
            updateViewState { state ->
                state.copy(
                    selectedDate = date,
                    selectedTime = null,
                    canContinue = false
                )
            }
        }
    }

    private fun selectTime(time: LocalTime) {
        updateViewState { state ->
            val canContinue = state.selectedDate != null
            state.copy(
                selectedTime = time,
                canContinue = canContinue
            )
        }
    }

    private fun generateAvailableDates(yearMonth: YearMonth = YearMonth.now()): Set<LocalDate> {
        val result = mutableSetOf<LocalDate>()
        val today = LocalDate.now()
        
        // Проверяем, содержится ли сегодняшний день в указанном месяце
        val isTodayInMonth = today.year == yearMonth.year && today.monthValue == yearMonth.monthValue
        
        // Определяем начальный день для генерации дат
        val startDay = if (isTodayInMonth) {
            today.dayOfMonth
        } else if (yearMonth.isAfter(YearMonth.from(today))) {
            // Если месяц в будущем, добавляем все дни
            1
        } else {
            // Если месяц в прошлом, не добавляем ни одного дня
            yearMonth.lengthOfMonth() + 1 // Это условие никогда не выполнится
        }
        
        // Добавляем все дни начиная с startDay
        for (day in startDay..yearMonth.lengthOfMonth()) {
            result.add(yearMonth.atDay(day))
        }
        
        return result
    }

    private fun generateTimeSlots(): Map<LocalDate, List<LocalTime>> {
        val result = mutableMapOf<LocalDate, List<LocalTime>>()
        val availableTimes = listOf(
            LocalTime.of(9, 0),
            LocalTime.of(10, 0),
            LocalTime.of(11, 0),
            LocalTime.of(12, 30),
            LocalTime.of(15, 0),
            LocalTime.of(17, 0)
        )
        
        // Генерируем одинаковые временные слоты для всех доступных дат
        for (date in generateAvailableDates()) {
            result[date] = availableTimes
        }
        
        return result
    }
}
