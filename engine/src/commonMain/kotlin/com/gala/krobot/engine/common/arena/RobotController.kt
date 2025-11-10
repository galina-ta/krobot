package com.gala.krobot.engine.common.arena

import com.gala.krobot.engine.common.arena.entity.Position
import com.gala.krobot.engine.common.arena.entity.RobotException
import com.gala.krobot.engine.common.arena.entity.RobotState
import com.gala.krobot.engine.common.arena.entity.arena.Level
import com.gala.krobot.engine.common.arena.entity.arena.RandomCodeBlock

abstract class RobotController : RobotState.Source {
    var onLevelSet: (Level) -> Unit = {}
    lateinit var applyStates: suspend (List<RobotState>) -> Unit

    private lateinit var level: Level

    suspend fun setLevel(level: Level) {
        this.level = level
        onLevelSet(level)
        updateState(level.initialRobotState)
    }

    private lateinit var currentState: RobotState

    abstract suspend fun run()

    fun requireWon() {
        if (!currentState.isWon) {
            throw NotCompleteException(currentState, stateHistory)
        }
    }

    suspend fun getKey(): String {
        updateState(currentState.withInitKey())
        return currentState.getKey()
    }

    suspend fun useKey(key: String) {
        updateState(currentState.withKey(key))
    }

    fun needKeyLeft(): Boolean {
        return needKey(direction = Position.Direction.Left)
    }

    fun needKeyRight(): Boolean {
        return needKey(direction = Position.Direction.Right)
    }

    fun needKeyUp(): Boolean {
        return needKey(direction = Position.Direction.Up)
    }

    fun needKeyDown(): Boolean {
        return needKey(direction = Position.Direction.Down)
    }

    fun needKey(direction: Position.Direction): Boolean {
        val nextPosition = currentState.position.moved(direction)
        return level.blockOn(nextPosition)?.requiresKey == true
    }

    fun currentCode(): Int {
        val block = level.blockOn(currentState.position)
        require(block is RandomCodeBlock) { "Robot is not on a CodeBlock now" }
        return block.randomCode
    }

    open suspend fun display(password: String) {
        updateState(state = currentState.displaying(password))
    }

    open suspend fun moveRight(stepsCount: Int = 1) {
        repeat(stepsCount) {
            move(direction = Position.Direction.Right)
        }
    }

    open suspend fun moveLeft(stepsCount: Int = 1) {
        repeat(stepsCount) {
            move(direction = Position.Direction.Left)
        }
    }

    open suspend fun moveDown(stepsCount: Int = 1) {
        repeat(stepsCount) {
            move(direction = Position.Direction.Down)
        }
    }

    open suspend fun moveUp(stepsCount: Int = 1) {
        repeat(stepsCount) {
            move(direction = Position.Direction.Up)
        }
    }

    open suspend fun move(direction: Position.Direction) {
        updateState(state = currentState.moved(direction).withSource(source = this))
    }

    suspend fun setBeforeMove(beforeMove: () -> Unit) {
        updateState(state = currentState.withBeforeMove(beforeMove))
    }

    suspend fun updateState(state: RobotState) {
        val statesList = makeStatesList(state)
        applyStates(statesList)
    }

    open suspend fun finish(reason: RobotException?) {
        updateState(currentState.finished(reason))
    }

    private fun makeStatesList(state: RobotState): List<RobotState> {

        val list = mutableListOf<RobotState>()

        val beforeState = level.beforeRobotMove(robotState = state).takeIf { it != state }
        if (beforeState != null) {
            val beforeStates = makeStatesList(beforeState)
            list.addAll(beforeStates)
        }

        list.add(state)

        val afterState = level.afterRobotMove(robotState = state).takeIf { it != state }
        if (afterState != null) {
            val afterStates = makeStatesList(afterState)
            list.addAll(afterStates)
        }

        return list
    }

    private val stateHistory = mutableListOf<RobotState>()
    fun onStateApplied(state: RobotState) {
        stateHistory.add(state)
        currentState = state
        if (state.finishReason != null) {
            throw FinishedException(state.finishReason, state, stateHistory)
        }
    }

    override fun sourceRepresentation(): String {
        return this::class.simpleName.toString()
    }
}

interface RobotExecutor {

    suspend fun execute(robotController: RobotController, callback: Callback)

    interface Callback {
        fun onWon()
        fun onFailure(e: Exception)
    }
}

typealias CreateRobotController = () -> RobotController

class CreateRobotControllerHolder {
    var instance: CreateRobotController? = null
}

interface RobotStatesApplier {
    suspend fun applyStates(states: List<RobotState>, callback: Callback)
    suspend fun robotMoved()

    interface Callback {
        suspend fun moveRobot(state: RobotState)
        fun onStateApplied(state: RobotState)
    }
}

class FinishedException(
    cause: Throwable?,
    state: RobotState,
    stateHistory: List<RobotState>,
) : IllegalStateException("state=$state, history:\n${formatHistory(stateHistory)}", cause)

class NotCompleteException(
    state: RobotState,
    stateHistory: List<RobotState>,
) : IllegalStateException(
    "level is not completed, state=$state, history:\n${formatHistory(stateHistory)}"
)

private fun formatHistory(stateHistory: List<RobotState>): String {
    return stateHistory.joinToString(separator = " ->\n") { "(${it})" }
}
