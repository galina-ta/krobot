package com.gala.maze.common.arena

import com.gala.maze.common.BaseViewModel
import com.gala.maze.common.arena.entity.RobotState
import com.gala.maze.common.arena.entity.arena.Arena
import com.gala.maze.common.program.text.ProgramParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ArenaViewModel(
    createRobotControllerHolder: CreateRobotControllerHolder,
    private val executor: RobotExecutor,
    private val statesApplier: RobotStatesApplier,
//    private val clipboardReceiver: ClipboardReceiver,
//    private val programParser: ProgramParser,
    private val scope: CoroutineScope,
) : BaseViewModel<ArenaViewState>(
    initialState = ArenaViewState(
        arena = null,
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

        robotController.onArenaSet = { arena ->
            updateState { copy(arena = arena) }
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

//    fun executeCopiedCodeClicked() {
//        val text = clipboardReceiver.get() ?: return
//        val commands = programParser.parse(text)
//        val controller = ProgramRobotController(commands)
//        execute(controller)
//    }
}

data class ArenaViewState(
    val arena: Arena?,
    val robotState: RobotState?,
    val movesRight: Boolean,
    val isWon: Boolean,
)
