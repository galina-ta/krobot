package com.gala.krobot.maze.common.models

data class Wall(val position: Position) {
    fun heroMoved(hero: Hero): Hero {
        return if (hero.position == position) {
            hero.copy(isDestroyed = true)
        } else {
            hero
        }
    }
}
