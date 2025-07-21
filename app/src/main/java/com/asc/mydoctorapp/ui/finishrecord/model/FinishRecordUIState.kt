package com.asc.mydoctorapp.ui.finishrecord.model

import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

data class FinishRecordUIState(
    val date: LocalDate? = null,
    val time: LocalTime? = null,
    val address: String = "Ул Вавилова, дом 15",
    val clinicName: String = "Клиника \"Здоровье\""
) {
    fun getFormattedDateTime(): String {
        val dateFormatter = DateTimeFormatter.ofPattern("dd")
        val monthName = date?.month?.getDisplayName(TextStyle.FULL, Locale("ru"))
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        
        return if (date != null && time != null) {
            "${date.format(dateFormatter)} $monthName, ${time.format(timeFormatter)}"
        } else {
            ""
        }
    }
}

sealed interface FinishRecordEvent {
    data object OnToMainClick : FinishRecordEvent
}

sealed interface FinishRecordAction {
    data object NavigateToMain : FinishRecordAction
}
