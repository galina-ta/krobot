package com.gala.krobot.engine.program.visual.model

import com.gala.krobot.engine.program.Program
import com.gala.krobot.engine.program.models.Token

fun VisualProgram.toProgram(levelName: String): Program = Program(
    functionDefinitions = functionDefinitions.map { definition ->
        val identifier = definition.name
        Token.FunctionDefinition(
            name = identifier.name,
            parameterName = definition.parameterName?.name,
            isMain = identifier == VisualSymbol.Identifier.Run,
            statements = definition.lines.drop(1).mapNotNull { line ->
                val firstSymbol = line.symbols
                    .dropWhile { it == VisualSymbol.Space }
                    .firstOrNull()
                when (firstSymbol) {
                    is VisualSymbol.Statement.FunctionCall.Move -> {
                        val stepCount = line
                            .parameterSymbol<VisualSymbol.Expression>()
                            ?.toToken(line)
                        when (firstSymbol) {
                            VisualSymbol.Statement.FunctionCall.Move.Down ->
                                Token.Statement.FunctionCall.Move.Down(stepCount)

                            VisualSymbol.Statement.FunctionCall.Move.Left ->
                                Token.Statement.FunctionCall.Move.Left(stepCount)

                            VisualSymbol.Statement.FunctionCall.Move.Right ->
                                Token.Statement.FunctionCall.Move.Right(stepCount)

                            VisualSymbol.Statement.FunctionCall.Move.Up ->
                                Token.Statement.FunctionCall.Move.Up(stepCount)
                        }
                    }

                    VisualSymbol.Statement.FunctionCall.SetLevel ->
                        Token.Statement.FunctionCall.SetLevel(levelName)

                    is VisualSymbol.Statement.FunctionCall.User -> {
                        val expressionSymbol = line.parameterSymbol<VisualSymbol.Expression>()
                        Token.Statement.FunctionCall.DefinedFunction(
                            name = firstSymbol.name.name,
                            parameter = expressionSymbol?.toToken(line),
                        )
                    }

                    VisualSymbol.Statement.FunctionCall.Use -> {
                        val expressionSymbol = line.parameterSymbol<VisualSymbol.Expression>()
                        Token.Statement.FunctionCall.Use(
                            what = expressionSymbol?.toToken(line)
                        )
                    }

                    VisualSymbol.Statement.Return -> {
                        val expressionSymbol = requireNotNull(line.firstExpression)
                        Token.Statement.Return(
                            what = expressionSymbol.toToken(line),
                        )
                    }

                    VisualSymbol.Statement.VariableDefinitionMarker -> {
                        val identifier = requireNotNull(line.firstIdentifier)
                        val expressionSymbol = requireNotNull(line.firstExpression)
                        Token.Statement.VariableDefinition(
                            name = identifier.name,
                            value = expressionSymbol.toToken(line),
                        )
                    }

                    is VisualSymbol.Bracket.Curly,
                    is VisualSymbol.Bracket.Round,
                    is VisualSymbol.FunctionDefinitionMarker,
                    is VisualSymbol.Identifier,
                    is VisualSymbol.ParameterUsage,
                    is VisualSymbol.VariableUsage,
                    is VisualSymbol.Literal,
                    VisualSymbol.Expression.Empty,
                    VisualSymbol.Get,
                    VisualSymbol.Assign,
                    VisualSymbol.Remove,
                    VisualSymbol.Space,
                    null -> null
                }
            }
        )
    }
)

private fun VisualSymbol.Expression.toToken(
    line: VisualProgramLine,
): Token.Expression = when (this) {
    VisualSymbol.Expression.Empty -> Token.Expression.Empty
    VisualSymbol.Get -> Token.Get
    is VisualSymbol.Literal -> Token.Literal(value = value)
    is VisualSymbol.ParameterUsage -> Token.ParameterUsage(name.name)
    is VisualSymbol.VariableUsage -> Token.VariableUsage(name.name)
    is VisualSymbol.Statement.FunctionCall.User ->
        Token.Statement.FunctionCall.DefinedFunction(
            name = name.name,
            parameter = line.parameterSymbol<VisualSymbol.Expression>()?.toToken(line)
        )
}
