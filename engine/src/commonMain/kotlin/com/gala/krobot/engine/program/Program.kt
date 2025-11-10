package com.gala.krobot.engine.program

import com.gala.krobot.engine.program.entity.Token

data class Program(
    val functionDefinitions: List<Token.FunctionDefinition>,
) {
    companion object {
        val empty = Program(
            functionDefinitions = listOf(
                Token.FunctionDefinition(
                    name = "",
                    parameterName = null,
                    statements = emptyList(),
                    isMain = true,
                )
            )
        )

        fun setLevel(levelName: String): Program = Program(
            functionDefinitions = listOf(
                Token.FunctionDefinition(
                    name = "",
                    parameterName = null,
                    statements = listOf(Token.Statement.FunctionCall.SetLevel(name = levelName)),
                    isMain = true,
                )
            )
        )
    }
}
