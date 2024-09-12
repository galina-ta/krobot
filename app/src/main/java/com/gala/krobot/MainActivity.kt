package com.gala.krobot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
    val cellSize = 20.dp
    var x by remember {
        mutableIntStateOf(0)
    }
    var y by remember {
        mutableIntStateOf(0)
    }
    Box(modifier = Modifier.size(cellSize * 10)) {
        Image(
            modifier = Modifier
                .size(cellSize)
                .padding(start = cellSize * x, top = cellSize * y),
            painter = painterResource(id = R.drawable.keyboard),
            contentDescription = ""
        )
    }
    Button(onClick = { x -= 1 }) {
        Text(text = "←")
    }
    Button(onClick = { x += 1 }) {
        Text(text = "→")
    }
    Button(onClick = { y -= 1 }) {
        Text(text = "↑")
    }
    Button(onClick = { y += 1 }) {
        Text(text = "↓")
    }
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