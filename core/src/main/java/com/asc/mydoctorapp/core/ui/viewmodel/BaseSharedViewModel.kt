package com.diveomedia.little.stories.bedtime.books.kids.core.ui.viewmodel

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

abstract class BaseSharedViewModel<State : Any, Action, Event>(initialState: State? = null) : KViewModel() {

    private val _viewStates = MutableStateFlow(initialState)
    // Channel is required to handle all edge cases,
    // see https://github.com/Kotlin/kotlinx.coroutines/issues/3002
    private val _viewActions = Channel<Action?>(Channel.BUFFERED)

    fun viewStates(): StateFlow<State?> = _viewStates
    fun viewActions(): Flow<Action?> =_viewActions.receiveAsFlow()

    protected var viewState: State?
        get() = _viewStates.value
        set(value) {
            /** StateFlow doesn't work with same values */
            if (_viewStates.value == value) {
                _viewStates.value = null
            }
            _viewStates.value = value
        }

    private var viewAction: Action?
        get() = null
        set(value) {
            viewModelScope.launch {
                _viewActions.send(value)
            }
        }

    fun updateViewState(function: (State) -> State) {
        _viewStates.update { state ->
            state?.let { function.invoke(it) }
        }
    }

    protected fun sendViewAction(action: Action?) {
        viewAction = action
    }

    abstract fun obtainEvent(viewEvent: Event)

    /**
     * Convenient method to perform work in [viewModelScope] scope.
     */
    protected fun withViewModelScope(block: suspend CoroutineScope.() -> Unit) {
        viewModelScope.launch(block = block)
    }
}