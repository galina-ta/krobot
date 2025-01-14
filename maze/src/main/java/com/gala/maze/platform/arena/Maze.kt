package com.gala.maze.platform.arena

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gala.maze.R
import com.gala.maze.common.arena.ArenaViewModel
import com.gala.maze.common.arena.entity.RobotState
import com.gala.maze.common.arena.entity.Size
import com.gala.maze.common.arena.entity.arena.Arena
import com.gala.maze.common.arena.entity.arena.Asset
import com.gala.maze.common.arena.entity.arena.Block
import com.gala.maze.common.arena.entity.rp

@Composable
fun Maze() {
    val viewModel = viewModel<ArenaViewModel>()
    val state = viewModel.state
    var rootSize: Size.Real by remember { mutableStateOf(Size.Real(0.rp, 0.rp)) }

    val pointSize: Float = remember(rootSize, state.arena) {
        viewModel.state.arena?.calculatePointSize(rootSize) ?: 0f
    }

    val density = LocalDensity.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .onGloballyPositioned { coordinates ->
                rootSize = Size.Real(
                    width = (coordinates.size.width / density.density).toInt().rp,
                    height = (coordinates.size.height / density.density).toInt().rp
                )
            },
    ) {
        if (pointSize != 0f && state.arena != null && state.robotState != null) {
            Arena(
                arena = state.arena,
                robotState = state.robotState,
                pointSize = pointSize,
            )
        }

        Button(
            modifier = Modifier.align(Alignment.BottomCenter),
            onClick = {
                viewModel.executeCopiedCodeClicked()
            },
        ) {
            Text("Выполнить скопированный код")
        }
    }
}

@Composable
private fun BoxScope.Arena(
    arena: Arena,
    robotState: RobotState,
    pointSize: Float
) {
    Box(
        modifier = Modifier
            .size(arena.size.render(pointSize))
            .align(Alignment.Center)
            .background(Color.White)
    ) {
        arena.blocks.forEach { block ->
            Block(block, pointSize)
        }
        Robot(
            state = robotState,
            pointSize = pointSize,
        )
    }
}

@Composable
private fun Robot(state: RobotState, pointSize: Float) {
    Box(
        Modifier
            .size(state.size.render(pointSize))
            .offset(
                x = state.position.x.render(pointSize),
                y = state.position.y.render(pointSize),
            )
            .alpha(if (state.finishReason == null) 1f else 0.3f),
    ) {
        ResourceImage(
            resource = R.drawable.robot,
        )
        ScaledText(
            text = state.text,
            scale = 0.7f,
            color = Color(0xFF00FF00),
            pointSize = pointSize,
        )
    }
}

@Composable
private fun Block(block: Block, pointSize: Float) {
    Box(
        modifier = Modifier
            .size(block.size.render(pointSize))
            .offset(
                x = block.position.x.render(pointSize),
                y = block.position.y.render(pointSize),
            ),
    ) {
        when (val asset = block.asset) {
            is Asset.Void -> {
                // Draw nothing
            }
            Asset.Platform -> {
                ResourceImage(
                    modifier = Modifier.border(1.dp, color = Color.Black),
                    resource = R.drawable.stone_texture,
                )
            }
            Asset.Target -> {
                ResourceImage(
                    resource = R.drawable.target,
                )
            }
            Asset.CheckKey -> {
                ResourceImage(
                    resource = R.drawable.password_texture,
                )
            }
            is Asset.Password -> {
                ResourceImage(
                    resource = R.drawable.password_texture,
                )
                ScaledText(
                    text = asset.password,
                    scale = 0.4f,
                    pointSize = pointSize,
                )
            }
            is Asset.Code -> {
                ScaledText(
                    text = asset.randomCode.toString(),
                    scale = 0.7f,
                    pointSize = pointSize,
                )
            }
            is Asset.CheckCode -> {
                ResourceImage(
                    resource = R.drawable.verify,
                )
            }
        }
    }
}

@Composable
private fun ResourceImage(modifier: Modifier = Modifier, @DrawableRes resource: Int) {
    Image(
        modifier = modifier.fillMaxSize(),
        painter = painterResource(resource),
        contentDescription = null,
        contentScale = ContentScale.FillBounds,
    )
}

@Composable
private fun BoxScope.ScaledText(
    text: String,
    color: Color = Color(0xFFD50000),
    scale: Float,
    pointSize: Float,
) {
    Text(
        modifier = Modifier.align(Alignment.Center),
        text = text,
        fontSize = (pointSize * scale).sp,
        fontWeight = FontWeight.Bold,
        color = color,
    )
}
