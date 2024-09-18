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
import androidx.compose.runtime.mutableIntStateOf
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
    var x by remember {
        mutableIntStateOf(0)
    }
    var y by remember {
        mutableIntStateOf(0)
    }
    Column {
        Box(modifier = Modifier.size(cellSize * 10)) {
            Image(
                modifier = Modifier
                    .size(cellSize)
                    .offset(x = cellSize * x, y = cellSize * y),
                painter = painterResource(id = R.drawable.keyboard),
                contentDescription = ""
            )
        }
        Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Button(onClick = { x -= 1 }) {
                Arrow(text = "◀")
            }
            Button(onClick = { y -= 1 }) {
                Arrow(text = "▲")
            }
            Button(onClick = { y += 1 }) {
                Arrow(text = "▼")
            }
            Button(onClick = { x += 1 }) {
                Arrow(text = "▶")
            }
        }
    }
}

@Composable
private fun Arrow(text: String) {
    Text(text = text, fontSize = 25.sp)
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    KrobotTheme {
        Maze()
    }
}