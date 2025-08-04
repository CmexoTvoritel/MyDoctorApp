package com.asc.mydoctorapp.ui.doctorrecord.viewmodel

import android.util.Log
import com.asc.mydoctorapp.core.data.remote.WorkingDays
import com.asc.mydoctorapp.core.domain.model.AppointmentRequest
import com.asc.mydoctorapp.core.domain.model.Doctor
import com.asc.mydoctorapp.core.domain.model.UserToken
import com.asc.mydoctorapp.core.domain.usecase.BookAppointmentUseCase
import com.asc.mydoctorapp.core.domain.usecase.GetClinicByQueryUseCase
import com.asc.mydoctorapp.core.domain.usecase.GetDoctorByEmailUseCase
import com.asc.mydoctorapp.core.domain.usecase.GetUserRecordsUseCase
import com.asc.mydoctorapp.core.utils.PreferencesManager
import com.asc.mydoctorapp.ui.doctorrecord.model.DoctorRecordAction
import com.asc.mydoctorapp.ui.doctorrecord.model.DoctorRecordEvent
import com.asc.mydoctorapp.ui.doctorrecord.model.DoctorRecordUIState
import com.diveomedia.little.stories.bedtime.books.kids.core.ui.viewmodel.BaseSharedViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class DoctorRecordViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager,
    private val getDoctorByEmailUseCase: GetDoctorByEmailUseCase,
    private val bookAppointmentUseCase: BookAppointmentUseCase,
    private val getClinicByQueryUseCase: GetClinicByQueryUseCase,
    private val getUserRecordsUseCase: GetUserRecordsUseCase
) : BaseSharedViewModel<DoctorRecordUIState, DoctorRecordAction, DoctorRecordEvent>(
    initialState = DoctorRecordUIState(
        displayedMonth = YearMonth.now(),
        availableDates = emptySet()
    )
) {
    private var doctorInfo: Doctor? = null
    private var doctorEmail: String = ""

    private fun parseTimeRange(range: String): List<LocalTime> {
        val (startStr, endStr) = range.split('-')
        val formatter = DateTimeFormatter.ofPattern("H:mm")
        val start = LocalTime.parse(startStr.trim(), formatter)
        val end   = LocalTime.parse(endStr.trim(), formatter)
        return generateSequence(start) { it.plusHours(1) }
            .takeWhile { it < end }
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
            is DoctorRecordEvent.LoadDoctor -> loadDoctorInfo(viewEvent.email, viewEvent.clinicName)
            is DoctorRecordEvent.OnErrorShown -> {
                updateViewState { it.copy(hasError = false) }
            }
        }
    }

    private fun loadDoctorInfo(email: String, clinicName: String) {
        viewModelScope.launch {
            try {
                val doctor = getDoctorByEmailUseCase(email, clinicName)
                doctorInfo = doctor
                doctorEmail = email
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
                        canContinue = false,
                        isLoadingRecords = true // Начинаем загрузку записей
                    )
                }
                loadUserRecords()
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
                selectedDate = null,
                selectedTime = null,
                availableTimeSlots = emptyList(),
                canContinue = false,
                isLoadingRecords = true // Начинаем загрузку записей при смене месяца
            )
        }
        loadUserRecords()
    }

    private fun selectDate(date: LocalDate) {
        val state = viewStates().value ?: return
        if (date !in state.availableDates) return

        // Проверяем, занята ли дата записью к этому врачу
        val isDateBlocked = date in state.bookedDates
        
        val slots = if (isDateBlocked) {
            emptyList() // Если дата заблокирована, времени нет
        } else {
            buildList {
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
        }

        updateViewState {
            it.copy(
                selectedDate = date,
                selectedTime = null,
                availableTimeSlots = slots,
                canContinue = false,
                isDateBlocked = isDateBlocked
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

                updateViewState { it.copy(isLoading = true, hasError = false) }

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
                        val clinicName = doctorInfo?.clinic ?: ""
                        val clinics = getClinicByQueryUseCase.invoke("")
                        val clinic = clinics.find { it.name == clinicName }
                        val clinicAddress = clinic?.address ?: ""

                        // Форматируем дату и время для передачи
                        val monthName = date.month.getDisplayName(
                            java.time.format.TextStyle.FULL,
                            Locale("ru")
                        )
                        val appointmentInfo = "${date.dayOfMonth} $monthName ${String.format("%02d:%02d", time.hour, time.minute)}"

                        updateViewState { it.copy(isLoading = false) }
                        sendViewAction(DoctorRecordAction.NavigateToConfirmation(
                            appointmentInfo = appointmentInfo,
                            clinicName = clinicName,
                            clinicAddress = clinicAddress
                        ))
                    } else {
                        updateViewState { it.copy(isLoading = false, hasError = true) }
                        sendViewAction(DoctorRecordAction.ShowError)
                    }
                } catch (e: Exception) {
                    Log.e("DoctorRecordViewModel", "Error creating record", e)
                    updateViewState { it.copy(isLoading = false, hasError = true) }
                    sendViewAction(DoctorRecordAction.ShowError)
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

    private fun loadUserRecords() {
        viewModelScope.launch {
            try {
                val allRecords = getUserRecordsUseCase.invoke()
                // Фильтруем записи для текущего врача по email
                val doctorRecords = allRecords.filter { record ->
                    record.email == doctorEmail
                }
                
                // Парсим даты записей
                val bookedDates = mutableSetOf<LocalDate>()
                val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm")
                
                doctorRecords.forEach { record ->
                    try {
                        val dateTimeString = record.start
                        if (!dateTimeString.isNullOrEmpty()) {
                            // Парсим время из строки формата "dd.MM.yyyy, HH:mm"
                            val localDateTime = LocalDateTime.parse(dateTimeString, formatter)
                            bookedDates.add(localDateTime.toLocalDate())
                        }
                    } catch (e: DateTimeParseException) {
                        Log.w("DoctorRecordViewModel", "Failed to parse date: ${record.start}", e)
                    }
                }
                
                updateViewState { state ->
                    state.copy(bookedDates = bookedDates, isLoadingRecords = false)
                }
            } catch (e: Exception) {
                Log.e("DoctorRecordViewModel", "Error loading user records", e)
                updateViewState { state ->
                    state.copy(isLoadingRecords = false)
                }
            }
        }
    }
}
