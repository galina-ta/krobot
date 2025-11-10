package com.gala.krobot.engine.level.impls

import com.gala.krobot.engine.level.RobotController
import com.gala.krobot.engine.level.RobotExecutor
import com.gala.krobot.engine.level.entity.RobotException
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
