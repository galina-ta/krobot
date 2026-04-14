package com.gala.krobot.engine.program.visual.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface VisualSymbol {
    @Serializable
    sealed interface Expression : VisualSymbol {

        @[Serializable SerialName("emptyExpression")]
        data object Empty : Expression

        companion object {
            private fun allBuiltIn(): List<Expression> =
                listOf(Get, Statement.FunctionCall.Equal) + Literal.all()

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

    @[Serializable SerialName("functionDefinitionMarker")]
    data object FunctionDefinitionMarker : VisualSymbol

    @[Serializable SerialName("if")]
    data object ConditionMarker : VisualSymbol

    @Serializable
    sealed interface Statement : VisualSymbol {

        @Serializable
        sealed interface FunctionCall : Statement {

            @Serializable
            sealed interface Move : FunctionCall {

                @[Serializable SerialName("left")]
                data object Left : Move

                @[Serializable SerialName("right")]
                data object Right : Move

                @[Serializable SerialName("up")]
                data object Up : Move

                @[Serializable SerialName("down")]
                data object Down : Move

                companion object Companion {
                    fun all(): List<Move> = listOf(Left, Right, Up, Down)
                }
            }

            @[Serializable SerialName("setLevel")]
            data class SetLevel(val name: String) : FunctionCall

            @[Serializable SerialName("use")]
            data object Use : FunctionCall

            @[Serializable SerialName("equal")]
            data object Equal : FunctionCall, Expression

            @[Serializable SerialName("user")]
            data class User(val name: Identifier) : FunctionCall, Expression

            companion object {
                fun allExceptRun(
                    definitions: List<VisualFunctionDefinition>,
                    levelName: String,
                ): List<FunctionCall> =
                    allStatic(levelName) + allNonRunFunctions(definitions)

                private fun allStatic(levelName: String): List<FunctionCall> = listOf(
                    *Move.all().toTypedArray(),
                    Use,
                    SetLevel(levelName),
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

        @[Serializable SerialName("return")]
        data object Return : Statement

        @[Serializable SerialName("variableDefinitionMarker")]
        data object VariableDefinitionMarker : Statement
    }

    @[Serializable SerialName("get")]
    data object Get : Expression

    @[Serializable SerialName("variableUsage")]
    data class VariableUsage(val name: Identifier) : Expression

    @[Serializable SerialName("parameterUsage")]
    data class ParameterUsage(val name: Identifier) : Expression

    @Serializable
    sealed class Literal(val value: Int) : Expression {
        @[Serializable SerialName("0")]
        data object L0 : Literal(value = 0)

        @[Serializable SerialName("1")]
        data object L1 : Literal(value = 1)

        @[Serializable SerialName("2")]
        data object L2 : Literal(value = 2)

        @[Serializable SerialName("3")]
        data object L3 : Literal(value = 3)

        @[Serializable SerialName("4")]
        data object L4 : Literal(value = 4)

        @[Serializable SerialName("5")]
        data object L5 : Literal(value = 5)

        @[Serializable SerialName("6")]
        data object L6 : Literal(value = 6)

        @[Serializable SerialName("7")]
        data object L7 : Literal(value = 7)

        @[Serializable SerialName("8")]
        data object L8 : Literal(value = 8)

        @[Serializable SerialName("9")]
        data object L9 : Literal(value = 9)

        companion object {
            fun all(): List<Literal> = listOf(L0, L1, L2, L3, L4, L5, L6, L7, L8, L9)
        }
    }

    @Serializable
    sealed interface Identifier : VisualSymbol {
        val name: String

        @[Serializable SerialName("run")]
        data object Run : Identifier {
            override val name = "выполнить"
        }

        @[Serializable SerialName("code")]
        data object Code : Identifier {
            override val name = "код"
        }

        @[Serializable SerialName("key")]
        data object Key : Identifier {
            override val name = "ключ"
        }

        @[Serializable SerialName("stepCount")]
        data object StepCount : Identifier {
            override val name = "раз"
        }

        @[Serializable SerialName("what")]
        data object What : Identifier {
            override val name = "что"
        }

        @[Serializable SerialName("to")]
        data object To : Identifier {
            override val name = "чему"
        }

        @[Serializable SerialName("undefined")]
        data object Undefined : Identifier {
            override val name = ""
        }

        @Serializable
        sealed interface User : Identifier {
            @[Serializable SerialName("a")]
            data object A : User {
                override val name = "a"
            }

            @[Serializable SerialName("b")]
            data object B : User {
                override val name = "b"
            }

            @[Serializable SerialName("c")]
            data object C : User {
                override val name = "c"
            }

            @[Serializable SerialName("e")]
            data object E : User {
                override val name = "e"
            }

            @[Serializable SerialName("i")]
            data object I : User {
                override val name = "i"
            }

            @[Serializable SerialName("j")]
            data object J : User {
                override val name = "j"
            }

            @[Serializable SerialName("m")]
            data object M : User {
                override val name = "m"
            }

            @[Serializable SerialName("n")]
            data object N : User {
                override val name = "n"
            }

            @[Serializable SerialName("x")]
            data object X : User {
                override val name = "x"
            }

            @[Serializable SerialName("y")]
            data object Y : User {
                override val name = "y"
            }

            @[Serializable SerialName("s")]
            data object S : User {
                override val name = "s"
            }

            @[Serializable SerialName("h")]
            data object H : User {
                override val name = "h"
            }

            companion object Companion {
                fun all(): List<User> = listOf(A, B, C, E, I, J, M, N, X, Y, S, H)
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
            @[Serializable SerialName("openCurlyBracket")]
            data object Open : Curly

            @[Serializable SerialName("closeCurlyBracket")]
            data object Close : Curly
        }

        sealed interface Round : Bracket {
            @[Serializable SerialName("openRoundBracket")]
            data object Open : Round

            @[Serializable SerialName("closeRoundBracket")]
            data object Close : Round
        }
    }

    @[Serializable SerialName("assign")]
    data object Assign : VisualSymbol

    @[Serializable SerialName("space")]
    data object Space : VisualSymbol

    @[Serializable SerialName("remove")]
    data object Remove : VisualSymbol
}

inline fun <reified T : VisualSymbol> List<VisualSymbol>.parameterSymbol(): T? =
    parameterSymbols().filterIsInstance<T>().firstOrNull()

fun List<VisualSymbol>.parameterSymbols(): List<VisualSymbol> {
    val parameterSymbols = mutableListOf<VisualSymbol>()
    var parametersStarted = false
    for (symbol in this) {
        if (symbol is VisualSymbol.Bracket.Round.Close) break
        if (parametersStarted) parameterSymbols.add(symbol)
        if (symbol is VisualSymbol.Bracket.Round.Open) parametersStarted = true
    }
    return parameterSymbols
}
