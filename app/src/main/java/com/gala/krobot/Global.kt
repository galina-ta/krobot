package com.gala.krobot

import com.gala.krobot.global.moveLeft
import com.gala.krobot.global.moveUp
import com.gala.krobot.global.setArena
import com.gala.maze.levels.dogArena

// определяем функцию выполнить
fun run() {
    setArena(dogArena)

    moveUp()
    moveUp()
    moveLeft()
    moveLeft()
    moveLeft()
    moveUp()
}
