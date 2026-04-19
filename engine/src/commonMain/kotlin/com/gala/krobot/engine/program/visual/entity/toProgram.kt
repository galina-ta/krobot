package com.gala.krobot.engine.program.visual.entity

import com.gala.krobot.engine.program.Program
import com.gala.krobot.engine.program.entity.Token
import com.gala.krobot.engine.program.entity.Token.Statement.FunctionCall.DefinedFunction
import com.gala.krobot.engine.program.entity.Token.Statement.FunctionCall.Move.Down
import com.gala.krobot.engine.program.entity.Token.Statement.FunctionCall.Move.Left
import com.gala.krobot.engine.program.entity.Token.Statement.FunctionCall.Move.Right
import com.gala.krobot.engine.program.entity.Token.Statement.FunctionCall.Move.Up
import com.gala.krobot.engine.program.entity.Token.Statement.FunctionCall.SetLevel
import com.gala.krobot.engine.program.entity.Token.Statement.FunctionCall.Use
import com.gala.krobot.engine.program.entity.Token.Statement.Return
import com.gala.krobot.engine.program.entity.Token.Statement.VariableDefinition

fun VisualProgram.toProgram(): Program = Program(
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
                val followingSymbols = line.symbols.drop(1)
                when (firstSymbol) {
                    is VisualSymbol.Statement.FunctionCall.Move -> {
                        val stepCount = line
                            .parameterSymbol<VisualSymbol.Expression>()
                            ?.toToken(followingSymbols)
                        when (firstSymbol) {
                            VisualSymbol.Statement.FunctionCall.Move.Down ->
                                Down(stepCount)

                            VisualSymbol.Statement.FunctionCall.Move.Left ->
                                Left(stepCount)

                            VisualSymbol.Statement.FunctionCall.Move.Right ->
                                Right(stepCount)

                            VisualSymbol.Statement.FunctionCall.Move.Up ->
                                Up(stepCount)
                        }
                    }

                    is VisualSymbol.Statement.FunctionCall.SetLevel ->
                        SetLevel(firstSymbol.name)

                    is VisualSymbol.Statement.FunctionCall.User -> {
                        val expressionSymbol = line.parameterSymbol<VisualSymbol.Expression>()
                        DefinedFunction(
                            name = firstSymbol.name.name,
                            parameter = expressionSymbol?.toToken(followingSymbols),
                        )
                    }

                    VisualSymbol.Statement.FunctionCall.Use -> {
                        val expressionSymbol = line.parameterSymbol<VisualSymbol.Expression>()
                        Use(
                            what = expressionSymbol?.toToken(followingSymbols)
                        )
                    }

                    VisualSymbol.Statement.Return -> {
                        val expressionSymbol = requireNotNull(line.firstExpression)
                        Return(
                            what = expressionSymbol.toToken(followingSymbols),
                        )
                    }

                    VisualSymbol.Statement.VariableDefinitionMarker -> {
                        val identifier = requireNotNull(line.firstIdentifier)
                        val expressionSymbol = requireNotNull(line.firstExpression)
                        VariableDefinition(
                            name = identifier.name,
                            value = expressionSymbol.toToken(followingSymbols),
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
                    VisualSymbol.Comma,
                    null -> null

                    VisualSymbol.ConditionMarker -> null // TODO
                    VisualSymbol.Statement.FunctionCall.Equal -> null // TODO
                }
            }
        )
    }
)

private fun VisualSymbol.Expression.toToken(
    followingSymbols: List<VisualSymbol>,
): Token.Expression = when (this) {
    VisualSymbol.Expression.Empty -> Token.Expression.Empty
    VisualSymbol.Get -> Token.Get
    is VisualSymbol.Literal -> Token.Literal(value = value)
    is VisualSymbol.ParameterUsage -> Token.ParameterUsage(name.name)
    is VisualSymbol.VariableUsage -> Token.VariableUsage(name.name)
    is VisualSymbol.Statement.FunctionCall.User ->
        DefinedFunction(
            name = name.name,
            parameter = followingSymbols.parameterSymbol<VisualSymbol.Expression>()?.toToken(
                followingSymbols = followingSymbols.parameterSymbols()
            )
        )

    VisualSymbol.Statement.FunctionCall.Equal -> TODO()
}
