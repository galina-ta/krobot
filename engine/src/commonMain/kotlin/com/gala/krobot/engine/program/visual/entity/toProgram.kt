package com.gala.krobot.engine.program.visual.entity

import com.gala.krobot.engine.program.Program
import com.gala.krobot.engine.program.entity.Token
import com.gala.krobot.engine.program.entity.Token.Equal
import com.gala.krobot.engine.program.entity.Token.Statement.Condition
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
            statements = getStatements(definition.lines.drop(1)),
        )
    }
)

private fun getStatements(lines: List<VisualProgramLine>): List<Token.Statement> = buildList {
    var lineIndex = 0
    while (lineIndex < lines.size) {
        val line = lines[lineIndex]
        val firstSymbol = line.symbols
            .dropWhile { it == VisualSymbol.Space }
            .firstOrNull()
        val followingSymbols = line.symbols.drop(1)
        when (firstSymbol) {
            is VisualSymbol.Statement.FunctionCall.Move -> {
                val stepCount = line
                    .parameterSymbol<VisualSymbol.Expression>(0)
                    ?.toToken(followingSymbols)
                add(
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
                )
            }

            is VisualSymbol.Statement.FunctionCall.SetLevel ->
                add(SetLevel(firstSymbol.name))

            is VisualSymbol.Statement.FunctionCall.User -> {
                val expressionSymbol = line.parameterSymbol<VisualSymbol.Expression>(0)
                add(
                    DefinedFunction(
                        name = firstSymbol.name.name,
                        parameter = expressionSymbol?.toToken(followingSymbols),
                    )
                )
            }

            VisualSymbol.Statement.FunctionCall.Use -> {
                val expressionSymbol = line.parameterSymbol<VisualSymbol.Expression>(0)
                add(
                    Use(
                        what = expressionSymbol?.toToken(followingSymbols)
                    )
                )
            }

            VisualSymbol.Statement.Return -> {
                val expressionSymbol = requireNotNull(line.firstExpression)
                add(
                    Return(
                        what = expressionSymbol.toToken(followingSymbols),
                    )
                )
            }

            VisualSymbol.Statement.VariableDefinitionMarker -> {
                val identifier = requireNotNull(line.firstIdentifier)
                val expressionSymbol = requireNotNull(line.firstExpression)
                add(
                    VariableDefinition(
                        name = identifier.name,
                        value = expressionSymbol.toToken(followingSymbols),
                    )
                )
            }

            VisualSymbol.ConditionMarker -> {
                val predicate = requireNotNull(line.firstExpression)
                lineIndex++
                var blockLine = lines[lineIndex]
                val blockLines = mutableListOf<VisualProgramLine>()
                while (!blockLine.isBlockEnd) {
                    blockLines.add(blockLine)
                    lineIndex++
                    blockLine = lines[lineIndex]
                }
                add(
                    Condition(
                        predicate = predicate.toToken(followingSymbols),
                        statements = getStatements(blockLines)
                    )
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
            VisualSymbol.Equal,
            null -> Unit
        }
        lineIndex++
    }
}

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
            parameter = followingSymbols.parameterSymbol<VisualSymbol.Expression>(0)?.toToken(
                followingSymbols = followingSymbols.parameterSymbols()
            )
        )

    VisualSymbol.Equal -> Equal(
        what = requireNotNull(
            followingSymbols.parameterSymbol<VisualSymbol.Expression>(0)?.toToken(
                followingSymbols = followingSymbols.parameterSymbols()
            )
        ),
        to = requireNotNull(
            followingSymbols.parameterSymbol<VisualSymbol.Expression>(1)?.toToken(
                followingSymbols = followingSymbols.parameterSymbols()
            )
        )
    )
}
