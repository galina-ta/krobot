package com.gala.krobot.engine.program.visual.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.gala.krobot.engine.program.visual.entity.VisualProgramLine
import com.gala.krobot.engine.program.visual.entity.VisualSymbol

@Composable
fun Comment(
    line: VisualProgramLine,
    modifier: Modifier = Modifier,
) {
    val comment = remember(line) { line.toComment() }
    if (comment != null) {
        Text(
            modifier = modifier,
            text = "// $comment",
            color = Color(0xFF616161),
        )
    }
}

private fun VisualProgramLine.toComment(): String? =
    when {
        isFunctionDefinition ->
            "объявление функции ${firstIdentifier?.name} и определение её как:"

        isVariableDefinition ->
            "объявить переменную ${firstIdentifier?.name} и присвоить ей значение " +
                    formatExpression(symbols.allAfter<VisualSymbol.Assign>())

        isReturnStatement ->
            "возврат значения " +
                    formatExpression(symbols.allAfter<VisualSymbol.Statement.Return>())

        isFunctionCall -> "вызов функции " + formatExpression(symbols)

        else -> null
    }

private fun formatExpression(
    symbols: List<VisualSymbol>,
): String = symbols.joinToString(" ") { symbol ->
    when (symbol) {
        VisualSymbol.Assign -> ", равным"
        VisualSymbol.Bracket.Round.Open -> "c параметром"
        VisualSymbol.Get -> "получить"
        is VisualSymbol.Literal -> "числу ${symbol.value}"
        is VisualSymbol.ParameterUsage -> symbol.name.name
        is VisualSymbol.VariableUsage -> symbol.name.name
        is VisualSymbol.Statement.FunctionCall.User -> symbol.name.name
        is VisualSymbol.Identifier -> symbol.name
        VisualSymbol.Statement.FunctionCall.Move.Down -> "движение вниз"
        VisualSymbol.Statement.FunctionCall.Move.Left -> "движение влево"
        VisualSymbol.Statement.FunctionCall.Move.Right -> "движение вправо"
        VisualSymbol.Statement.FunctionCall.Move.Up -> "движение вверх"
        is VisualSymbol.Statement.FunctionCall.SetLevel -> "установки уровня ${symbol.name}"
        VisualSymbol.Statement.FunctionCall.Use -> "применить"

        VisualSymbol.Expression.Empty,
        VisualSymbol.Bracket.Round.Close,
        VisualSymbol.Space -> ""

        VisualSymbol.Statement.VariableDefinitionMarker,
        VisualSymbol.Statement.Return,
        is VisualSymbol.Bracket.Curly,
        VisualSymbol.Remove,
        VisualSymbol.FunctionDefinitionMarker ->
            throw IllegalArgumentException("$symbol can not be in an expression")
    }
}.replace(" ,", ",").replace("  ", " ")

private inline fun <reified T : VisualSymbol> List<VisualSymbol>.allAfter(): List<VisualSymbol> =
    dropWhile { it !is T }.drop(1)
