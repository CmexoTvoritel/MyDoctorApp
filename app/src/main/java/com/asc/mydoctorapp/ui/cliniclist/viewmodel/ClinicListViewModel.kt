package com.asc.mydoctorapp.ui.cliniclist.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asc.mydoctorapp.core.domain.usecase.GetAllClinicsUseCase
import com.asc.mydoctorapp.navigation.AppRoutes
import com.asc.mydoctorapp.ui.cliniclist.viewmodel.model.ClinicListAction
import com.asc.mydoctorapp.ui.cliniclist.viewmodel.model.ClinicListEvent
import com.asc.mydoctorapp.ui.cliniclist.viewmodel.model.ClinicListUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClinicListViewModel @Inject constructor(
    private val getAllClinicsUseCase: GetAllClinicsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ClinicListUIState())
    
    private val _actions = Channel<ClinicListAction>(Channel.BUFFERED)
    
    fun viewStates() = _state.asStateFlow()
    fun viewActions() = _actions.receiveAsFlow()
    
    init {
        loadClinics()
    }
    
    fun handleEvent(event: ClinicListEvent) {
        when (event) {
            is ClinicListEvent.LoadClinics -> loadClinics()
            is ClinicListEvent.OnRefresh -> refreshClinics()
            is ClinicListEvent.OnClinicClick -> {
                viewModelScope.launch {
                    val route = AppRoutes.DoctorList.route.replace(
                        "{clinicName}", event.clinicName
                    )
                    _actions.send(ClinicListAction.NavigateToClinic(route))
                }
            }
            is ClinicListEvent.OnBackClick -> {
                viewModelScope.launch {
                    _actions.send(ClinicListAction.NavigateBack)
                }
            }
        }
    }
    
    private fun loadClinics() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            
            try {
                val clinics = getAllClinicsUseCase()
                _state.value = _state.value.copy(
                    isLoading = false,
                    isRefreshing = false,
                    clinics = clinics,
                    error = null
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    isRefreshing = false,
                    error = e.message ?: "Ошибка загрузки клиник"
                )
                _actions.send(ClinicListAction.ShowError(e.message ?: "Ошибка загрузки клиник"))
            }
        }
    }
    
    private fun refreshClinics() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isRefreshing = true, error = null)
            
            try {
                val clinics = getAllClinicsUseCase()
                _state.value = _state.value.copy(
                    isRefreshing = false,
                    clinics = clinics,
                    error = null
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isRefreshing = false,
                    error = e.message ?: "Ошибка загрузки клиник"
                )
                _actions.send(ClinicListAction.ShowError(e.message ?: "Ошибка загрузки клиник"))
            }
        }
    }
}
