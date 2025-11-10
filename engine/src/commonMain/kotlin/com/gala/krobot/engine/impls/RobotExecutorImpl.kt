package com.gala.krobot.engine.impls

import com.gala.krobot.engine.common.arena.RobotController
import com.gala.krobot.engine.common.arena.RobotExecutor
import com.gala.krobot.engine.common.arena.entity.RobotException
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RobotExecutorImpl : RobotExecutor {
    private val context = CoroutineScope(Dispatchers.Main)

    override suspend fun execute(
        robotController: RobotController,
        callback: RobotExecutor.Callback
    ) {
        context.launch(CoroutineName("Robot")) {
            try {
                try {
                    robotController.run()
                    robotController.requireWon()
                    callback.onWon()
                } catch (e: Exception) {
                    robotController.finish(RobotException(e))
                    throw e
                }
            } catch (e: Exception) {
                callback.onFailure(e)
            }
        }
    }
}
