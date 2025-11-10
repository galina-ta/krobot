package com.gala.krobot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.gala.krobot.global.globalRobotController
import com.gala.krobot.ui.theme.KrobotTheme
import com.gala.maze.common.arena.ArenaViewModel
import com.gala.maze.common.arena.CreateRobotControllerHolder
import com.gala.maze.common.arena.ui.Maze
import com.gala.maze.impls.RobotExecutorImpl
import com.gala.maze.impls.RobotStatesApplierImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val createRobotControllerHolder = CreateRobotControllerHolder()

        val robotController = createRobotController()
        globalRobotController = robotController
        createRobotControllerHolder.instance = { robotController }

        val viewModel = ArenaViewModel(
            createRobotControllerHolder = createRobotControllerHolder,
            executor = RobotExecutorImpl(),
            statesApplier = RobotStatesApplierImpl(),
            scope = CoroutineScope(Dispatchers.Main.immediate + SupervisorJob()),
        )

        enableEdgeToEdge()
        setContent {
            KrobotTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        Maze(viewModel)
                    }
                }
            }
        }
    }
}
