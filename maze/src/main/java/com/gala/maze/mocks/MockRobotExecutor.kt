package com.gala.maze.mocks

import com.gala.maze.common.arena.RobotController
import com.gala.maze.common.arena.RobotExecutor

class MockRobotExecutor : RobotExecutor {

    override fun execute(
        robotController: RobotController,
        callback: RobotExecutor.Callback,
        useCallback: (() -> Unit) -> Unit,
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