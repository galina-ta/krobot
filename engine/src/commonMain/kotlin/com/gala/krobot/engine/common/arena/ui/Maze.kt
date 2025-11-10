package com.gala.krobot.engine.common.arena.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gala.krobot.engine.common.arena.LevelViewModel
import com.gala.krobot.engine.common.arena.entity.RobotState
import com.gala.krobot.engine.common.arena.entity.Size
import com.gala.krobot.engine.common.arena.entity.arena.Asset
import com.gala.krobot.engine.common.arena.entity.arena.Block
import com.gala.krobot.engine.common.arena.entity.arena.Level
import com.gala.krobot.engine.common.arena.entity.rp
import krobot.engine.generated.resources.Res
import krobot.engine.generated.resources.password_texture
import krobot.engine.generated.resources.robot
import krobot.engine.generated.resources.target
import krobot.engine.generated.resources.verify
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun LevelScreen(viewModel: LevelViewModel) {
    val state = viewModel.state
    var rootSize: Size.Real by remember { mutableStateOf(Size.Real(0.rp, 0.rp)) }

    val pointSize: Float = remember(rootSize, state.level) {
        viewModel.state.level?.calculatePointSize(rootSize) ?: 0f
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
        if (pointSize != 0f && state.level != null && state.robotState != null) {
            Level(
                level = state.level,
                robotState = state.robotState,
                movesRight = state.movesRight,
                pointSize = pointSize,
            )
        }
    }
}

@Composable
private fun BoxScope.Level(
    level: Level,
    robotState: RobotState,
    movesRight: Boolean,
    pointSize: Float
) {
    Box(
        modifier = Modifier
            .size(level.size.render(pointSize))
            .align(Alignment.Center)
            .background(Color.White)
    ) {
        level.blocks.forEach { block ->
            Block(block, pointSize)
        }
        Robot(
            state = robotState,
            pointSize = pointSize,
            movesRight = movesRight,
        )
    }
}

@Composable
private fun Robot(state: RobotState, movesRight: Boolean, pointSize: Float) {
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
            modifier = Modifier.scale(scaleX = if (movesRight) 1f else -1f, scaleY = 1f),
            resource = Res.drawable.robot,
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
            is Asset.Pass -> {
                Box(
                    modifier = Modifier.fillMaxSize().border(1.dp, color = Color.Black),
                )
            }

            is Asset.Wall -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .border(1.dp, color = Color.Black)
                        .background(
                            Color(
                                when (val id = asset.colorId) {
                                    0 -> 0xFF9E9E9E
                                    1 -> 0xFF757575
                                    2 -> 0xFF616161
                                    3 -> 0xFF424242
                                    4 -> 0xFF212121
                                    5 -> 0xFFD7CCC8
                                    6 -> 0xFFA1887F
                                    7 -> 0xFF795548
                                    8 -> 0xFF5D4037
                                    9 -> 0xFF3E2723
                                    else -> throw IllegalArgumentException("colorId can not be $id")
                                }
                            )
                        ),
                )
            }

            Asset.Target -> {
                ResourceImage(
                    resource = Res.drawable.target,
                )
            }

            Asset.CheckKey -> {
                ResourceImage(
                    resource = Res.drawable.password_texture,
                )
            }

            is Asset.Password -> {
                ResourceImage(
                    resource = Res.drawable.password_texture,
                )
                ScaledText(
                    text = asset.password,
                    scale = 0.4f,
                    color = Color(0xFFFF5252),
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
                    resource = Res.drawable.verify,
                )
            }
        }
    }
}

@Composable
private fun ResourceImage(modifier: Modifier = Modifier, resource: DrawableResource) {
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
