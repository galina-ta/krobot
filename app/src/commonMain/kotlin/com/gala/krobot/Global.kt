package com.gala.krobot

import com.gala.krobot.global.moveLeft
import com.gala.krobot.global.moveUp
import com.gala.krobot.global.setLevel
import com.gala.krobot.engine.levels.dogLevel

// определяем функцию выполнить
suspend fun run() {
    setLevel(dogLevel)

    moveUp()
    moveUp()
    moveLeft()
    moveLeft()
}
