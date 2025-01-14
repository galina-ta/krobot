package com.gala.maze.common.program.models

sealed interface Command {

    sealed interface Usage : Command {

        sealed interface Function : Usage {

            sealed interface SetArena : Function {
                data object SetDemoArena : SetArena
                data object SetArena1 : SetArena
                data object SetHomework1Variant1Arena : SetArena
                data object SetHomework1Variant2Arena : SetArena
                data object SetHomework1Variant3Arena : SetArena
            }

            sealed interface Move : Function {
                val stepsCount: Int

                data class Left(override val stepsCount: Int) : Move
                data class Right(override val stepsCount: Int) : Move
                data class Up(override val stepsCount: Int) : Move
                data class Down(override val stepsCount: Int) : Move
            }
        }
    }

    sealed interface Definition : Command {
        data class Function(val name: String, val subcommands: List<Command>) : Definition
    }

    companion object {
        const val MAIN_NAME = "main"
    }
}
