package com.gala.maze.common.program

import com.gala.maze.common.program.models.Token

data class Program(
    val tokens: List<Token.FunctionDefinition>,
) {
    companion object {
        val empty = Program(
            tokens = listOf(
                Token.FunctionDefinition(
                    name = "",
                    isMain = true,
                    tokens = emptyList(),
                )
            )
        )

        fun setLevel(levelName: String): Program = Program(
            tokens = listOf(
                Token.FunctionDefinition(
                    name = "",
                    isMain = true,
                    tokens = listOf(Token.Usage.Function.SetArena(name = levelName)),
                )
            )
        )
    }
}
