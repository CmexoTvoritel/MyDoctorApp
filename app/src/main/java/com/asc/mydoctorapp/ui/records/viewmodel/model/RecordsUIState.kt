package com.asc.mydoctorapp.ui.records.viewmodel.model

import com.asc.mydoctorapp.core.data.remote.RecordUI

enum class RecordsTab { CURRENT, PAST, CANCELLED }

data class RecordsUIState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val selectedTab: RecordsTab = RecordsTab.CURRENT,
    val current: List<RecordUI> = emptyList(),
    val past: List<RecordUI> = emptyList(),
    val cancelled: List<RecordUI> = emptyList()
)

sealed interface RecordsEvent {
    data class OnTabSelected(val tab: RecordsTab) : RecordsEvent
    data class OnFavoriteToggle(val recordId: String, val newValue: Boolean) : RecordsEvent
    object OnPrimaryButtonClick : RecordsEvent          // «Найти врача…» или «Записаться повторно»
    data class OnRecordClick(val recordId: String) : RecordsEvent
    object OnRefresh : RecordsEvent                     // Pull to refresh
}

sealed interface RecordsAction {
    data class NavigateToSearchDoctor(val route: String) : RecordsAction
    data class NavigateToRecordDetails(val recordId: String) : RecordsAction
}
