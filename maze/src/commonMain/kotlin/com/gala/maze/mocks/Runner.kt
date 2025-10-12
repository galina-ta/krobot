package com.gala.maze.mocks

import com.gala.maze.common.arena.RobotController
import com.gala.maze.common.arena.RobotExecutor
import com.gala.maze.common.arena.RobotStatesApplier
import com.gala.maze.common.arena.entity.RobotState
import com.gala.maze.common.arena.entity.arena.Arena

suspend fun runMockRobot(arena: Arena, robotController: RobotController) {
    var exception: Exception? = null
    val executor = MockRobotExecutor()
    val statesApplier = MockRobotStatesApplier()
    robotController.applyStates = { states ->
        statesApplier.applyStates(
            states = states,
            callback = object : RobotStatesApplier.Callback {

                override suspend fun moveRobot(state: RobotState) {
                    statesApplier.robotMoved()
                }

                override fun onStateApplied(state: RobotState) {
                    robotController.onStateApplied(state)
                }
            }
        )
    }
    robotController.setArena(arena)
    executor.execute(
        robotController = robotController,
        callback = object : RobotExecutor.Callback {

            override fun onWon() {
                // do nothing
            }

            override fun onFailure(e: Exception) {
                exception = e
            }
        }
    )
    if (exception != null) throw exception!!
}