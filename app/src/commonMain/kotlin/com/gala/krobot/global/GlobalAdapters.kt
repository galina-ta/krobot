package com.gala.krobot.global

import com.gala.krobot.engine.common.arena.RobotController
import com.gala.krobot.engine.common.arena.entity.arena.Arena
import com.gala.krobot.engine.levels.arena1
import com.gala.krobot.engine.levels.demoArena
import com.gala.krobot.engine.levels.homework1Variant1Arena
import com.gala.krobot.engine.levels.homework1Variant2Arena
import com.gala.krobot.engine.levels.homework1Variant3Arena

lateinit var globalRobotController: RobotController

suspend fun setArena(arena: Arena) {
    globalRobotController.setArena(arena)
}

suspend fun setDemoArena() {
    setArena(arena = demoArena)
}

suspend fun setArena1() {
    setArena(arena = arena1)
}

suspend fun setHomework1Variant1Arena() {
    setArena(arena = homework1Variant1Arena)
}

suspend fun setHomework1Variant2Arena() {
    setArena(arena = homework1Variant2Arena)
}

suspend fun setHomework1Variant3Arena() {
    setArena(arena = homework1Variant3Arena)
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
 * Показать [password] на дисплее робота.
 * Нужно для прохождения блоков с паролем.
 */
suspend fun display(password: String) {
    globalRobotController.display(password)
}

suspend fun getKey(): String {
    return globalRobotController.getKey()
}

suspend fun useKey(key: String) {
    globalRobotController.useKey(key)
}

fun currentCode(): Int {
    return globalRobotController.currentCode()
}

suspend fun setBeforeMove(beforeMove: () -> Unit) {
    return globalRobotController.setBeforeMove(beforeMove)
}
