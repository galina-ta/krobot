package com.gala.krobot.engine.impls

import com.gala.krobot.engine.common.arena.RobotStatesApplier
import com.gala.krobot.engine.common.arena.entity.RobotState
import kotlinx.coroutines.sync.Mutex

class RobotStatesApplierImpl : RobotStatesApplier {

    private val mutex = Mutex()
    override suspend fun applyStates(
        states: List<RobotState>,
        callback: RobotStatesApplier.Callback,
    ) {
        states.forEach { state ->
            callback.moveRobot(state)
            mutex.lock()
            callback.onStateApplied(state)
        }
    }

    override suspend fun robotMoved() {
        mutex.unlock()
    }
}
