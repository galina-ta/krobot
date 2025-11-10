package com.gala.krobot.engine.program.visual.entity

data class ActionSet(
    val type: Type,
    val actions: List<Action>
) {
    enum class Type {
        General,
        SetFunctionDefinitionName,
        SetVariableDefinitionName,
        SetParameterName,
        UseExpression,
        AddStatement,
    }

    companion object {

        fun available(program: VisualProgram): List<ActionSet> {
            val selectedFunction = program.selectedFunctionDefinition()
            val selectedLine = selectedFunction?.selectedLine()
            return when {
                selectedLine == null -> listOf(
                    general(canRemove = false),
                )

                selectedLine.isFunctionDefinition -> listOfNotNull(
                    general(
                        canDefineVariable = true,
                        canHaveParameter = true,
                        hasParameter = selectedLine.hasOpenedRoundBracket,
                        canReturn = true,
                    ),
                    functionIdentifiers,
                    statements(program),
                    if (selectedLine.hasOpenedRoundBracket)
                        parameterDefinitionIdentifiers
                    else
                        null,
                )

                selectedLine.isVariableDefinition -> listOfNotNull(
                    general(canDefineVariable = true, canReturn = true),
                    variableDefinitionIdentifiers,
                    statements(program),
                    expressions(
                        parameterNames = listOfNotNull(selectedFunction.parameterName),
                        variableDefinitionNames = selectedFunction.variableDefinitionNames,
                        functionDefinitions = program.functionDefinitions,
                    ),
                )

                selectedLine.isFunctionCall -> listOfNotNull(
                    general(
                        canDefineVariable = true,
                        canHaveParameter = true,
                        hasParameter = selectedLine.hasOpenedRoundBracket,
                        canReturn = true,
                    ),
                    statements(program),
                    *if (selectedLine.hasOpenedRoundBracket)
                        arrayOf(
                            callParameterName(
                                selectedLine.functionParameterNames(program.functionDefinitions)
                            ),
                            expressions(
                                parameterNames = listOfNotNull(selectedFunction.parameterName),
                                variableDefinitionNames = selectedFunction.variableDefinitionNames,
                                functionDefinitions = program.functionDefinitions,
                            )
                        )
                    else
                        emptyArray(),
                )

                selectedLine.isReturnStatement -> listOfNotNull(
                    general(
                        canDefineVariable = true,
                        canReturn = true,
                    ),
                    statements(program),
                    expressions(
                        parameterNames = listOfNotNull(selectedFunction.parameterName),
                        variableDefinitionNames = selectedFunction.variableDefinitionNames,
                        functionDefinitions = program.functionDefinitions,
                    ),
                )

                else -> throw IllegalStateException("incorrect selectedLine")
            }
        }

        private val functionIdentifiers = ActionSet(
            type = Type.SetFunctionDefinitionName,
            actions = VisualSymbol.Identifier.allDefined().map { identifier ->
                Action.SetName.FunctionDefinition(identifier)
            }
        )

        private val variableDefinitionIdentifiers = ActionSet(
            type = Type.SetVariableDefinitionName,
            actions = VisualSymbol.Identifier.User.all().map { identifier ->
                Action.SetName.VariableDefinition(identifier)
            }
        )

        private val parameterDefinitionIdentifiers = ActionSet(
            type = Type.SetParameterName,
            actions = VisualSymbol.Identifier.User.all().map { identifier ->
                Action.SetName.Parameter(identifier)
            }
        )

        private fun statements(program: VisualProgram) = ActionSet(
            type = Type.AddStatement,
            actions = VisualSymbol.Statement.FunctionCall.allExceptRun(
                definitions = program.functionDefinitions,
                levelName = program.levelName,
            ).map { call ->
                Action.AddStatement(statement = call)
            }
        )

        private fun expressions(
            parameterNames: List<VisualSymbol.Identifier>,
            variableDefinitionNames: List<VisualSymbol.Identifier>,
            functionDefinitions: List<VisualFunctionDefinition>,
        ) = ActionSet(
            type = Type.UseExpression,
            actions = VisualSymbol.Expression
                .all(parameterNames, variableDefinitionNames, functionDefinitions)
                .map {
                    Action.SetExpression(expression = it)
                }
        )

        private fun callParameterName(
            parameterNames: List<VisualSymbol.Identifier>
        ) = ActionSet(
            type = Type.SetParameterName,
            actions = parameterNames.map { identifier ->
                Action.SetName.Parameter(identifier)
            }
        )

        private fun general(
            canRemove: Boolean = true,
            canDefineVariable: Boolean = false,
            canHaveParameter: Boolean = false,
            hasParameter: Boolean = false,
            canReturn: Boolean = false,
        ): ActionSet =
            ActionSet(
                type = Type.General,
                actions = listOfNotNull(
                    Action.AddFunctionDefinition,
                    Action.AddVariableDefinition.takeIf { canDefineVariable },
                    Action.AddParameter.takeIf { canHaveParameter && !hasParameter },
                    Action.RemoveParameter.takeIf { canHaveParameter && hasParameter },
                    Action.AddReturnStatement.takeIf { canReturn },
                    Action.Remove.takeIf { canRemove },
                )
            )
    }
}
