package com.gala.krobot.maze.common.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.gala.krobot.maze.common.models.Hero
import com.gala.krobot.maze.common.models.Position
import com.gala.krobot.maze.common.models.Wall

class MazeViewModel : ViewModel() {

    var state by mutableStateOf(
        ViewState(
            hero = Hero(position = Position(x = 0, y = 0), isDestroyed = false),
            walls = listOf(
                Wall(position = Position(x = 3, y = 2)),
                Wall(position = Position(x = 2, y = 2)),
            ),
        )
    )

    fun leftClicked() {
        state = state.copy(hero = state.hero.movedLeft())
    }

    fun rightClicked() {
        state = state.copy(hero = state.hero.movedRight())
    }

    fun upClicked() {
        state = state.copy(hero = state.hero.movedUp())
    }

    fun downClicked() {
        state = state.copy(hero = state.hero.movedDown())
    }
}

data class ViewState(
    val hero: Hero,
    val walls: List<Wall>,
)
