package com.gala.maze.common.arena

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.gala.maze.common.BaseViewModel
import com.gala.maze.common.arena.entity.RobotState
import com.gala.maze.common.arena.entity.arena.Arena
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArenaViewModel @Inject constructor(
    createRobotControllerHolder: CreateRobotControllerHolder,
    executor: RobotExecutor,
    private val statesApplier: RobotStatesApplier,
) : BaseViewModel<ArenaViewState>(
    initialState = ArenaViewState(
        arena = null,
        robotState = null,
        isWon = false,
    ),
) {
    private val statesApplierCallback: RobotStatesApplier.Callback =
        object : RobotStatesApplier.Callback {

            override fun moveRobot(state: RobotState) {
                updateState { copy(robotState = state) }
            }

            override fun onStateApplied(state: RobotState) {
                robotController.onStateApplied(state)
            }
        }

    private val robotController: RobotController =
        requireNotNull(createRobotControllerHolder.instance) {
            "userAction must not be null"
        }.invoke()

    init {
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
    }

    private val executorCallback = object : RobotExecutor.Callback {

        override fun onWon() {
            updateState { copy(isWon = true) }
        }

        override fun onFailure(e: Exception) {
            Log.e(TAG, "Robot destroyed", e)
        }
    }

    init {
        executor.execute(robotController, executorCallback, useCallback = { action ->
            viewModelScope.launch {
                action()
            }
        })
    }

    fun robotMoved() {
        statesApplier.robotMoved()
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
