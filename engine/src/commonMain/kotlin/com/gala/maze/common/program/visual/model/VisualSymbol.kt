package com.gala.maze.common.program.visual.model

sealed interface VisualSymbol {
    sealed interface Expression : VisualSymbol {
        data object Empty : Expression

        companion object {
            private fun allBuiltIn(): List<Expression> = listOf(Get) + Literal.all()

            fun all(
                parameterNames: List<Identifier>,
                variableDefinitionNames: List<Identifier>,
                functionDefinitions: List<VisualFunctionDefinition>,
            ): List<Expression> =
                allBuiltIn() +
                        variableDefinitionNames.map { VariableUsage(it) } +
                        parameterNames.map { ParameterUsage(it) } +
                        functionDefinitions
                            .filter { it.hasReturnValue }
                            .map { Statement.FunctionCall.User(it.name) }
        }
    }

    data object FunctionDefinitionMarker : VisualSymbol

    sealed interface Statement : VisualSymbol {

        sealed interface FunctionCall : Statement {
            sealed interface Move : FunctionCall {
                data object Left : Move
                data object Right : Move
                data object Up : Move
                data object Down : Move

                companion object Companion {
                    fun all() = listOf(Left, Right, Up, Down)
                }
            }

            data object SetLevel : FunctionCall
            data object Use : FunctionCall

            data class User(val name: Identifier) : FunctionCall, Expression

            companion object {
                fun allExceptRun(definitions: List<VisualFunctionDefinition>): List<FunctionCall> =
                    allStatic() + allNonRunFunctions(definitions)

                private fun allStatic(): List<FunctionCall> = listOf(
                    *Move.all().toTypedArray(),
                    Use,
                    SetLevel,
                )

                private fun allNonRunFunctions(
                    definitions: List<VisualFunctionDefinition>,
                ): List<FunctionCall> {
                    val defs = definitions
                        .filter { it.name != Identifier.Run }
                        .map { User(name = it.name) }
                    return defs
                }
            }
        }

        data object Return : Statement

        data object VariableDefinitionMarker : Statement
    }

    data object Get : Expression

    data class VariableUsage(val name: Identifier) : Expression
    data class ParameterUsage(val name: Identifier) : Expression

    sealed class Literal(val value: Int) : Expression {
        data object L0 : Literal(value = 0)
        data object L1 : Literal(value = 1)
        data object L2 : Literal(value = 2)
        data object L3 : Literal(value = 3)
        data object L4 : Literal(value = 4)
        data object L5 : Literal(value = 5)
        data object L6 : Literal(value = 6)
        data object L7 : Literal(value = 7)
        data object L8 : Literal(value = 8)
        data object L9 : Literal(value = 9)

        companion object {
            fun all(): List<Literal> = listOf(L0, L1, L2, L3, L4, L5, L6, L7, L8, L9)
        }
    }

    sealed class Identifier(val name: String) : VisualSymbol {
        data object Run : Identifier(name = "выполнить")
        data object Code : Identifier(name = "код")
        data object Key : Identifier(name = "ключ")
        data object StepCount : Identifier(name = "раз")
        data object Undefined : Identifier(name = "")

        sealed class User(name: String) : Identifier(name) {
            data object A : User(name = "a")
            data object B : User(name = "b")
            data object C : User(name = "c")
            data object E : User(name = "e")
            data object I : User(name = "i")
            data object J : User(name = "j")
            data object M : User(name = "m")
            data object N : User(name = "n")
            data object X : User(name = "x")
            data object Y : User(name = "y")
            data object S : User(name = "s")
            data object H : User(name = "h")

            companion object Companion {
                fun all() = listOf(A, B, C, E, I, J, M, N, X, Y, S, H)
            }
        }

        companion object Companion {

            fun allDefined(): List<Identifier> = listOf(
                Run,
                *User.all().toTypedArray(),
            )
        }
    }

    sealed interface Bracket : VisualSymbol {
        sealed interface Curly : Bracket {
            data object Open : Curly
            data object Close : Curly
        }

        sealed interface Round : Bracket {
            data object Open : Round
            data object Close : Round
        }
    }

    data object Assign : VisualSymbol

    data object Space : VisualSymbol

    data object Remove : VisualSymbol
}
