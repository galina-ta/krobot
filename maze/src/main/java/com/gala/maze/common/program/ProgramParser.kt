package com.gala.maze.common.program

import com.gala.maze.common.program.models.Command
import javax.inject.Inject

class ProgramParser @Inject constructor() {

    fun parse(text: String): List<Command> {
        val tokens = parseTokens(text)
        val wordsStack = WordsStack(initial = tokens)
        val commands = parseTopLevel(wordsStack)
        return commands
    }

    private fun parseTopLevel(wordsStack: WordsStack): List<Command.Definition> {
        val commands = mutableListOf<Command.Definition>()
        while (!wordsStack.isEmpty()) {
            val word = wordsStack.pop()
            when (word) {
                FUN_KEYWORD -> {
                    val definition = parseFunctionDefinition(wordsStack)
                    commands.add(definition)
                }
            }
        }
        return commands
    }

    private fun parseFunctionDefinition(wordsStack: WordsStack): Command.Definition.Function {
        val name = wordsStack.pop()
        while (!wordsStack.isEmpty()) {
            val word = wordsStack.pop()
            when (word) {
                OPEN_BRACKET -> Unit
                CLOSE_BRACKET -> Unit
                OPEN_BRACE -> {
                    return Command.Definition.Function(
                        name = name,
                        subcommands = parseFunctionBody(wordsStack)
                    )
                }
            }
        }
        error("function is not ended")
    }

    private fun parseFunctionBody(wordsStack: WordsStack): List<Command> {
        val subcommands = mutableListOf<Command>()
        while (!wordsStack.isEmpty()) {
            if (wordsStack.first() == FUN_KEYWORD) return subcommands
            when (val word = wordsStack.pop()) {
                OPEN_BRACE,
                OPEN_BRACKET,
                CLOSE_BRACKET -> error("wrong token $word")

                CLOSE_BRACE -> return subcommands

                else -> subcommands.add(parseFunctionUsage(name = word, wordsStack = wordsStack))
            }
        }
        return subcommands // In case if the final closing brace is missing
    }

    private fun parseFunctionUsage(name: String, wordsStack: WordsStack): Command.Usage.Function {
        while (!wordsStack.isEmpty()) {
            val word = wordsStack.pop()
            when (word) {
                OPEN_BRACKET -> Unit

                else -> return when (name) {
                    MOVE_LEFT_NAME -> Command.Usage.Function.Move.Left(1)
                    MOVE_RIGHT_NAME -> Command.Usage.Function.Move.Right(1)
                    MOVE_UP_NAME -> Command.Usage.Function.Move.Up(1)
                    MOVE_DOWN_NAME -> Command.Usage.Function.Move.Down(1)

                    SET_DEMO_ARENA ->
                        Command.Usage.Function.SetArena.SetDemoArena
                    SET_ARENA_1 ->
                        Command.Usage.Function.SetArena.SetArena1
                    SET_HOMEWORK_1_VARIANT_1_ARENA ->
                        Command.Usage.Function.SetArena.SetHomework1Variant1Arena
                    SET_HOMEWORK_1_VARIANT_2_ARENA ->
                        Command.Usage.Function.SetArena.SetHomework1Variant2Arena
                    SET_HOMEWORK_1_VARIANT_3_ARENA ->
                        Command.Usage.Function.SetArena.SetHomework1Variant3Arena

                    else -> error("")
                }
            }
        }
        error("function usage is not ended")
    }

    private fun parseTokens(text: String): List<String> {
        return text
            .split(' ', '\n')
            .filter { it.isNotBlank() }
            .flatMap { word ->
                val newTokens = mutableListOf<String>()
                var stringBuilder = StringBuilder()
                word.forEach { char ->
                    if (char.isLetterOrDigit()) {
                        stringBuilder.append(char)
                    } else {
                        if (stringBuilder.isNotEmpty()) {
                            newTokens.add(stringBuilder.toString())
                            stringBuilder = StringBuilder()
                        }
                        newTokens.add(char.toString())
                    }
                }
                if (stringBuilder.isNotEmpty()) {
                    newTokens.add(stringBuilder.toString())
                    stringBuilder = StringBuilder()
                }
                newTokens
            }
    }

    private class WordsStack(initial: List<String>) {
        private val list: MutableList<String> = initial.toMutableList()

        fun isEmpty(): Boolean = list.isEmpty()
        fun pop(): String = requireNotNull(list.removeFirstOrNull())
        fun first(): String = requireNotNull(list.firstOrNull())
    }

    private companion object {
        private const val FUN_KEYWORD = "fun"
        private const val OPEN_BRACKET = "("
        private const val CLOSE_BRACKET = ")"
        private const val OPEN_BRACE = "{"
        private const val CLOSE_BRACE = "}"

        private const val MOVE_LEFT_NAME = "moveLeft"
        private const val MOVE_RIGHT_NAME = "moveRight"
        private const val MOVE_UP_NAME = "moveUp"
        private const val MOVE_DOWN_NAME = "moveDown"

        private const val SET_DEMO_ARENA = "setDemoArena"
        private const val SET_ARENA_1 = "setArena1"
        private const val SET_HOMEWORK_1_VARIANT_1_ARENA = "setHomework1Variant1Arena"
        private const val SET_HOMEWORK_1_VARIANT_2_ARENA = "setHomework1Variant2Arena"
        private const val SET_HOMEWORK_1_VARIANT_3_ARENA = "setHomework1Variant3Arena"
    }
}
