package com.gala.krobot.engine.program.visual.entity

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Immutable
@Serializable
data class VisualProgram(
    val levelName: String,
    val functionDefinitions: List<VisualFunctionDefinition> = emptyList(),
) {
    @Transient
    val flatLines = functionDefinitions.flatMap { it.lines }

    @Transient
    val availableActionSets: List<ActionSet> = ActionSet.available(program = this)

    fun modified(action: Action): VisualProgram = when (action) {
        Action.AddFunctionDefinition -> withNewFunction()
        Action.AddVariableDefinition -> withVariableDefinition()
        Action.AddCondition -> withCondition()
        Action.AddReturnStatement -> withReturnStatement()
        Action.AddParameterDefinition -> withParameterDefinition()
        is Action.AddParameterUsage -> withParameterUsage(count = action.count)
        Action.RemoveParameter -> withoutParameter()
        is Action.AddStatement -> withSingleStatement(action.statement)
        is Action.SetExpression -> withExpression(action.expression, action.index)

        is Action.SetName.FunctionDefinition,
        is Action.SetName.VariableDefinition -> withMainName(action.name)

        is Action.SetName.Parameter -> withParameterName(action.name, action.index)

        Action.Remove -> withoutSelected()
    }

    fun withLineSelected(line: VisualProgramLine): VisualProgram {
        return copy(
            functionDefinitions = functionDefinitions.map { definition ->
                var selectedLineFound = false
                definition.copy(
                    lines = definition.lines.map { currentLine ->
                        if (currentLine === line) {
                            selectedLineFound = true
                            currentLine.copy(isSelected = true)
                        } else {
                            currentLine.unselected()
                        }
                    },
                    isSelected = selectedLineFound,
                )
            }
        )
    }

    private fun withNewFunction(): VisualProgram {
        val newDefinition = VisualFunctionDefinition(
            isSelected = true,
            index = functionDefinitions.size,
        )
        val definitions = functionDefinitions.map { it.unselected() } + newDefinition
        return copy(functionDefinitions = definitions)
    }

    private fun withVariableDefinition(): VisualProgram = lineAfterSelected { definitionIndex ->
        newSelectedLine(
            functionDefinitionIndex = definitionIndex,
            symbols = listOf(
                VisualSymbol.Statement.VariableDefinitionMarker,
                VisualSymbol.Identifier.Undefined,
                VisualSymbol.Assign,
                VisualSymbol.Expression.Empty,
            )
        )
    }

    private fun withCondition(): VisualProgram {
        return lineAfterSelected { definitionIndex ->
            newSelectedLine(
                functionDefinitionIndex = definitionIndex,
                symbols = listOf(
                    VisualSymbol.ConditionMarker,
                    VisualSymbol.Expression.Empty,
                    VisualSymbol.Bracket.Curly.Open,
                )
            )
        }.lineAfterSelected { definitionIndex ->
            VisualProgramLine(
                isSelectable = true,
                functionDefinitionIndex = definitionIndex,
                symbols = listOf(VisualSymbol.Space) + VisualSymbol.Bracket.Curly.Close,
                isSelected = false,
            )
        }
    }

    private fun withSingleStatement(statement: VisualSymbol.Statement): VisualProgram =
        lineAfterSelected { definitionIndex ->
            newSelectedLine(definitionIndex, listOf(statement))
        }

    private fun withReturnStatement(): VisualProgram =
        lineAfterSelected { definitionIndex ->
            newSelectedLine(
                definitionIndex,
                listOf(VisualSymbol.Statement.Return, VisualSymbol.Expression.Empty)
            )
        }

    private fun lineAfterSelected(
        addLine: (definitionIndex: Int) -> VisualProgramLine,
    ): VisualProgram {
        val selectedDefinition = requireNotNull(selectedFunctionDefinition())
        val selectedLine = requireNotNull(selectedDefinition.selectedLine())
        val selectedDefinitionIndex = functionDefinitions.indexOf(selectedDefinition)
        require(selectedDefinitionIndex >= 0) { "selectedDefinition not found in functionDefinitions" }
        val newLine = addLine(selectedDefinitionIndex)
        return copy(
            functionDefinitions = functionDefinitions.map { definition ->
                if (definition === selectedDefinition) {
                    definition.copy(
                        isSelected = true,
                        lines = definition.lines.flatMap { line ->
                            if (line === selectedLine) {
                                listOf(
                                    if (newLine.isSelected) line.unselected() else line,
                                    when {
                                        newLine.isBlockEnd -> newLine
                                        line.isBlockBegin -> newLine.indented(line.indent + 1)
                                        line.indent != 0 -> newLine.indented(line.indent)
                                        else -> newLine
                                    },
                                )
                            } else {
                                listOf(
                                    if (newLine.isSelected) line.unselected() else line
                                )
                            }
                        }
                    )
                } else {
                    definition.unselected()
                }
            }
        )
    }

    private fun withMainName(identifier: VisualSymbol.Identifier): VisualProgram =
        mapFunctionDefinition(selected = { old ->
            var alreadyRenamed = false
            old.mapLine(selected = { line ->
                line.copy(
                    symbols = line.symbols.map { symbol ->
                        if (symbol is VisualSymbol.Identifier && !alreadyRenamed) {
                            alreadyRenamed = true
                            identifier
                        } else {
                            symbol
                        }
                    }
                )
            })
        })

    private fun withParameterName(identifier: VisualSymbol.Identifier, index: Int): VisualProgram =
        mapFunctionDefinition(selected = { old ->
            old.mapLine(selected = { line ->
                var roundBracketOpened = false
                var identifierIndex = 0
                line.copy(
                    symbols = line.symbols
                        .map { symbol ->
                            if (symbol is VisualSymbol.Bracket.Round.Open) {
                                roundBracketOpened = true
                            }
                            if (!roundBracketOpened || symbol !is VisualSymbol.Identifier) {
                                return@map symbol
                            }
                            val newSymbol = if (identifierIndex == index) identifier else symbol
                            identifierIndex++
                            newSymbol
                        }
                )
            })
        })

    private fun withParameterDefinition(): VisualProgram =
        mapFunctionDefinition(selected = { old ->
            old.mapLine(selected = { line ->
                line.copy(
                    symbols = line.symbols.flatMap { symbol ->
                        if (symbol is VisualSymbol.Identifier || symbol is VisualSymbol.Statement) {
                            listOf(
                                symbol,
                                VisualSymbol.Bracket.Round.Open,
                                VisualSymbol.Identifier.Undefined,
                                VisualSymbol.Bracket.Round.Close,
                            )
                        } else {
                            listOf(symbol)
                        }
                    }
                )
            })
        })

    private fun withParameterUsage(count: Int): VisualProgram =
        mapFunctionDefinition(selected = { old ->
            old.mapLine(selected = { line ->
                line.copy(
                    symbols = line.symbols.flatMap { symbol ->
                        if (symbol is VisualSymbol.Identifier || symbol is VisualSymbol.Statement) {
                            listOf(
                                symbol,
                                VisualSymbol.Bracket.Round.Open,
                                *buildList {
                                    repeat(count) { index ->
                                        if (index != 0) {
                                            add(VisualSymbol.Comma)
                                        }
                                        add(VisualSymbol.Identifier.Undefined)
                                        add(VisualSymbol.Assign)
                                        add(VisualSymbol.Expression.Empty)
                                    }
                                }.toTypedArray(),
                                VisualSymbol.Bracket.Round.Close,
                            )
                        } else {
                            listOf(symbol)
                        }
                    }
                )
            })
        })

    private fun withoutParameter(): VisualProgram =
        mapFunctionDefinition(selected = { old ->
            old.mapLine(selected = { line ->
                var roundBracketOpened = false
                line.copy(
                    symbols = line.symbols.mapNotNull { symbol ->
                        when (symbol) {
                            VisualSymbol.Bracket.Round.Open -> {
                                roundBracketOpened = true
                                null
                            }

                            VisualSymbol.Bracket.Round.Close -> {
                                roundBracketOpened = false
                                null
                            }

                            else -> {
                                if (roundBracketOpened)
                                    null
                                else
                                    symbol
                            }
                        }
                    }
                )
            })
        })

    private fun withExpression(expression: VisualSymbol.Expression, index: Int): VisualProgram =
        mapFunctionDefinition(selected = { old ->
            old.mapLine(selected = { line ->
                var expressionIndex = 0
                line.copy(
                    symbols = line.symbols.map { symbol ->
                        if (!symbol.isExpression(line)) return@map symbol
                        val newExpression = if (expressionIndex == index) expression else symbol
                        expressionIndex++
                        newExpression
                    }
                )
            })
        })

    // Is an expression in the current context. It's important for function calls with parameters.
    private fun VisualSymbol.isExpression(line: VisualProgramLine): Boolean {
        if (this !is VisualSymbol.Expression) return false
        if (line.functionParametersCount(functionDefinitions) == 0) return true
        var roundBracketOpened = false
        line.symbols.forEach { symbol ->
            if (symbol == VisualSymbol.Bracket.Round.Open) roundBracketOpened = true
            if (symbol === this) return roundBracketOpened
        }
        if (!roundBracketOpened) return true
        throw IllegalArgumentException("expression is not in the line, expression=$this, line=$line")
    }

    private fun withoutSelected(): VisualProgram {
        val selectedDefinition = requireNotNull(selectedFunctionDefinition())
        val selectedLine = requireNotNull(selectedDefinition.selectedLine())
        return when {
            selectedLine.isFunctionDefinition -> {
                var selectedSet = false
                copy(
                    functionDefinitions = functionDefinitions.mapNotNull { definition ->
                        if (definition === selectedDefinition)
                            null
                        else
                            if (!selectedSet) {
                                selectedSet = true
                                definition.withSelectedDefinition()
                            } else {
                                definition
                            }
                    }
                )
            }

            selectedLine.isCondition -> mapFunctionDefinition(selected = { old ->
                val lineIndex = old.lines.indexOf(selectedLine)
                var inCondition = false
                old.copy(
                    lines = old.lines.mapIndexedNotNull { index, line ->
                        when (index) {
                            lineIndex - 1 -> line.copy(isSelected = true)
                            lineIndex -> {
                                inCondition = true
                                null
                            }

                            else -> {
                                if (inCondition) {
                                    null
                                } else {
                                    line
                                }.also {
                                    if (inCondition &&
                                        line.symbols
                                            .dropWhile { it == VisualSymbol.Space }
                                            .firstOrNull() == VisualSymbol.Bracket.Curly.Close
                                    ) {
                                        inCondition = false
                                    }
                                }
                            }
                        }
                    }
                )
            })

            else -> mapFunctionDefinition(selected = { old ->
                val lineIndex = old.lines.indexOf(selectedLine)
                old.copy(
                    lines = old.lines.mapIndexedNotNull { index, line ->
                        when (index) {
                            lineIndex - 1 -> line.copy(isSelected = true)
                            lineIndex -> null
                            else -> line
                        }
                    }
                )
            })
        }
    }

    private fun newSelectedLine(
        functionDefinitionIndex: Int,
        symbols: List<VisualSymbol>,
    ): VisualProgramLine =
        VisualProgramLine(
            isSelectable = true,
            functionDefinitionIndex = functionDefinitionIndex,
            symbols = listOf(VisualSymbol.Space) + symbols,
            isSelected = true,
        )

    fun selectedFunctionDefinition(): VisualFunctionDefinition? =
        functionDefinitions.find { it.isSelected }

    companion object {
        fun empty(levelName: String) = VisualProgram(levelName = levelName)
            .modified(Action.AddFunctionDefinition)
            .modified(Action.SetName.FunctionDefinition(VisualSymbol.Identifier.Run))
    }
}

private inline fun VisualProgram.mapFunctionDefinition(
    unselected: (old: VisualFunctionDefinition) -> VisualFunctionDefinition = { it },
    selected: (old: VisualFunctionDefinition) -> VisualFunctionDefinition,
): VisualProgram {
    val selectedDefinition = requireNotNull(selectedFunctionDefinition())
    return copy(
        functionDefinitions = functionDefinitions.map { definition ->
            if (definition === selectedDefinition)
                selected(definition)
            else
                unselected(definition)
        }
    )
}
