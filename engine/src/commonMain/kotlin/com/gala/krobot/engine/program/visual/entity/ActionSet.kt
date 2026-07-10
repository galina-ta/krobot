package com.gala.krobot.engine.program.visual.entity

data class ActionSet(
    val type: Type,
    val actions: List<Action>
) {
    sealed interface Type {
        object General : Type
        object SetFunctionDefinitionName : Type
        object SetVariableDefinitionName : Type
        data class SetParameterName(val index: Int) : Type
        data class UseExpression(val index: Int) : Type
        object AddStatement : Type
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
                    if (selectedLine.hasOpenedRoundBracket)
                        parameterDefinitionIdentifiers
                    else
                        null,
                    functionIdentifiers,
                    statements(program),
                    general(
                        canDefineVariable = true,
                        canDefineParameter = true,
                        hasParameter = selectedLine.hasOpenedRoundBracket,
                        canReturn = true,
                    ),
                )

                selectedLine.isVariableDefinition -> listOfNotNull(
                    variableDefinitionIdentifiers,
                    expressions(
                        parameterNames = listOfNotNull(selectedFunction.parameterName),
                        variableDefinitionNames = selectedFunction.variableDefinitionNames,
                        functionDefinitions = program.functionDefinitions,
                    ),
                    statements(program),
                    general(canDefineVariable = true, canReturn = true),
                )

                selectedLine.isFunctionCall -> {
                    val parametersCount =
                        selectedLine.functionParametersCount(program.functionDefinitions)
                    listOfNotNull(
                        *if (selectedLine.hasOpenedRoundBracket)
                            arrayOf(
                                *(0..<parametersCount).map { index ->
                                    callParameterName(
                                        index,
                                        selectedLine.functionParameterNames(program.functionDefinitions)
                                    )
                                }.toTypedArray(),
                                *(0..<parametersCount).map { index ->
                                    expressions(
                                        parameterNames = listOfNotNull(selectedFunction.parameterName),
                                        variableDefinitionNames = selectedFunction.variableDefinitionNames,
                                        functionDefinitions = program.functionDefinitions,
                                        index = index,
                                    )
                                }.toTypedArray()
                            )
                        else
                            emptyArray(),
                        statements(program),
                        general(
                            canDefineVariable = true,
                            parametersCount = parametersCount,
                            hasParameter = selectedLine.hasOpenedRoundBracket,
                            canReturn = true,
                        ),
                    )
                }

                selectedLine.isReturnStatement -> listOfNotNull(
                    expressions(
                        parameterNames = listOfNotNull(selectedFunction.parameterName),
                        variableDefinitionNames = selectedFunction.variableDefinitionNames,
                        functionDefinitions = program.functionDefinitions,
                    ),
                    statements(program),
                    general(
                        canDefineVariable = true,
                        canReturn = true,
                    ),
                )

                selectedLine.isCondition -> listOfNotNull(
                    expressions(
                        parameterNames = listOfNotNull(selectedFunction.parameterName),
                        variableDefinitionNames = selectedFunction.variableDefinitionNames,
                        functionDefinitions = program.functionDefinitions,
                    ),
                    statements(program),
                    general(
                        canDefineVariable = true,
                        canReturn = true,
                    ),
                )

                selectedLine.isBlockEnd -> listOfNotNull(
                    statements(program),
                    general(
                        canDefineVariable = true,
                        canDefineParameter = true,
                        hasParameter = selectedLine.hasOpenedRoundBracket,
                        canReturn = true,
                    ),
                )

                else -> throw IllegalStateException("incorrect selectedLine, selectedLine=$selectedLine")
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
            // Multiple definition parameters are unsupported for now
            type = Type.SetParameterName(index = 0),
            actions = VisualSymbol.Identifier.User.all().map { identifier ->
                Action.SetName.Parameter(identifier, index = 0)
            }
        )

        private fun statements(program: VisualProgram) = ActionSet(
            type = Type.AddStatement,
            actions = VisualSymbol.FunctionCall.allStatements(
                definitions = program.functionDefinitions,
                levelName = program.levelName,
            ).map { call ->
                Action.AddStatement(statement = call)
            } + Action.AddCondition
        )

        private fun expressions(
            parameterNames: List<VisualSymbol.Identifier>,
            variableDefinitionNames: List<VisualSymbol.Identifier>,
            functionDefinitions: List<VisualFunctionDefinition>,
            index: Int = 0,
        ) = ActionSet(
            type = Type.UseExpression(index),
            actions = VisualSymbol.Expression
                .all(parameterNames, variableDefinitionNames, functionDefinitions)
                .map {
                    Action.SetExpression(expression = it, index)
                }
        )

        private fun callParameterName(
            index: Int,
            parameterNames: List<VisualSymbol.Identifier>
        ) = ActionSet(
            type = Type.SetParameterName(index),
            actions = parameterNames.map { identifier ->
                Action.SetName.Parameter(identifier, index)
            }
        )

        private fun general(
            canRemove: Boolean = true,
            canDefineVariable: Boolean = false,
            canDefineParameter: Boolean = false,
            parametersCount: Int = 0,
            hasParameter: Boolean = false,
            canReturn: Boolean = false,
        ): ActionSet =
            ActionSet(
                type = Type.General,
                actions = listOfNotNull(
                    Action.AddFunctionDefinition,
                    Action.AddVariableDefinition.takeIf { canDefineVariable },
                    Action.AddParameterDefinition.takeIf { canDefineParameter && !hasParameter },
                    if (parametersCount != 0 && !hasParameter)
                        Action.AddParameterUsage(parametersCount)
                    else
                        null,
                    Action.RemoveParameter
                        .takeIf { (parametersCount != 0 || canDefineParameter) && hasParameter },
                    Action.AddReturnStatement.takeIf { canReturn },
                    Action.Remove.takeIf { canRemove },
                )
            )
    }
}
