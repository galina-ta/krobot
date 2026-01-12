package com.gala.krobot.engine.program.visual.entity

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class VisualProgramLine(
    val isSelectable: Boolean,
    val functionDefinitionIndex: Int,
    val symbols: List<VisualSymbol>,
    val isSelected: Boolean,
) {
    @Transient
    val isFunctionDefinition: Boolean =
        symbols.any { it is VisualSymbol.FunctionDefinitionMarker }

    @Transient
    val isVariableDefinition: Boolean =
        symbols.any { it is VisualSymbol.Statement.VariableDefinitionMarker }

    @Transient
    val isFunctionCall: Boolean =
        symbols.any { it is VisualSymbol.Statement.FunctionCall }

    @Transient
    val hasOpenedRoundBracket: Boolean =
        symbols.any { it is VisualSymbol.Bracket.Round.Open }

    @Transient
    val isReturnStatement: Boolean =
        symbols.any { it is VisualSymbol.Statement.Return }

    @Transient
    val firstIdentifier: VisualSymbol.Identifier? =
        symbols.filterIsInstance<VisualSymbol.Identifier>().firstOrNull()

    @Transient
    val firstExpression: VisualSymbol.Expression? =
        symbols.filterIsInstance<VisualSymbol.Expression>().firstOrNull()

    inline fun <reified T : VisualSymbol> parameterSymbol(): T? {
        return symbols.parameterSymbol()
    }

    fun functionParameterNames(
        definitions: List<VisualFunctionDefinition>,
    ): List<VisualSymbol.Identifier> {
        val functionCall = symbols
            .filterIsInstance<VisualSymbol.Statement.FunctionCall>()
            .firstOrNull()
        return when (functionCall) {
            is VisualSymbol.Statement.FunctionCall.Move ->
                listOf(VisualSymbol.Identifier.StepCount)

            is VisualSymbol.Statement.FunctionCall.Use ->
                listOf(
                    VisualSymbol.Identifier.Code,
                    VisualSymbol.Identifier.Key,
                )

            is VisualSymbol.Statement.FunctionCall.User -> {
                val definition = definitions.find { it.name == functionCall.name }
                listOfNotNull(definition?.parameterName)
            }

            is VisualSymbol.Statement.FunctionCall.SetLevel,
            null -> emptyList()
        }
    }

    fun unselected() = if (isSelected) copy(isSelected = false) else this
}
