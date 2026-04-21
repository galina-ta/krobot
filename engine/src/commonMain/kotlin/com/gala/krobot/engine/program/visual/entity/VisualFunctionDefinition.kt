package com.gala.krobot.engine.program.visual.entity

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class VisualFunctionDefinition(
    val isSelected: Boolean,
    val lines: List<VisualProgramLine>,
) {
    constructor(isSelected: Boolean, index: Int) : this(
        isSelected = isSelected,
        lines = listOf(
            VisualProgramLine(
                isSelectable = true,
                isSelected = true,
                functionDefinitionIndex = index,
                symbols = listOf(
                    VisualSymbol.FunctionDefinitionMarker,
                    VisualSymbol.Identifier.Undefined,
                    VisualSymbol.Bracket.Curly.Open,
                ),
            ),
            VisualProgramLine(
                isSelectable = false,
                isSelected = false,
                functionDefinitionIndex = index,
                symbols = listOf(
                    VisualSymbol.Bracket.Curly.Close,
                ),
            ),
        ),
    )

    @Transient
    val name = requireNotNull(lines.first { it.isFunctionDefinition }.firstIdentifier)

    @Transient
    val parameterName: VisualSymbol.Identifier? =
        lines.first { it.isFunctionDefinition }.parameterSymbol(0)

    @Transient
    val variableDefinitionNames: List<VisualSymbol.Identifier> =
        lines.mapNotNull { line ->
            line.firstIdentifier.takeIf { line.isVariableDefinition }
        }

    fun selectedLine(): VisualProgramLine? =
        lines.find { it.isSelected }

    @Transient
    val hasReturnValue: Boolean =
        lines.any { it.isReturnStatement }

    fun unselected(): VisualFunctionDefinition = if (isSelected)
        copy(
            isSelected = false,
            lines = lines.map { line ->
                line.unselected()
            }
        )
    else
        this

    fun withSelectedDefinition(): VisualFunctionDefinition = copy(
        isSelected = true,
        lines = lines.map { line ->
            if (line.isFunctionDefinition)
                line.copy(isSelected = true)
            else
                line.unselected()
        }
    )
}

inline fun VisualFunctionDefinition.mapLine(
    unselected: (definitionLine: VisualProgramLine) -> VisualProgramLine = { it },
    selected: (definitionLine: VisualProgramLine) -> VisualProgramLine,
): VisualFunctionDefinition = copy(
    lines = lines.map { line ->
        if (line.isSelected) {
            selected(line)
        } else {
            unselected(line)
        }
    }
)
