package com.gala.maze.common.program.visual.model

sealed interface Action {
    data object AddFunctionDefinition : Action
    data object AddVariableDefinition : Action
    data object AddReturnStatement : Action
    data object AddParameter : Action
    data object RemoveParameter : Action

    data class AddStatement(val statement: VisualSymbol.Statement) : Action
    data class SetExpression(val expression: VisualSymbol.Expression) : Action

    sealed interface SetName : Action {
        val name: VisualSymbol.Identifier

        data class FunctionDefinition(override val name: VisualSymbol.Identifier) : SetName
        data class VariableDefinition(override val name: VisualSymbol.Identifier) : SetName
        data class Parameter(override val name: VisualSymbol.Identifier) : SetName
    }

    data object Remove : Action
}
