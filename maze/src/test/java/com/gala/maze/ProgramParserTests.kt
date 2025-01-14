package com.gala.maze

import com.gala.maze.common.program.ProgramParser
import com.gala.maze.common.program.models.Command
import org.junit.Assert.assertEquals
import org.junit.Test

class ProgramParserTests {
    private val parser = ProgramParser()

    @Test
    fun justMain() {

        val code = """
            fun main() {
                moveRight()
                moveRight()
                moveDown()
            }
        """.trimIndent()

        val commands = parser.parse(code)

        assertEquals(
            listOf(
                Command.Definition.Function(
                    name = Command.MAIN_NAME,
                    subcommands = listOf(
                        Command.Usage.Function.Move.Right(stepsCount = 1),
                        Command.Usage.Function.Move.Right(stepsCount = 1),
                        Command.Usage.Function.Move.Down(stepsCount = 1),
                    ),
                ),
            ),
            commands
        )
    }
}
