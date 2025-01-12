package com.gala.krobot

import com.gala.maze.common.arena.RobotController

fun createRobotController(): RobotController {
    return GlobalRobotController()
}

class GlobalRobotController : RobotController() {

    override fun run() {
        run(controller = this)
        com.gala.krobot.run()
    }
}
