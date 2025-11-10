package com.gala.krobot.engine.program

import com.gala.krobot.engine.level.RobotController
import com.gala.krobot.engine.level.entity.Level
import com.gala.krobot.engine.program.models.Token
import com.gala.krobot.engine.levels.allLevels
import kotlin.jvm.JvmInline

class ProgramRobotController(
    private val program: Program,
    private val dynamicLevelName: String,
    private val dynamicLevel: Level,
) : RobotController() {

    override suspend fun run() {
        val mainFunction = program.functionDefinitions.first { it.isMain }
        executeFunction(mainFunction, parameters = emptyMap())
    }

    private suspend fun executeFunction(
        definition: Token.FunctionDefinition,
        parameters: Map<String, Value>,
    ): Value? {
        val variables = FunctionMemory()
        definition.statements.forEach { statement ->
            when (statement) {
                is Token.Statement.FunctionCall ->
                    executeFunctionCall(
                        call = statement,
                        parameters = parameters,
                        variables = variables,
                    )

                is Token.Statement.VariableDefinition ->
                    variables.set(
                        statement.name,
                        returningValue(statement.value, parameters, variables)
                    )

                is Token.Statement.Return ->
                    return returningValue(statement.what, parameters, variables)
            }
        }
        return null
    }

    private suspend fun executeFunctionCall(
        call: Token.Statement.FunctionCall,
        parameters: Map<String, Value>,
        variables: FunctionMemory,
    ) {
        when (call) {
            is Token.Statement.FunctionCall.Move -> {
                val stepCount = call
                    .stepCount?.let { stepCount ->
                        returningValue(stepCount, parameters, variables)
                    } as? Value.Number
                val stepCountInt = stepCount?.value ?: 1
                when (call) {
                    is Token.Statement.FunctionCall.Move.Left -> moveLeft(stepCountInt)
                    is Token.Statement.FunctionCall.Move.Right -> moveRight(stepCountInt)
                    is Token.Statement.FunctionCall.Move.Up -> moveUp(stepCountInt)
                    is Token.Statement.FunctionCall.Move.Down -> moveDown(stepCountInt)
                }
            }

            is Token.Statement.FunctionCall.Use -> {
                val what = requireNotNull(call.what) { "use.what muse be set" }
                when (val value = returningValue(what, parameters, variables)) {
                    Value.Collect -> useKey(key = getKey())
                    is Value.Key -> useKey(key = value.key)
                    is Value.Number -> display(password = value.value.toString())
                }
            }

            is Token.Statement.FunctionCall.SetLevel -> {
                setLevel(
                    when (call.name) {
                        in allLevels -> allLevels[call.name]!!
                        dynamicLevelName -> dynamicLevel
                        else -> throw IllegalArgumentException("level ${call.name} is not registered")
                    }
                )
            }

            is Token.Statement.FunctionCall.DefinedFunction -> {
                executeDefinedFunction(call, parameters, variables)
            }
        }
    }

    private suspend fun returningValue(
        expression: Token.Expression,
        parameters: Map<String, Value>,
        variables: FunctionMemory,
    ): Value =
        when (expression) {
            Token.Get -> Value.Collect
            is Token.ParameterUsage -> parameters.getValue(expression.name)
            is Token.VariableUsage -> variables.get(expression.name)
            is Token.Statement.FunctionCall.DefinedFunction -> {
                val value = executeDefinedFunction(expression, parameters, variables)
                requireNotNull(value) { "function ${expression.name} is not returning value" }
            }

            is Token.Literal -> Value.Number(value = expression.value)

            Token.Expression.Empty -> throw IllegalArgumentException("expression must be set")
        }

    private suspend fun executeDefinedFunction(
        function: Token.Statement.FunctionCall.DefinedFunction,
        parameters: Map<String, Value>,
        variables: FunctionMemory,
    ): Value? {
        val definition = requireNotNull(
            program.functionDefinitions.find { it.name == function.name }
        ) { "function ${function.name} is not defined" }
        val parameters = function.parameter
            ?.let { parameter ->
                val parameterName = requireNotNull(definition.parameterName) {
                    "${definition.name} has no parameter"
                }
                val value = returningValue(parameter, parameters, variables)
                mapOf(parameterName to value)
            }
            ?: emptyMap()
        return executeFunction(definition, parameters)
    }

    @JvmInline
    private value class FunctionMemory(
        private val values: MutableMap<String, Value> = mutableMapOf(),
    ) {
        fun set(name: String, value: Value) {
            values[name] = value
        }

        fun get(name: String): Value {
            return requireNotNull(values[name]) { "$name is not initialized" }
        }
    }

    sealed interface Value {
        data class Key(val key: String) : Value
        data class Number(val value: Int) : Value
        data object Collect : Value
    }
}
