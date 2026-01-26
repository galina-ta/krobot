package com.gala.krobot

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

sealed interface Route {

    sealed interface Robot : Route {
        object Level : Robot
        object CodeEditor : Robot
    }

    object LevelEditor : Route
}

class Router(initial: Route) {
    var currentRoute: Route by mutableStateOf(initial)
        private set

    fun navigate(route: Route) {
        currentRoute = route
    }
}
