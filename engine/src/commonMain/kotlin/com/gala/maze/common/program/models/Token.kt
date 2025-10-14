package com.gala.maze.common.program.models

sealed interface Token {

    sealed interface Usage : Token {

        sealed interface Function : Usage {

            data class DefinedFunction(val name: String) : Function

            data class SetArena(val name: String) : Function

            sealed interface Move : Function {
                val stepsCount: Int

                data class Left(override val stepsCount: Int) : Move
                data class Right(override val stepsCount: Int) : Move
                data class Up(override val stepsCount: Int) : Move
                data class Down(override val stepsCount: Int) : Move
            }
        }
    }

    data class FunctionDefinition(
        val name: String,
        val tokens: List<Usage>,
        val isMain: Boolean,
    ) : Token
}
