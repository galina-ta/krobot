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
        VisualSymbol.Assign -> ", которому присвоено значение"
        VisualSymbol.Bracket.Round.Open -> "c параметром"
        VisualSymbol.FunctionCall.Get -> "выданное функцией получить"
        VisualSymbol.FunctionCall.Equal -> "выданное функцией сравнения"
        is VisualSymbol.Literal -> "${symbol.value}"
        is VisualSymbol.ParameterUsage -> symbol.name.name
        is VisualSymbol.VariableUsage -> symbol.name.name
        is VisualSymbol.FunctionCall.User -> symbol.name.name
        is VisualSymbol.Identifier -> symbol.name
        VisualSymbol.FunctionCall.Move.Down -> "движение вниз"
        VisualSymbol.FunctionCall.Move.Left -> "движение влево"
        VisualSymbol.FunctionCall.Move.Right -> "движение вправо"
        VisualSymbol.FunctionCall.Move.Up -> "движение вверх"
        is VisualSymbol.FunctionCall.SetLevel -> "установки уровня ${symbol.name}"
        VisualSymbol.FunctionCall.Use -> "применить"
        VisualSymbol.ConditionMarker -> "если"

        VisualSymbol.Expression.Empty,
        VisualSymbol.Bracket.Round.Close,
        VisualSymbol.Space,
        is VisualSymbol.Bracket.Curly,
        VisualSymbol.Comma -> ""

        VisualSymbol.Statement.VariableDefinitionMarker,
        VisualSymbol.Statement.Return,
        VisualSymbol.Remove,
        VisualSymbol.FunctionDefinitionMarker ->
            throw IllegalArgumentException("$symbol can not be in an expression")
    }
}.replace(" ,", ",").replace("  ", " ")

private inline fun <reified T : VisualSymbol> List<VisualSymbol>.allAfter(): List<VisualSymbol> =
    dropWhile { it !is T }.drop(1)
