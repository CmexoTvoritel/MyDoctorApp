package com.asc.mydoctorapp.ui.finishrecord.viewmodel

import com.asc.mydoctorapp.ui.finishrecord.model.FinishRecordAction
import com.asc.mydoctorapp.ui.finishrecord.model.FinishRecordEvent
import com.asc.mydoctorapp.ui.finishrecord.model.FinishRecordUIState
import com.diveomedia.little.stories.bedtime.books.kids.core.ui.viewmodel.BaseSharedViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class FinishRecordViewModel @Inject constructor() : BaseSharedViewModel<FinishRecordUIState, FinishRecordAction, FinishRecordEvent>(
    initialState = FinishRecordUIState()
) {

    fun setAppointmentDetails(
        appointmentInfo: String,
        clinicName: String,
        clinicAddress: String,
        date: LocalDate, 
        time: LocalTime
    ) {
        updateViewState { state ->
            state.copy(
                appointmentInfo = appointmentInfo,
                clinicName = clinicName,
                address = clinicAddress,
                date = date,
                time = time
            )
        }
    }

    override fun obtainEvent(viewEvent: FinishRecordEvent) {
        when (viewEvent) {
            is FinishRecordEvent.OnToMainClick -> {
                sendViewAction(FinishRecordAction.NavigateToMain)
            }
        }
    }
}
