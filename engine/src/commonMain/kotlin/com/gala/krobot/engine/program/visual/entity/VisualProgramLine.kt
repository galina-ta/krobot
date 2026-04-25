package com.gala.krobot.engine.program.visual.entity

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class VisualProgramLine(
    val isSelectable: Boolean,
    val functionDefinitionIndex: Int,
    val symbols: List<VisualSymbol>,
    val isSelected: Boolean,
    val indent: Int = 0,
) {
    @Transient
    val isFunctionDefinition: Boolean =
        symbols.any { it is VisualSymbol.FunctionDefinitionMarker }

    @Transient
    val isVariableDefinition: Boolean =
        symbols.any { it is VisualSymbol.Statement.VariableDefinitionMarker }

    @Transient
    val isFunctionCall: Boolean =
        symbols.any { it is VisualSymbol.FunctionCall }

    @Transient
    val hasOpenedRoundBracket: Boolean =
        symbols.any { it is VisualSymbol.Bracket.Round.Open }

    @Transient
    val isReturnStatement: Boolean =
        symbols.any { it is VisualSymbol.Statement.Return }

    @Transient
    val isCondition: Boolean =
        symbols.any { it is VisualSymbol.ConditionMarker }

    @Transient
    val isBlockEnd: Boolean =
        symbols.dropWhile { it == VisualSymbol.Space }
            .firstOrNull() == VisualSymbol.Bracket.Curly.Close

    @Transient
    val isBlockBegin: Boolean = isCondition

    @Transient
    val firstIdentifier: VisualSymbol.Identifier? =
        symbols.filterIsInstance<VisualSymbol.Identifier>().firstOrNull()

    @Transient
    val firstExpression: VisualSymbol.Expression? =
        symbols.filterIsInstance<VisualSymbol.Expression>().firstOrNull()

    inline fun <reified T : VisualSymbol> parameterSymbol(index: Int): T? {
        return symbols.parameterSymbol(index)
    }

    fun functionParameterNames(
        definitions: List<VisualFunctionDefinition>,
    ): List<VisualSymbol.Identifier> {
        val functionCall = symbols
            .filterIsInstance<VisualSymbol.FunctionCall>()
            .firstOrNull()
        return when (functionCall) {
            is VisualSymbol.FunctionCall.Move ->
                listOf(VisualSymbol.Identifier.StepCount)

            is VisualSymbol.FunctionCall.Use ->
                listOf(
                    VisualSymbol.Identifier.Code,
                    VisualSymbol.Identifier.Key,
                )

            VisualSymbol.FunctionCall.Equal ->
                listOf(
                    VisualSymbol.Identifier.What,
                    VisualSymbol.Identifier.To,
                )

            is VisualSymbol.FunctionCall.User -> {
                val definition = definitions.find { it.name == functionCall.name }
                listOfNotNull(definition?.parameterName)
            }

            is VisualSymbol.FunctionCall.SetLevel,
            VisualSymbol.FunctionCall.Get,
            null -> emptyList()
        }
    }

    fun functionParametersCount(definitions: List<VisualFunctionDefinition>): Int {
        val functionCall = symbols
            .filterIsInstance<VisualSymbol.FunctionCall>()
            .firstOrNull()
        return when (functionCall) {
            is VisualSymbol.FunctionCall.Move -> 1
            is VisualSymbol.FunctionCall.Use -> 1
            VisualSymbol.FunctionCall.Equal -> 2

            is VisualSymbol.FunctionCall.User -> {
                val definition = definitions.find { it.name == functionCall.name }
                if (definition?.parameterName != null) 1 else 0
            }

            is VisualSymbol.FunctionCall.SetLevel,
            VisualSymbol.FunctionCall.Get,
            null -> 0
        }
    }

    fun unselected() = if (isSelected) copy(isSelected = false) else this

    fun indented(indent: Int): VisualProgramLine = copy(
        indent = indent,
        symbols = (0..<indent).map { VisualSymbol.Space } + symbols,
    )
}
