package com.gala.maze.common.program.visual.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.gala.maze.common.program.visual.VisualProgramEditorViewModel
import com.gala.maze.common.program.visual.model.Action
import com.gala.maze.common.program.visual.model.ActionSet
import com.gala.maze.common.program.visual.model.VisualSymbol
import krobot.engine.generated.resources.Res
import krobot.engine.generated.resources.SpaceMono_Regular
import krobot.engine.generated.resources.arrow_up
import krobot.engine.generated.resources.get
import krobot.engine.generated.resources.return_statement
import krobot.engine.generated.resources.use
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource

@Composable
fun VisualProgramEditor(
    viewModel: VisualProgramEditorViewModel,
    modifier: Modifier = Modifier,
) {
    val state = viewModel.state
    Column(modifier = modifier.background(Color.White)) {
        LazyColumn(modifier = Modifier.fillMaxWidth().weight(1f)) {
            itemsIndexed(state.program.flatLines) { index, line ->
                Row(
                    modifier = Modifier
                        .then(
                            if (line.isSelected)
                                Modifier.background(Color(0xFF82B1FF))
                            else
                                Modifier,
                        )
                        .then(
                            if (line.isSelectable)
                                Modifier.clickable {
                                    viewModel.selectLine(line)
                                }
                            else
                                Modifier
                        )
                ) {
                    Text(
                        modifier = Modifier
                            .padding(horizontal = 6.dp)
                            .width(25.dp)
                            .align(Alignment.CenterVertically),
                        color = Color(0xFF1565C0),
                        fontStyle = FontStyle.Italic,
                        text = "${index + 1}"
                    )
                    line.symbols.forEach { symbol ->
                        Symbol(
                            symbol = symbol,
                            levelName = state.levelName
                        )
                    }
                }
            }
        }
        Keyboard(
            actionSets = state.program.availableActionSets,
            levelName = state.levelName,
            actionClicked = { action ->
                viewModel.executeAction(action)
            }
        )
    }
}

@Composable
private fun Keyboard(
    actionSets: List<ActionSet>,
    levelName: String,
    actionClicked: (Action) -> Unit,
) {
    var widthDp: Dp? by remember { mutableStateOf(null) }
    val density = LocalDensity.current
    Column(modifier = Modifier.fillMaxWidth().onGloballyPositioned { coordinates ->
        widthDp = with(density) {
            coordinates.boundsInParent().size.width.toDp()
        }
    }) {
        actionSets.forEach { set ->
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    modifier = Modifier
                        .padding(horizontal = 3.dp)
                        .align(Alignment.CenterVertically),
                    text = when (set.type) {
                        ActionSet.Type.General -> "добавить"
                        ActionSet.Type.SetFunctionDefinitionName -> "имя функции"
                        ActionSet.Type.SetVariableDefinitionName -> "имя переменной"
                        ActionSet.Type.SetParameterName -> "имя параметра"
                        ActionSet.Type.UseExpression -> "использовать"
                        ActionSet.Type.AddStatement -> "вызвать"
                    },
                    color = Color.Black,
                )
                set.actions.forEach { action ->
                    Action(
                        action,
                        isLargeSet = widthDp
                            ?.let { (defaultSymbolSide * set.actions.size / 2) > it }
                            ?: false,
                        levelName = levelName,
                        clicked = {
                            actionClicked(action)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun RowScope.Action(
    action: Action,
    isLargeSet: Boolean,
    levelName: String,
    clicked: () -> Unit,
) {
    val symbolModifier = Modifier.clickable { clicked() }
    val symbol: VisualSymbol = when (action) {
        Action.AddFunctionDefinition -> VisualSymbol.FunctionDefinitionMarker
        Action.AddVariableDefinition -> VisualSymbol.Statement.VariableDefinitionMarker
        Action.AddReturnStatement -> VisualSymbol.Statement.Return

        Action.AddParameter,
        Action.RemoveParameter -> VisualSymbol.Bracket.Round.Open

        is Action.AddStatement -> action.statement
        is Action.SetName -> action.name

        is Action.SetExpression -> action.expression
        Action.Remove -> VisualSymbol.Remove
    }
    Symbol(
        modifier = symbolModifier,
        symbol = symbol,
        fillWidth = isLargeSet,
        lineThrough = action is Action.RemoveParameter,
        levelName = levelName,
    )
}

@Composable
private fun RowScope.Symbol(
    symbol: VisualSymbol,
    levelName: String,
    fillWidth: Boolean = false,
    lineThrough: Boolean = false,
    modifier: Modifier = Modifier,
) {
    @Composable
    fun TextSymbol(
        text: String,
        isDefinition: Boolean = false,
        textModifier: Modifier = Modifier,
    ) {
        val isLong = text.length > 1
        Text(
            modifier = modifier.then(textModifier)
                .height(defaultSymbolSide)
                .align(Alignment.CenterVertically)
                .wrapContentHeight(align = Alignment.CenterVertically)
                .then(
                    if (isLong)
                        Modifier.padding(horizontal = 3.dp)
                    else
                        if (fillWidth)
                            Modifier.weight(1f)
                        else
                            Modifier.width(defaultSymbolSide)
                ),
            text = text,
            fontWeight = if (isDefinition) FontWeight.Bold else null,
            color = if (isDefinition) Color.Red else Color.Black,
            textAlign = TextAlign.Center,
            style = TextStyle(
                textDecoration = if (lineThrough) TextDecoration.LineThrough else null,
                fontFamily = if (!isLong)
                    FontFamily(Font(Res.font.SpaceMono_Regular, FontWeight.Normal))
                else
                    null,
            )
        )
    }

    @Composable
    fun ImageSymbol(
        drawableResource: DrawableResource,
        imageModifier: Modifier = Modifier,
    ) {
        Image(
            modifier = modifier
                .then(imageModifier)
                .size(defaultSymbolSide)
                .padding(all = 5.dp),
            painter = painterResource(drawableResource),
            contentDescription = null,
        )
    }

    @Composable
    fun Arrow(rotateDegree: Float) {
        ImageSymbol(
            drawableResource = Res.drawable.arrow_up,
            imageModifier = Modifier.rotate(rotateDegree)
        )
    }

    when (symbol) {
        is VisualSymbol.FunctionDefinitionMarker -> TextSymbol("f", isDefinition = true)
        VisualSymbol.Statement.VariableDefinitionMarker -> TextSymbol("v", isDefinition = true)

        is VisualSymbol.Statement.FunctionCall.Move.Up -> Arrow(rotateDegree = 0f)
        is VisualSymbol.Statement.FunctionCall.Move.Right -> Arrow(rotateDegree = 90f)
        is VisualSymbol.Statement.FunctionCall.Move.Down -> Arrow(rotateDegree = 180f)
        is VisualSymbol.Statement.FunctionCall.Move.Left -> Arrow(rotateDegree = 270f)

        is VisualSymbol.Statement.FunctionCall.SetLevel -> TextSymbol("уровень $levelName")

        VisualSymbol.Bracket.Curly.Close -> TextSymbol("}")
        VisualSymbol.Bracket.Curly.Open -> TextSymbol("{")
        VisualSymbol.Bracket.Round.Close -> TextSymbol(")")
        VisualSymbol.Bracket.Round.Open -> TextSymbol("(")

        is VisualSymbol.Identifier -> TextSymbol(symbol.name)
        is VisualSymbol.Statement.FunctionCall.User -> TextSymbol(symbol.name.name)

        is VisualSymbol.VariableUsage -> TextSymbol(symbol.name.name)
        is VisualSymbol.ParameterUsage -> TextSymbol(symbol.name.name)
        is VisualSymbol.Literal -> TextSymbol(symbol.value.toString())

        VisualSymbol.Get -> ImageSymbol(Res.drawable.get)
        VisualSymbol.Statement.FunctionCall.Use -> ImageSymbol(Res.drawable.use)

        VisualSymbol.Statement.Return ->
            ImageSymbol(
                drawableResource = Res.drawable.return_statement,
                imageModifier = Modifier.rotate(90f)
            )

        VisualSymbol.Assign -> TextSymbol("=")
        VisualSymbol.Remove -> TextSymbol("удалить")

        VisualSymbol.Space,
        VisualSymbol.Expression.Empty -> TextSymbol("")
    }
}

private val defaultSymbolSide = 30.dp
