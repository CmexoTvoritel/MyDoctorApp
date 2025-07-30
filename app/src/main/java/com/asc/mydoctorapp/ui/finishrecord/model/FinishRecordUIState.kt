package com.asc.mydoctorapp.ui.finishrecord.model

import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

data class FinishRecordUIState(
    val date: LocalDate? = null,
    val time: LocalTime? = null,
    val appointmentInfo: String = "",
    val address: String = "",
    val clinicName: String = ""
) {
    fun getFormattedDateTime(): String {
        return appointmentInfo
    }
}

sealed interface FinishRecordEvent {
    data object OnToMainClick : FinishRecordEvent
}

sealed interface FinishRecordAction {
    data object NavigateToMain : FinishRecordAction
}
