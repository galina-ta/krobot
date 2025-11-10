package com.gala.krobot.engine.common.arena

import com.gala.krobot.engine.common.BaseViewModel
import com.gala.krobot.engine.common.arena.entity.RobotState
import com.gala.krobot.engine.common.arena.entity.arena.Level
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LevelViewModel(
    createRobotControllerHolder: CreateRobotControllerHolder,
    private val executor: RobotExecutor,
    private val statesApplier: RobotStatesApplier,
    private val scope: CoroutineScope,
) : BaseViewModel<LevelViewState>(
    initialState = LevelViewState(
        level = null,
        robotState = null,
        movesRight = true,
        isWon = false,
    ),
) {
    private val executorCallback = object : RobotExecutor.Callback {

        override fun onWon() {
            updateState { copy(isWon = true) }
        }

        override fun onFailure(e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun execute(robotController: RobotController) {

        val statesApplierCallback: RobotStatesApplier.Callback =
            object : RobotStatesApplier.Callback {

                override suspend fun moveRobot(state: RobotState) {
                    delay(500)
                    scope.launch {
                        updateState {
                            copy(
                                robotState = state,
                                movesRight = when {
                                    robotState == null -> true
                                    state.position.x > robotState.position.x -> true
                                    state.position.x < robotState.position.x -> false
                                    else -> movesRight
                                }
                            )
                        }
                        statesApplier.robotMoved()
                    }
                }

                override fun onStateApplied(state: RobotState) {
                    robotController.onStateApplied(state)
                }
            }

        robotController.onLevelSet = { arena ->
            updateState { copy(level = arena) }
        }
        robotController.applyStates = { states ->
            statesApplier.applyStates(states, statesApplierCallback)
        }

        executor.execute(robotController, executorCallback)
    }

    init {
        val robotController = requireNotNull(createRobotControllerHolder.instance) {
            "userAction must not be null"
        }.invoke()

        scope.launch {
            execute(robotController)
        }
    }
}

data class LevelViewState(
    val level: Level?,
    val robotState: RobotState?,
    val movesRight: Boolean,
    val isWon: Boolean,
)
