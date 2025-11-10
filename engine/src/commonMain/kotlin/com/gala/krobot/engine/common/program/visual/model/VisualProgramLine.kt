package com.gala.krobot.engine.common.program.visual.model

data class VisualProgramLine(
    val isSelectable: Boolean,
    val functionDefinitionIndex: Int,
    val symbols: List<VisualSymbol>,
    val isSelected: Boolean,
) {
    val isFunctionDefinition: Boolean =
        symbols.any { it is VisualSymbol.FunctionDefinitionMarker }

    val isVariableDefinition: Boolean =
        symbols.any { it is VisualSymbol.Statement.VariableDefinitionMarker }

    val isFunctionCall: Boolean =
        symbols.any { it is VisualSymbol.Statement.FunctionCall }

    val hasOpenedRoundBracket: Boolean =
        symbols.any { it is VisualSymbol.Bracket.Round.Open }

    val isReturnStatement: Boolean =
        symbols.any { it is VisualSymbol.Statement.Return }

    val firstIdentifier: VisualSymbol.Identifier? =
        symbols.filterIsInstance<VisualSymbol.Identifier>().firstOrNull()

    val firstExpression: VisualSymbol.Expression? =
        symbols.filterIsInstance<VisualSymbol.Expression>().firstOrNull()

    inline fun <reified T : VisualSymbol> parameterSymbol(): T? {
        var parametersStarted = false
        symbols.forEach { symbol ->
            if (symbol is VisualSymbol.Bracket.Round.Open) parametersStarted = true
            if (parametersStarted && symbol is T) return symbol
        }
        return null
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

            VisualSymbol.Statement.FunctionCall.SetLevel,
            null -> emptyList()
        }
    }

    fun unselected() = if (isSelected) copy(isSelected = false) else this
}
