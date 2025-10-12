package com.gala.maze.common.program

import com.gala.maze.common.arena.RobotController
import com.gala.maze.common.program.models.Command
import com.gala.maze.levels.arena1
import com.gala.maze.levels.demoArena
import com.gala.maze.levels.homework1Variant1Arena
import com.gala.maze.levels.homework1Variant2Arena
import com.gala.maze.levels.homework1Variant3Arena

class ProgramRobotController(
    private val commands: List<Command>,
) : RobotController() {

    override suspend fun run() {
        val functionDefinitions = commands.filterIsInstance<Command.Definition.Function>()
        val mainFunction = functionDefinitions.first { it.name == Command.MAIN_NAME }
        executeFunction(mainFunction)
    }

    private suspend fun executeFunction(definition: Command.Definition.Function) {
        definition.subcommands.forEach { command ->
            when (command) {
                is Command.Usage.Function -> executeFunctionUsage(usage = command)
                is Command.Definition.Function -> error("can not define an inner function")
            }
        }
    }

    private suspend fun executeFunctionUsage(usage: Command.Usage.Function) {
        when (usage) {
            is Command.Usage.Function.Move.Left -> moveLeft(usage.stepsCount)
            is Command.Usage.Function.Move.Right -> moveRight(usage.stepsCount)
            is Command.Usage.Function.Move.Up -> moveUp(usage.stepsCount)
            is Command.Usage.Function.Move.Down -> moveDown(usage.stepsCount)
            Command.Usage.Function.SetArena.SetDemoArena -> setArena(demoArena)
            Command.Usage.Function.SetArena.SetArena1 -> setArena(arena1)
            Command.Usage.Function.SetArena.SetHomework1Variant1Arena ->
                setArena(homework1Variant1Arena)
            Command.Usage.Function.SetArena.SetHomework1Variant2Arena ->
                setArena(homework1Variant2Arena)
            Command.Usage.Function.SetArena.SetHomework1Variant3Arena ->
                setArena(homework1Variant3Arena)
        }
    }
}
