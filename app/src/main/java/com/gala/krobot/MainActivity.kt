package com.gala.krobot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gala.krobot.ui.theme.KrobotTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KrobotTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(modifier = Modifier.padding(innerPadding)) { Maze() }

                }

            }
        }
    }
}

@Composable
fun Maze() {
    val cellSize = 60.dp
    val walls = listOf(
        Wall(position = Position(x = 3, y = 2)),
        Wall(position = Position(x = 2, y = 2)),

        )
    var hero by remember {
        mutableStateOf(Hero(position = Position(x = 0, y = 0), isDestroyed = false))
    }

    Column {
        Box(modifier = Modifier.size(cellSize * 10)) {
            walls.forEach { wall ->
                Image(
                    modifier = Modifier
                        .size(cellSize)
                        .offset(x = cellSize * wall.position.x, y = cellSize * wall.position.y),
                    painter = painterResource(id = R.drawable.wall),
                    contentDescription = ""
                )
            }
            Image(
                modifier = Modifier
                    .size(cellSize)
                    .offset(x = cellSize * hero.position.x, y = cellSize * hero.position.y),
                painter = painterResource(id = R.drawable.keyboard),
                contentDescription = ""
            )
        }
        Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Button(onClick = { hero = hero.movedLeft() }) {
                Arrow(text = "◀")
            }
            Button(onClick = { hero = hero.movedUp() }) {
                Arrow(text = "▲")
            }
            Button(onClick = { hero = hero.movedDown() }) {
                Arrow(text = "▼")
            }
            Button(onClick = { hero = hero.movedRight() }) {
                Arrow(text = "▶")
            }
        }
    }
}

@Composable
private fun Arrow(text: String) {
    Text(text = text, fontSize = 25.sp)
}


data class Wall(val position: Position) {
    fun heroMoved(hero: Hero): Hero {
        return if (hero.position == position) {
            hero.copy(isDestroyed = true)
        } else {
            hero
        }
    }
}

data class Hero(val position: Position, val isDestroyed: Boolean) {
    fun movedUp(): Hero {
        return copy(position = position.copy(y = position.y - 1))
    }

    fun movedDown(): Hero {
        return copy(position = position.copy(y = position.y + 1))
    }

    fun movedLeft(): Hero {
        return copy(position = position.copy(x = position.x - 1))
    }

    fun movedRight(): Hero {
        return copy(position = position.copy(x = position.x + 1))
    }
}

data class Position(val x: Int, val y: Int)

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    KrobotTheme {
        Maze()
    }
}

