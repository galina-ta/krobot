package com.gala.maze.common.arena

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.gala.maze.common.BaseViewModel
import com.gala.maze.common.arena.entity.RobotState
import com.gala.maze.common.arena.entity.arena.Arena
import com.gala.maze.common.program.ClipboardReceiver
import com.gala.maze.common.program.ProgramParser
import com.gala.maze.common.program.ProgramRobotController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArenaViewModel @Inject constructor(
    createRobotControllerHolder: CreateRobotControllerHolder,
    private val executor: RobotExecutor,
    private val statesApplier: RobotStatesApplier,
    private val clipboardReceiver: ClipboardReceiver,
    private val programParser: ProgramParser,
) : BaseViewModel<ArenaViewState>(
    initialState = ArenaViewState(
        arena = null,
        robotState = null,
        isWon = false,
    ),
) {
    private val executorCallback = object : RobotExecutor.Callback {

        override fun onWon() {
            updateState { copy(isWon = true) }
        }

        override fun onFailure(e: Exception) {
            Log.e(TAG, "Robot destroyed", e)
        }
    }

    private fun execute(robotController: RobotController) {

        val statesApplierCallback: RobotStatesApplier.Callback =
            object : RobotStatesApplier.Callback {

                override fun moveRobot(state: RobotState) {
                    updateState { copy(robotState = state) }
                    Thread.sleep(500)
                    statesApplier.robotMoved()
                }

                override fun onStateApplied(state: RobotState) {
                    robotController.onStateApplied(state)
                }
            }

        robotController.onArenaSet = { arena ->
            viewModelScope.launch {
                updateState { copy(arena = arena) }
            }
        }
        robotController.applyStates = { states ->
            statesApplier.applyStates(states, statesApplierCallback, useCallback = { action ->
                viewModelScope.launch {
                    action()
                }
            })
        }
        executor.execute(robotController, executorCallback, useCallback = { action ->
            viewModelScope.launch {
                action()
            }
        })
    }

    init {
        val robotController = requireNotNull(createRobotControllerHolder.instance) {
            "userAction must not be null"
        }.invoke()

        execute(robotController)
    }

    fun executeCopiedCodeClicked() {
        val text = clipboardReceiver.get() ?: return
        val commands = programParser.parse(text)
        val controller = ProgramRobotController(commands)
        execute(controller)
    }

    private companion object {
        private const val TAG = "ArenaViewModel"
    }
}

data class ArenaViewState(
    val arena: Arena?,
    val robotState: RobotState?,
    val isWon: Boolean,
)
