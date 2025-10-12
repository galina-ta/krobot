package com.gala.maze.mocks

import com.gala.maze.common.arena.RobotStatesApplier
import com.gala.maze.common.arena.entity.RobotState

class MockRobotStatesApplier : RobotStatesApplier {

    override suspend fun applyStates(
        states: List<RobotState>,
        callback: RobotStatesApplier.Callback
    ) {
        states.forEach { state ->
            callback.moveRobot(state)
            callback.onStateApplied(state)
        }
    }

    override suspend fun robotMoved() {

    }
}
