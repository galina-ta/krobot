package com.gala.krobot.engine.program.visual.model

import androidx.compose.runtime.Immutable

@Immutable
data class VisualProgram(
    val functionDefinitions: List<VisualFunctionDefinition> = emptyList(),
) {
    val flatLines = functionDefinitions.flatMap { it.lines }

    val availableActionSets: List<ActionSet> = ActionSet.available(program = this)

    fun modified(action: Action): VisualProgram = when (action) {
        Action.AddFunctionDefinition -> withNewFunction()
        Action.AddVariableDefinition -> withVariableDefinition()
        Action.AddReturnStatement -> withReturnStatement()
        Action.AddParameter -> withParameter()
        Action.RemoveParameter -> withoutParameter()
        is Action.AddStatement -> withSingleStatement(action.statement)
        is Action.SetExpression -> withExpression(action.expression)

        is Action.SetName.FunctionDefinition,
        is Action.SetName.VariableDefinition -> withMainName(action.name)

        is Action.SetName.Parameter -> withParameterName(action.name)

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
        return copy(
            functionDefinitions = functionDefinitions.mapIndexed { definitionIndex, definition ->
                if (definition === selectedDefinition) {
                    definition.copy(
                        isSelected = true,
                        lines = definition.lines.flatMap { line ->
                            if (line === selectedLine) {
                                listOf(
                                    line.unselected(),
                                    addLine(definitionIndex),
                                )
                            } else {
                                listOf(
                                    line.unselected()
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

    private fun withParameterName(identifier: VisualSymbol.Identifier): VisualProgram =
        mapFunctionDefinition(selected = { old ->
            old.mapLine(selected = { line ->
                var roundBracketOpened = false
                line.copy(
                    symbols = line.symbols
                        .map { symbol ->
                            if (symbol is VisualSymbol.Bracket.Round.Open) {
                                roundBracketOpened = true
                            }
                            if (roundBracketOpened && symbol is VisualSymbol.Identifier)
                                identifier
                            else
                                symbol
                        }
                )
            })
        })

    private fun withParameter(): VisualProgram =
        mapFunctionDefinition(selected = { old ->
            old.mapLine(selected = { line ->
                line.copy(
                    symbols = line.symbols.flatMap { symbol ->
                        if (symbol is VisualSymbol.Identifier || symbol is VisualSymbol.Statement) {
                            listOf(
                                symbol,
                                VisualSymbol.Bracket.Round.Open,
                                VisualSymbol.Identifier.Undefined,
                                VisualSymbol.Assign,
                                VisualSymbol.Expression.Empty,
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

    private fun withExpression(expression: VisualSymbol.Expression): VisualProgram =
        mapFunctionDefinition(selected = { old ->
            old.mapLine(selected = { line ->
                line.copy(
                    symbols = line.symbols.map { symbol ->
                        if (symbol is VisualSymbol.Expression)
                            expression
                        else
                            symbol
                    }
                )
            })
        })

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
        val empty = VisualProgram()
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
