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
            is VisualSymbol.FunctionCall.Move -> {
                val stepCount = line
                    .parameterSymbol<VisualSymbol.Expression>(0)
                    ?.toToken(followingSymbols)
                add(
                    when (firstSymbol) {
                        VisualSymbol.FunctionCall.Move.Down ->
                            Down(stepCount)

                        VisualSymbol.FunctionCall.Move.Left ->
                            Left(stepCount)

                        VisualSymbol.FunctionCall.Move.Right ->
                            Right(stepCount)

                        VisualSymbol.FunctionCall.Move.Up ->
                            Up(stepCount)
                    }
                )
            }

            is VisualSymbol.FunctionCall.SetLevel ->
                add(SetLevel(firstSymbol.name))

            is VisualSymbol.FunctionCall.User -> {
                val expressionSymbol = line.parameterSymbol<VisualSymbol.Expression>(0)
                add(
                    DefinedFunction(
                        name = firstSymbol.name.name,
                        parameter = expressionSymbol?.toToken(followingSymbols),
                    )
                )
            }

            VisualSymbol.FunctionCall.Use -> {
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
                val identifier = requireNotNull(line.firstIdentifier) {
                    "variable identifier must not be null"
                }
                val expressionSymbol = requireNotNull(line.firstExpression) {
                    "variable expression must not be null"
                }
                add(
                    VariableDefinition(
                        name = identifier.name,
                        value = expressionSymbol.toToken(followingSymbols),
                    )
                )
            }

            VisualSymbol.ConditionMarker -> {
                val predicate = requireNotNull(line.firstExpression) {
                    "condition predicate must not be null"
                }
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
            VisualSymbol.FunctionCall.Get,
            VisualSymbol.FunctionCall.Equal,
            VisualSymbol.Assign,
            VisualSymbol.Remove,
            VisualSymbol.Space,
            VisualSymbol.Comma,
            null -> Unit
        }
        lineIndex++
    }
}

private fun VisualSymbol.Expression.toToken(
    followingSymbols: List<VisualSymbol>,
): Token.Expression = when (this) {
    VisualSymbol.Expression.Empty -> Token.Expression.Empty
    VisualSymbol.FunctionCall.Get -> Token.Get
    is VisualSymbol.Literal -> Token.Literal(value = value)
    is VisualSymbol.ParameterUsage -> Token.ParameterUsage(name.name)
    is VisualSymbol.VariableUsage -> Token.VariableUsage(name.name)
    is VisualSymbol.FunctionCall.User ->
        DefinedFunction(
            name = name.name,
            parameter = followingSymbols.parameterSymbol<VisualSymbol.Expression>(0)?.toToken(
                followingSymbols = followingSymbols.parameterSymbols()
            )
        )

    VisualSymbol.FunctionCall.Equal -> Equal(
        what = followingSymbols.parameterSymbol<VisualSymbol.Expression>(0)?.toToken(
            followingSymbols = followingSymbols.parameterSymbols()
        ),
        to = followingSymbols.parameterSymbol<VisualSymbol.Expression>(1)?.toToken(
            followingSymbols = followingSymbols.parameterSymbols()
        )
    )
}
