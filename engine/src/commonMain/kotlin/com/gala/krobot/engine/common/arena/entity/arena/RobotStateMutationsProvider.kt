package com.gala.krobot.engine.common.arena.entity.arena

import com.gala.krobot.engine.common.arena.entity.RobotState

interface RobotStateMutationsProvider {
    fun beforeRobotMove(robotState: RobotState): RobotState? = null
    fun afterRobotMove(robotState: RobotState): RobotState? = null
}
