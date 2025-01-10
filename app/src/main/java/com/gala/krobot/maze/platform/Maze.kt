package com.gala.krobot.maze.platform

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gala.krobot.R
import com.gala.krobot.maze.common.presentation.MazeViewModel
import com.gala.krobot.ui.theme.CellSize
import com.gala.krobot.ui.theme.KrobotTheme

@Composable
fun Maze() {
    val viewModel: MazeViewModel = viewModel()
    val state = viewModel.state

    if (!state.hero.isDestroyed) {
        Column {
            Box(modifier = Modifier.size(CellSize * 10)) {
                state.walls.forEach { wall ->
                    Image(
                        modifier = Modifier
                            .size(CellSize)
                            .offset(
                                x = CellSize * wall.position.x,
                                y = CellSize * wall.position.y,
                            ),
                        painter = painterResource(id = R.drawable.wall),
                        contentDescription = ""
                    )
                }
                Image(
                    modifier = Modifier
                        .size(CellSize)
                        .offset(
                            x = CellSize * state.hero.position.x,
                            y = CellSize * state.hero.position.y,
                        ),
                    painter = painterResource(id = R.drawable.keyboard),
                    contentDescription = ""
                )
            }
            Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Button(onClick = { viewModel.leftClicked() }) {
                    Arrow(text = "◀")
                }
                Button(onClick = { viewModel.upClicked() }) {
                    Arrow(text = "▲")
                }
                Button(onClick = { viewModel.downClicked() }) {
                    Arrow(text = "▼")
                }
                Button(onClick = { viewModel.rightClicked() }) {
                    Arrow(text = "▶")
                }
            }
        }
    }
}

@Composable
private fun Arrow(text: String) {
    Text(text = text, fontSize = 25.sp)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    KrobotTheme {
        Maze()
    }
}
