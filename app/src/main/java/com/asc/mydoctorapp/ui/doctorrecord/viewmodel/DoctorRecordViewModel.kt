package com.asc.mydoctorapp.ui.doctorrecord.viewmodel

import android.util.Log
import com.asc.mydoctorapp.core.data.remote.WorkingDays
import com.asc.mydoctorapp.core.domain.model.AppointmentRequest
import com.asc.mydoctorapp.core.domain.model.Doctor
import com.asc.mydoctorapp.core.domain.model.UserToken
import com.asc.mydoctorapp.core.domain.usecase.BookAppointmentUseCase
import com.asc.mydoctorapp.core.domain.usecase.GetDoctorByEmailUseCase
import com.asc.mydoctorapp.core.utils.PreferencesManager
import com.asc.mydoctorapp.ui.doctorrecord.model.DoctorRecordAction
import com.asc.mydoctorapp.ui.doctorrecord.model.DoctorRecordEvent
import com.asc.mydoctorapp.ui.doctorrecord.model.DoctorRecordUIState
import com.diveomedia.little.stories.bedtime.books.kids.core.ui.viewmodel.BaseSharedViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class DoctorRecordViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager,
    private val getDoctorByEmailUseCase: GetDoctorByEmailUseCase,
    private val bookAppointmentUseCase: BookAppointmentUseCase
) : BaseSharedViewModel<DoctorRecordUIState, DoctorRecordAction, DoctorRecordEvent>(
    initialState = DoctorRecordUIState(
        displayedMonth = YearMonth.now(),
        availableDates = emptySet()
    )
) {
    private var doctorInfo: Doctor? = null

    private fun parseTimeRange(range: String): List<LocalTime> {
        val (startStr, endStr) = range.split('-')
        val formatter = DateTimeFormatter.ofPattern("H:mm")
        val start = LocalTime.parse(startStr.trim(), formatter)
        val end   = LocalTime.parse(endStr.trim(), formatter)
        return generateSequence(start) { it.plusHours(1) }
            .takeWhile { it < end }       // последняя не включается, как в примере
            .toList()
    }

    override fun obtainEvent(viewEvent: DoctorRecordEvent) {
        when (viewEvent) {
            is DoctorRecordEvent.OnBackClick -> sendViewAction(DoctorRecordAction.NavigateBack)
            is DoctorRecordEvent.OnPrevMonth -> updateMonth(false)
            is DoctorRecordEvent.OnNextMonth -> updateMonth(true)
            is DoctorRecordEvent.OnDateSelected -> selectDate(viewEvent.date)
            is DoctorRecordEvent.OnTimeSelected -> selectTime(viewEvent.time)
            is DoctorRecordEvent.OnContinueClick -> continueClicked()
            is DoctorRecordEvent.LoadDoctor -> loadDoctorInfo(viewEvent.email)
        }
    }

    private fun loadDoctorInfo(email: String) {
        viewModelScope.launch {
            try {
                val doctor = getDoctorByEmailUseCase(email)
                doctorInfo = doctor
                updateViewState { s ->
                    val month = YearMonth.now()
                    val newDates = generateAvailableDates(month, doctor.workingDays)
                    s.copy(
                        workingDays = doctor.workingDays,
                        displayedMonth = month,
                        availableDates = newDates,
                        availableTimeSlots = emptyList(),
                        selectedDate = null,
                        selectedTime = null,
                        canContinue = false
                    )
                }
            } catch (e: Exception) {
                Log.e("DoctorRecordViewModel", "Error loading doctor info", e)
            }
        }
    }

    private fun updateMonth(isNext: Boolean) {
        val state = viewStates().value ?: return
        val newMonth = (state.displayedMonth).let { if (isNext) it.plusMonths(1) else it.minusMonths(1) }

        updateViewState {
            it.copy(
                displayedMonth = newMonth,
                availableDates = generateAvailableDates(newMonth, it.workingDays),
                // сбрасываем выбор при смене месяца
                selectedDate = null,
                selectedTime = null,
                availableTimeSlots = emptyList(),
                canContinue = false
            )
        }
    }

    private fun selectDate(date: LocalDate) {
        val state = viewStates().value ?: return
        if (date !in state.availableDates) return

        val slots = buildList {
            val wd = state.workingDays ?: return@buildList
            val rangeStr = when (date.dayOfWeek) {
                DayOfWeek.MONDAY    -> wd.monday
                DayOfWeek.TUESDAY   -> wd.tuesday
                DayOfWeek.WEDNESDAY -> wd.wednesday
                DayOfWeek.THURSDAY  -> wd.thursday
                DayOfWeek.FRIDAY    -> wd.friday
                DayOfWeek.SATURDAY  -> wd.saturday
                DayOfWeek.SUNDAY    -> wd.sunday
            }
            if (rangeStr != null) addAll(parseTimeRange(rangeStr))
        }

        updateViewState {
            it.copy(
                selectedDate = date,
                selectedTime = null,
                availableTimeSlots = slots,
                canContinue = false
            )
        }
    }

    private fun selectTime(time: LocalTime) {
        updateViewState { s ->
            val ok = s.selectedDate != null && time in s.availableTimeSlots
            s.copy(selectedTime = time, canContinue = ok)
        }
    }

    private fun continueClicked() {
        viewModelScope.launch {
            if (viewStates().value?.canContinue == true) {
                val date = viewStates().value?.selectedDate!!
                val time = viewStates().value?.selectedTime!!
                try {
                    val answer = bookAppointmentUseCase.invoke(request = AppointmentRequest(
                        doctorEmail = doctorInfo?.email ?: "",
                        token = UserToken(value = preferencesManager.userToken ?: ""),
                        day = date.dayOfMonth.toString(),
                        month = date.monthValue.toString(),
                        year = date.year.toString().substring(2),
                        hour = time.hour.toString(),
                        minutes = time.minute.toString()
                    ))
                    if (answer) {
                        sendViewAction(DoctorRecordAction.NavigateToConfirmation(date, time))
                    }
                } catch (e: Exception) {
                    Log.e("DoctorRecordViewModel", "Error creating record", e)
                }
            }
        }
    }

    private fun generateAvailableDates(month: YearMonth, wd: WorkingDays?): Set<LocalDate> {
        if (wd == null) return emptySet()

        val allowedDays = buildSet {
            if (wd.monday    != null) add(DayOfWeek.MONDAY)
            if (wd.tuesday   != null) add(DayOfWeek.TUESDAY)
            if (wd.wednesday != null) add(DayOfWeek.WEDNESDAY)
            if (wd.thursday  != null) add(DayOfWeek.THURSDAY)
            if (wd.friday    != null) add(DayOfWeek.FRIDAY)
            if (wd.saturday  != null) add(DayOfWeek.SATURDAY)
            if (wd.sunday    != null) add(DayOfWeek.SUNDAY)
        }

        val today = LocalDate.now()
        return (1..month.lengthOfMonth())
            .map { month.atDay(it) }
            .filter { it.dayOfWeek in allowedDays && !it.isBefore(today) }
            .toSet()
    }
}
