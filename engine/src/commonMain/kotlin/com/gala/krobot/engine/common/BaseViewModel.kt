package com.gala.krobot.engine.common

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

abstract class BaseViewModel<TViewState>(
    initialState: TViewState
) : ViewModel() {

    var state: TViewState by mutableStateOf(initialState)
        private set

    fun updateState(update: TViewState.() -> TViewState) {
        state = update(state)
    }
}

expect abstract class ViewModel()
