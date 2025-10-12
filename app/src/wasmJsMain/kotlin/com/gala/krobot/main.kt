package com.gala.krobot

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.ComposeViewport
import com.gala.krobot.global.globalRobotController
import com.gala.krobot.ui.theme.KrobotTheme
import com.gala.maze.common.arena.ArenaViewModel
import com.gala.maze.common.arena.CreateRobotControllerHolder
import com.gala.maze.common.arena.ui.Maze
import com.gala.maze.common.program.ProgramParser
import com.gala.maze.impls.RobotExecutorImpl
import com.gala.maze.impls.RobotStatesApplierImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val createRobotControllerHolder = CreateRobotControllerHolder()

    val robotController = createRobotController()
    globalRobotController = robotController
    createRobotControllerHolder.instance = { robotController }

    val viewModel = ArenaViewModel(
        createRobotControllerHolder = createRobotControllerHolder,
        executor = RobotExecutorImpl(),
        statesApplier = RobotStatesApplierImpl(),
//        clipboardReceiver = AndroidClipboardReceiver(AppHolder.instance),
        programParser = ProgramParser(),
        scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    )

    ComposeViewport {
        KrobotTheme {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                Box(modifier = Modifier.padding(innerPadding)) {
                    Maze(viewModel)
                }
            }
        }
    }
}
