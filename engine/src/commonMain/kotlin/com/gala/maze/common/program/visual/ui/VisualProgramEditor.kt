package com.gala.maze.common.program.visual.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.gala.maze.common.program.visual.VisualProgramEditorViewModel
import com.gala.maze.common.program.visual.model.VisualProgram

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
                            .align(Alignment.CenterVertically),
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
    actionSets: List<VisualProgram.ActionSet>,
    levelName: String,
    actionClicked: (VisualProgram.Action) -> Unit,
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
    action: VisualProgram.Action,
    isLargeSet: Boolean,
    levelName: String,
    clicked: () -> Unit,
) {
    val symbolModifier = Modifier.clickable { clicked() }
    when (action) {
        VisualProgram.Action.AddFunction -> {
            Symbol(
                modifier = symbolModifier,
                symbol = VisualProgram.Symbol.Definition.Function,
                fillWidth = isLargeSet,
                levelName = levelName,
            )
        }
        is VisualProgram.Action.AddUsage -> {
            Symbol(
                modifier = symbolModifier,
                symbol = action.usage,
                fillWidth = isLargeSet,
                levelName = levelName,
            )
        }
        is VisualProgram.Action.SetFunctionName -> {
            Symbol(
                modifier = symbolModifier,
                symbol = action.identifier,
                fillWidth = isLargeSet,
                levelName = levelName,
            )
        }
        VisualProgram.Action.Remove -> {
            Symbol(
                modifier = symbolModifier,
                symbol = VisualProgram.Symbol.Remove,
                fillWidth = isLargeSet,
                levelName = levelName,
            )
        }
    }
}

@Composable
private fun RowScope.Symbol(
    symbol: VisualProgram.Symbol,
    levelName: String,
    fillWidth: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val isDefinition = symbol is VisualProgram.Symbol.Definition
    Text(
        modifier = modifier
            .height(defaultSymbolSide)
            .then(if (fillWidth) Modifier else Modifier)
            .then(
                when (symbol) {
                    is VisualProgram.Symbol.Identifier.Run,
                    is VisualProgram.Symbol.Usage.SetLevel ->
                        Modifier.padding(horizontal = 3.dp)
                    else ->
                        if (fillWidth)
                            Modifier.weight(1f)
                        else
                            Modifier.width(defaultSymbolSide)
                }
            )
            .then(
                when (symbol) {
                    is VisualProgram.Symbol.Usage.Move.Down -> Modifier.rotate(90f)
                    is VisualProgram.Symbol.Usage.Move.Left -> Modifier.rotate(180f)
                    is VisualProgram.Symbol.Usage.Move.Right -> Modifier.rotate(0f)
                    is VisualProgram.Symbol.Usage.Move.Up -> Modifier.rotate(270f)
                    else -> Modifier
                }
            ),
        text = when (symbol) {
            is VisualProgram.Symbol.Definition.Function -> "f"
            is VisualProgram.Symbol.Usage.Move -> ">"
            is VisualProgram.Symbol.Usage.SetLevel -> "уровень $levelName"
            is VisualProgram.Symbol.Brace.Curly.Close -> "}"
            is VisualProgram.Symbol.Brace.Curly.Open -> "{"
            is VisualProgram.Symbol.Identifier -> symbol.name
            VisualProgram.Symbol.Space -> ""
            VisualProgram.Symbol.Remove -> "rm"
            is VisualProgram.Symbol.Usage.Function -> symbol.identifier.name
        },
        fontWeight = if (isDefinition) FontWeight.Bold else null,
        color = if (isDefinition) Color.Red else Color.Black,
        textAlign = TextAlign.Center,
    )
}

private val defaultSymbolSide = 30.dp
