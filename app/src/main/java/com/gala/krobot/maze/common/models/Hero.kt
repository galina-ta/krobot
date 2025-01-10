package com.gala.krobot.maze.common.models

data class Hero(val position: Position, val isDestroyed: Boolean) {
    fun movedUp(): Hero {
        return copy(position = position.copy(y = position.y - 1))
    }

    fun movedDown(): Hero {
        return copy(position = position.copy(y = position.y + 1))
    }

    fun movedLeft(): Hero {
        return copy(position = position.copy(x = position.x - 1))
    }

    fun movedRight(): Hero {
        return copy(position = position.copy(x = position.x + 1))
    }
}
