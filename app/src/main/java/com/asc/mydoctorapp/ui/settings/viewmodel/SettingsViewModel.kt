package com.asc.mydoctorapp.ui.settings.viewmodel

import com.asc.mydoctorapp.ui.settings.viewmodel.model.SettingsAction
import com.asc.mydoctorapp.ui.settings.viewmodel.model.SettingsEvent
import com.asc.mydoctorapp.ui.settings.viewmodel.model.SettingsUIState
import com.diveomedia.little.stories.bedtime.books.kids.core.ui.viewmodel.BaseSharedViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(

): BaseSharedViewModel<SettingsUIState, SettingsAction, SettingsEvent>() {
    override fun obtainEvent(viewEvent: SettingsEvent) {
        when (viewEvent) {
            else -> {}
        }
    }
}