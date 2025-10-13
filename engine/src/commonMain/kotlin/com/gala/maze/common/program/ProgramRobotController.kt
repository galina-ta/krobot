package com.gala.maze.common.program

import com.gala.maze.common.arena.RobotController
import com.gala.maze.common.arena.entity.arena.Arena
import com.gala.maze.common.program.models.Token
import com.gala.maze.levels.arena1
import com.gala.maze.levels.demoArena
import com.gala.maze.levels.homework1Variant1Arena
import com.gala.maze.levels.homework1Variant2Arena
import com.gala.maze.levels.homework1Variant3Arena

class ProgramRobotController(
    private val program: Program,
    private val dynamicLevelName: String,
    private val dynamicLevel: Arena,
) : RobotController() {

    override suspend fun run() {
        println("run $program")
        val mainFunction = program.tokens.first { it.isMain }
        println("mainFunction=$mainFunction")
        executeFunction(mainFunction)
    }

    private suspend fun executeFunction(definition: Token.FunctionDefinition) {
        println("executeFunction definition=$definition")
        definition.tokens.forEach { command ->
            println("executeFunction command=$command")
            when (command) {
                is Token.Usage.Function -> executeFunctionUsage(usage = command)
            }
        }
    }

    private suspend fun executeFunctionUsage(usage: Token.Usage.Function) {
        println("executeFunctionUsage $usage")
        when (usage) {
            is Token.Usage.Function.Move.Left -> moveLeft(usage.stepsCount)
            is Token.Usage.Function.Move.Right -> moveRight(usage.stepsCount)
            is Token.Usage.Function.Move.Up -> moveUp(usage.stepsCount)
            is Token.Usage.Function.Move.Down -> moveDown(usage.stepsCount)
            is Token.Usage.Function.SetArena -> {
                setArena(
                    when (usage.name) {
                        "demoArena" -> demoArena
                        "arena1" -> arena1
                        "homework1Variant1Arena" -> homework1Variant1Arena
                        "homework1Variant2Arena" -> homework1Variant2Arena
                        "homework1Variant3Arena" -> homework1Variant3Arena
                        dynamicLevelName -> dynamicLevel
                        else -> throw IllegalArgumentException("arena ${usage.name} is not registered")
                    }
                )
            }
        }
    }
}
