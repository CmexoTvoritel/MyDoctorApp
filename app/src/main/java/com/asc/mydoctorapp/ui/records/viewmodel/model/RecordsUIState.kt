package com.asc.mydoctorapp.ui.records.viewmodel.model

enum class RecordsTab { CURRENT, PAST }

data class RecordUi(
    val id: String,
    val doctorName: String,
    val specialty: String,
    val time: String,          // «15:00»
    val address: String,       // «Вавилова, 15»
    val clinic: String,        // «Клиника "Здоровье"»
    val photoRes: Int?,        // drawable / URL
    val isFavorite: Boolean
)

data class RecordsUIState(
    val selectedTab: RecordsTab = RecordsTab.CURRENT,
    val current: List<RecordUi> = emptyList(),
    val past: List<RecordUi> = emptyList()
)

sealed interface RecordsEvent {
    data class OnTabSelected(val tab: RecordsTab) : RecordsEvent
    data class OnFavoriteToggle(val recordId: String, val newValue: Boolean) : RecordsEvent
    object OnPrimaryButtonClick : RecordsEvent          // «Найти врача…» или «Записаться повторно»
    data class OnRecordClick(val recordId: String) : RecordsEvent
}

sealed interface RecordsAction {
    data class NavigateToSearchDoctor(val route: String) : RecordsAction
    data class NavigateToRecordDetails(val recordId: String) : RecordsAction
}
