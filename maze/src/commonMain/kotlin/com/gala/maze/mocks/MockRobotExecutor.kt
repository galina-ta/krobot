package com.gala.maze.mocks

import com.gala.maze.common.arena.RobotController
import com.gala.maze.common.arena.RobotExecutor

class MockRobotExecutor : RobotExecutor {

    override suspend fun execute(
        robotController: RobotController,
        callback: RobotExecutor.Callback
    ) {
        try {
            robotController.run()
            robotController.requireWon()
            callback.onWon()
        } catch (e: Exception) {
            callback.onFailure(e)
        }
    }
}
