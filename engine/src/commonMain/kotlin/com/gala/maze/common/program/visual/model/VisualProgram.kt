package com.gala.maze.common.program.visual.model

import androidx.compose.runtime.Immutable
import com.gala.maze.common.program.Program
import com.gala.maze.common.program.models.Token
import com.gala.maze.common.program.models.Token.Usage.Function.Move.Down
import com.gala.maze.common.program.models.Token.Usage.Function.Move.Left
import com.gala.maze.common.program.models.Token.Usage.Function.Move.Right
import com.gala.maze.common.program.models.Token.Usage.Function.Move.Up
import com.gala.maze.common.program.models.Token.Usage.Function.SetArena

@Immutable
data class VisualProgram(
    private val functionDefinitions: List<FunctionDefinition> = emptyList(),
) {
    val flatLines = functionDefinitions.flatMap { it.lines }

    val availableActionSets: List<ActionSet> = run {
        val selectedFunction = selectedFunctionDefinition()
        val selectedLine = selectedFunction?.selectedLine()
        when {
            selectedLine == null -> listOf(
                ActionSet.topLevel(canRemove = false),
            )
            selectedLine.isFunctionDefinition -> listOf(
                ActionSet.topLevel(canRemove = true),
                ActionSet.functionIdentifiers,
                ActionSet.usages(functionDefinitions),
            )
            else -> listOf(
                ActionSet.topLevel(canRemove = true),
                ActionSet.usages(functionDefinitions),
            )
        }
    }

    fun modified(action: Action): VisualProgram = when (action) {
        Action.AddFunction -> withNewFunction()
        is Action.AddUsage -> withUsage(action.usage)
        is Action.SetFunctionName -> withFunctionName(action.identifier)
        Action.Remove -> withoutSelected()
    }

    fun withLineSelected(line: Line): VisualProgram {
        return copy(
            functionDefinitions = functionDefinitions.map { definition ->
                var selectedLineFound = false
                definition.copy(
                    lines = definition.lines.map { currentLine ->
                        if (currentLine === line) {
                            selectedLineFound = true
                            currentLine.copy(isSelected = true)
                        } else {
                            currentLine.unselected()
                        }
                    },
                    isSelected = selectedLineFound,
                )
            }
        )
    }

    private fun withNewFunction(): VisualProgram {
        val newDefinition = FunctionDefinition(
            name = Symbol.Identifier.Undefined,
            isSelected = true,
            lines = listOf(
                Line(
                    isSelectable = true,
                    isSelected = true,
                    functionDefinitionIndex = functionDefinitions.size,
                    symbols = listOf(
                        Symbol.Definition.Function,
                        Symbol.Identifier.Undefined,
                        Symbol.Brace.Curly.Open,
                    ),
                ),
                Line(
                    isSelectable = false,
                    isSelected = false,
                    functionDefinitionIndex = functionDefinitions.size,
                    symbols = listOf(
                        Symbol.Brace.Curly.Close,
                    ),
                ),
            ),
        )
        val definitions = functionDefinitions.map { it.unselected() } + newDefinition
        return copy(functionDefinitions = definitions)
    }

    private fun withUsage(usage: Symbol.Usage): VisualProgram {
        val selectedDefinition = requireNotNull(selectedFunctionDefinition())
        val selectedLine = requireNotNull(selectedDefinition.selectedLine())
        return copy(
            functionDefinitions = functionDefinitions.mapIndexed { definitionIndex, definition ->
                if (definition === selectedDefinition) {
                    definition.copy(
                        isSelected = true,
                        lines = definition.lines.flatMap { line ->
                            if (line === selectedLine) {
                                listOf(
                                    line.unselected(),
                                    usage.newLine(functionDefinitionIndex = definitionIndex),
                                )
                            } else {
                                listOf(
                                    line.unselected()
                                )
                            }
                        }
                    )
                } else {
                    definition.unselected()
                }
            }
        )
    }

    private fun withFunctionName(identifier: Symbol.Identifier): VisualProgram {
        val selectedDefinition = requireNotNull(selectedFunctionDefinition())
        return copy(
            functionDefinitions = functionDefinitions.mapIndexed { definitionIndex, definition ->
                if (definition === selectedDefinition) {
                    definition.copy(
                        lines = definition.lines.map { line ->
                            if (line.isFunctionDefinition) {
                                line.copy(
                                    symbols = line.symbols.map { symbol ->
                                        if (symbol is Symbol.Identifier)
                                            identifier
                                        else
                                            symbol
                                    }
                                )
                            } else {
                                line
                            }
                        }
                    )
                } else {
                    definition
                }
            }
        )
    }

    private fun withoutSelected(): VisualProgram {
        val selectedDefinition = requireNotNull(selectedFunctionDefinition())
        val selectedLine = requireNotNull(selectedDefinition.selectedLine())
        return when {
            selectedLine.isFunctionDefinition -> {
                var selectedSet = false
                copy(
                    functionDefinitions = functionDefinitions.mapIndexedNotNull { index, definition ->
                        if (definition === selectedDefinition)
                            null
                        else
                            if (!selectedSet) {
                                selectedSet = true
                                definition.withSelectedDefinition()
                            } else {
                                definition
                            }
                    }
                )
            }

            else -> copy(
                functionDefinitions = functionDefinitions.map { definition ->
                    if (definition === selectedDefinition) {
                        val lineIndex = definition.lines.indexOf(selectedLine)
                        definition.copy(
                            lines = definition.lines.mapIndexedNotNull { index, line ->
                                when (index) {
                                    lineIndex - 1 -> line.copy(isSelected = true)
                                    lineIndex -> null
                                    else -> line
                                }
                            }
                        )
                    } else {
                        definition
                    }
                }
            )
        }
    }

    private fun Symbol.Usage.newLine(functionDefinitionIndex: Int): Line =
        Line(
            isSelectable = true,
            functionDefinitionIndex = functionDefinitionIndex,
            symbols = listOf(Symbol.Space, this),
            isSelected = true,
        )

    private fun selectedFunctionDefinition(): FunctionDefinition? =
        functionDefinitions.find { it.isSelected }

    fun toProgram(levelName: String): Program = Program(
        tokens = functionDefinitions.map { definition ->
            val identifier = definition.lines.first()
                .symbols.filterIsInstance<Symbol.Identifier>()
                .first()
            Token.FunctionDefinition(
                name = identifier.name,
                isMain = identifier == Symbol.Identifier.Run,
                tokens = definition.lines.drop(1).flatMap { line ->
                    line.symbols.flatMap { symbol ->
                        when (symbol) {
                            is Symbol.Brace.Curly,
                            is Symbol.Definition.Function,
                            is Symbol.Identifier,
                            Symbol.Remove,
                            Symbol.Space -> emptyList()
                            Symbol.Usage.Move.Down -> listOf(Down(1))
                            Symbol.Usage.Move.Left -> listOf(Left(1))
                            Symbol.Usage.Move.Right -> listOf(Right(1))
                            Symbol.Usage.Move.Up -> listOf(Up(1))
                            Symbol.Usage.SetLevel -> listOf(SetArena(levelName))
                            is Symbol.Usage.Function -> listOf()
                        }
                    }
                }
            )
        }
    )

    data class FunctionDefinition(
        val name: Symbol.Identifier,
        val isSelected: Boolean,
        val lines: List<Line>,
    ) {
        fun selectedLine(): Line? =
            lines.find { it.isSelected }

        fun unselected(): FunctionDefinition = if (isSelected)
            copy(
                isSelected = false,
                lines = lines.map { line ->
                    line.unselected()
                }
            )
        else
            this

        fun withSelectedDefinition(): FunctionDefinition = copy(
            isSelected = true,
            lines = lines.map { line ->
                if (line.isFunctionDefinition)
                    line.copy(isSelected = true)
                else
                    line.unselected()
            }
        )
    }

    data class Line(
        val isSelectable: Boolean,
        val functionDefinitionIndex: Int,
        val symbols: List<Symbol>,
        val isSelected: Boolean,
    ) {
        val isFunctionDefinition: Boolean = symbols.any { it is Symbol.Definition.Function }

        fun unselected() = if (isSelected) copy(isSelected = false) else this
    }

    data class ActionSet(
        val actions: List<Action>
    ) {
        companion object {

            val functionIdentifiers = ActionSet(
                actions = Symbol.Identifier.allDefined().map { identifier ->
                    Action.SetFunctionName(identifier)
                }
            )

            fun usages(
                functionDefinitions: List<FunctionDefinition>,
            ) = ActionSet(
                actions = Symbol.Usage.all().map { usage ->
                    Action.AddUsage(usage = usage)
                } + functionDefinitions
                    .filter { it.name != Symbol.Identifier.Run }
                    .map {
                        Action.AddUsage(usage = Symbol.Usage.Function(identifier = it.name))
                    }
            )

            fun topLevel(canRemove: Boolean): ActionSet =
                ActionSet(
                    actions = listOfNotNull(
                        Action.AddFunction,
                        Action.Remove.takeIf { canRemove },
                    )
                )
        }
    }

    sealed interface Action {
        data object AddFunction : Action
        data class SetFunctionName(val identifier: Symbol.Identifier) : Action
        data class AddUsage(val usage: Symbol.Usage) : Action
        data object Remove : Action
    }

    sealed interface Symbol {

        sealed interface Definition : Symbol {
            data object Function : Definition
        }

        sealed interface Usage : Symbol {

            sealed interface Move : Usage {
                data object Left : Move
                data object Right : Move
                data object Up : Move
                data object Down : Move

                companion object {
                    fun all() = listOf(Left, Right, Up, Down)
                }
            }

            data object SetLevel : Usage

            data class Function(val identifier: Identifier) : Usage

            companion object {
                fun all(): List<Usage> = listOf(
                    *Move.all().toTypedArray(),
                    SetLevel,
                )
            }
        }

        sealed class Identifier(val name: String) : Symbol {

            data object Run : Identifier(RUN_NAME)

            data object Undefined : Identifier(UNDEFINED_NAME)

            sealed class User(name: String) : Identifier(name) {
                data object A : User(A_NAME)
                data object B : User(B_NAME)
                data object C : User(C_NAME)
                data object E : User(E_NAME)
                data object I : User(I_NAME)
                data object J : User(J_NAME)
                data object M : User(M_NAME)
                data object N : User(N_NAME)
                data object X : User(X_NAME)
                data object Y : User(Y_NAME)
                data object S : User(S_NAME)
                data object H : User(H_NAME)

                companion object {
                    fun all() = listOf(A, B, C, E, I, J, M, N, X, Y, S, H)
                }
            }

            companion object {
                private const val RUN_NAME = "выполнить"
                private const val UNDEFINED_NAME = ""
                private const val A_NAME = "a"
                private const val B_NAME = "b"
                private const val C_NAME = "c"
                private const val E_NAME = "e"
                private const val I_NAME = "i"
                private const val J_NAME = "j"
                private const val M_NAME = "m"
                private const val N_NAME = "n"
                private const val X_NAME = "x"
                private const val Y_NAME = "y"
                private const val S_NAME = "s"
                private const val H_NAME = "h"

                fun allDefined(): List<Identifier> = listOf(
                    Run,
                    *User.all().toTypedArray(),
                )
            }
        }

        sealed interface Brace : Symbol {
            sealed interface Curly : Brace {
                data object Open : Curly
                data object Close : Curly
            }
        }

        data object Space : Symbol

        data object Remove : Symbol
    }

    companion object {
        val empty = VisualProgram()
            .modified(Action.AddFunction)
            .modified(Action.SetFunctionName(Symbol.Identifier.Run))
    }
}
