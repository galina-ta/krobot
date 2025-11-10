package com.gala.krobot

import com.gala.krobot.engine.level.RobotController

fun createRobotController(): RobotController {
    return GlobalRobotController()
}

class GlobalRobotController : RobotController() {

    override suspend fun run() {
        run(controller = this)
        com.gala.krobot.run()
    }
}
