package com.gala.krobot.engine.level.entity

interface RobotStateMutationsProvider {
    fun afterRobotStateCreate(robotState: RobotState) {}
    fun beforeRobotMove(robotState: RobotState): RobotState? = null
    fun afterRobotMove(robotState: RobotState): RobotState? = null
}
