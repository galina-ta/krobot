package com.gala.krobot.engine.program.models

sealed interface Token {
    sealed interface Expression {
        object Empty : Expression
    }

    sealed interface Statement : Token {

        sealed interface FunctionCall : Statement {

            data class DefinedFunction(
                val name: String,
                val parameter: Expression?,
            ) : FunctionCall, Expression

            data class SetLevel(val name: String) : FunctionCall

            data class Use(val what: Expression?) : FunctionCall

            sealed interface Move : FunctionCall {
                val stepCount: Expression?

                data class Left(override val stepCount: Expression?) : Move
                data class Right(override val stepCount: Expression?) : Move
                data class Up(override val stepCount: Expression?) : Move
                data class Down(override val stepCount: Expression?) : Move
            }
        }

        data class VariableDefinition(
            val name: String,
            val value: Expression,
        ) : Statement

        data class Return(val what: Expression) : Statement
    }

    data class VariableUsage(val name: String) : Expression

    data class ParameterUsage(val name: String) : Expression

    data class Literal(val value: Int) : Expression

    data object Get : Expression

    data class FunctionDefinition(
        val name: String,
        val parameterName: String?,
        val statements: List<Statement>,
        val isMain: Boolean,
    ) : Token
}
