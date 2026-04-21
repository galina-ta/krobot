package com.gala.krobot.engine.program

import com.gala.krobot.engine.level.RobotController
import com.gala.krobot.engine.level.entity.Collectable
import com.gala.krobot.engine.level.entity.Key
import com.gala.krobot.engine.level.entity.Level
import com.gala.krobot.engine.level.entity.Number
import com.gala.krobot.engine.levels.allLevels
import com.gala.krobot.engine.program.entity.Token
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
        return executeStatements(definition.statements, parameters, variables)
    }

    private suspend fun executeStatements(
        statements: List<Token.Statement>,
        parameters: Map<String, Value>,
        variables: FunctionMemory,
    ): Value? {
        statements.forEach { statement ->
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

                is Token.Statement.Condition -> {
                    val predicateValue = returningValue(statement.predicate, parameters, variables)
                    require(predicateValue is Value.Logical) {
                        "predicate value must be logical"
                    }
                    if (predicateValue == Value.Logical.True) {
                        val returnedValue = executeStatements(
                            statements = statement.statements,
                            parameters,
                            variables,
                        )
                        if (returnedValue != null) {
                            return returnedValue
                        }
                    }
                }
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
                    Value.Collect -> useKey(key = collect() as Key)
                    is Value.Collected -> useKey(key = value.collectable as Key)
                    is Value.Number -> showCode(code = value.value)
                    is Value.Logical -> error("Can not use logical value")
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
            Token.Get -> Value.Collected(collect())
            is Token.ParameterUsage -> parameters.getValue(expression.name)
            is Token.VariableUsage -> variables.get(expression.name)
            is Token.Statement.FunctionCall.DefinedFunction -> {
                val value = executeDefinedFunction(expression, parameters, variables)
                requireNotNull(value) { "function ${expression.name} is not returning value" }
            }

            is Token.Equal -> {
                val what = numberValue(returningValue(expression.what, parameters, variables))
                val to = numberValue(returningValue(expression.to, parameters, variables))
                if (what == to) Value.Logical.True else Value.Logical.False
            }

            is Token.Literal -> Value.Number(value = expression.value)

            Token.Expression.Empty -> throw IllegalArgumentException("expression must be set")
        }

    private suspend fun numberValue(value: Value): Value.Number = when (value) {
        Value.Collect -> Value.Number((collect() as Number).value)
        is Value.Collected -> Value.Number((value.collectable as Number).value)
        is Value.Number -> value
        is Value.Logical -> Value.Number(
            when (value) {
                Value.Logical.True -> 1
                Value.Logical.False -> 0
            }
        )
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
        data class Collected(val collectable: Collectable) : Value
        data class Number(val value: Int) : Value
        data object Collect : Value
        sealed interface Logical : Value {
            data object True : Logical
            data object False : Logical
        }
    }
}
