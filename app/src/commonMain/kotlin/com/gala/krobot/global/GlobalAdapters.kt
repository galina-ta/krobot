@file:Suppress("unused")

package com.gala.krobot.global

import com.gala.krobot.engine.level.RobotController
import com.gala.krobot.engine.level.entity.Key
import com.gala.krobot.engine.level.entity.Level
import com.gala.krobot.engine.levels.demoLevel
import com.gala.krobot.engine.levels.homework1Variant1Level
import com.gala.krobot.engine.levels.homework1Variant2Level
import com.gala.krobot.engine.levels.homework1Variant3Level
import com.gala.krobot.engine.levels.level1

lateinit var globalRobotController: RobotController

suspend fun setLevel(level: Level) {
    globalRobotController.setLevel(level)
}

suspend fun setDemoLevel() {
    setLevel(level = demoLevel)
}

suspend fun setLevel1() {
    setLevel(level = level1)
}

suspend fun setHomework1Variant1Level() {
    setLevel(level = homework1Variant1Level)
}

suspend fun setHomework1Variant2Level() {
    setLevel(level = homework1Variant2Level)
}

suspend fun setHomework1Variant3Level() {
    setLevel(level = homework1Variant3Level)
}

/**
 * Передвинуться вправо на [stepsCount]
 * Если [stepsCount] не указано, то на 1 шаг
 */
suspend fun moveRight(stepsCount: Int = 1) {
    globalRobotController.moveRight(stepsCount)
}

/**
 * Передвинуться влево на [stepsCount].
 * Если [stepsCount] не указано, то на 1 шаг.
 */
suspend fun moveLeft(stepsCount: Int = 1) {
    globalRobotController.moveLeft(stepsCount)
}

/**
 * Передвинуться вниз на [stepsCount].
 * Если [stepsCount] не указано, то на 1 шаг.
 */
suspend fun moveDown(stepsCount: Int = 1) {
    globalRobotController.moveDown(stepsCount)
}

/**
 * Передвинуться вверх на [stepsCount].
 * Если [stepsCount] не указано, то на 1 шаг.
 */
suspend fun moveUp(stepsCount: Int = 1) {
    globalRobotController.moveUp(stepsCount)
}

/**
 * Показать [code] на дисплее робота.
 * Нужно для прохождения блоков с паролем.
 */
suspend fun showCode(code: Int) {
    globalRobotController.showCode(code)
}

fun getKey(): Key {
    return globalRobotController.collectKey()
}

suspend fun useKey(key: Key) {
    globalRobotController.useKey(key)
}

suspend fun setBeforeMove(beforeMove: () -> Unit) {
    return globalRobotController.setBeforeMove(beforeMove)
}
