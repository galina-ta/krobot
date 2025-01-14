package com.gala.krobot.internal.platform

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.gala.krobot.createRobotController
import com.gala.krobot.global.globalRobotController
import com.gala.krobot.ui.theme.KrobotTheme
import com.gala.maze.common.arena.CreateRobotControllerHolder
import com.gala.maze.platform.arena.Maze
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var createRobotControllerHolder: CreateRobotControllerHolder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val robotController = createRobotController()
        globalRobotController = robotController
        createRobotControllerHolder.instance = { robotController }

        enableEdgeToEdge()
        setContent {
            KrobotTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        Maze()
                    }
                }
            }
        }
    }
}
