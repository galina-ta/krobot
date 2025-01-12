package com.gala.maze.mocks

import com.gala.maze.common.arena.RobotStatesApplier
import com.gala.maze.common.arena.entity.RobotState

class MockRobotStatesApplier : RobotStatesApplier {

    override fun applyStates(
        states: List<RobotState>,
        callback: RobotStatesApplier.Callback,
        useCallback: (() -> Unit) -> Unit
    ) {
        states.forEach { state ->
            callback.moveRobot(state)
            callback.onStateApplied(state)
        }
    }

    override fun robotMoved() {

    }
}