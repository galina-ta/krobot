package com.gala.krobot

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ComposeViewport
import com.gala.krobot.ui.theme.KrobotTheme
import com.gala.maze.common.arena.ArenaViewModel
import com.gala.maze.common.arena.CreateRobotControllerHolder
import com.gala.maze.common.arena.entity.arena.parseArena
import com.gala.maze.common.arena.ui.Maze
import com.gala.maze.common.program.Program
import com.gala.maze.common.program.ProgramRobotController
import com.gala.maze.common.program.visual.VisualProgramEditorViewModel
import com.gala.maze.common.program.visual.ui.VisualProgramEditor
import com.gala.maze.impls.RobotExecutorImpl
import com.gala.maze.impls.RobotStatesApplierImpl
import com.gala.maze.levels.demoArena
import io.ktor.http.parseUrl
import kotlinx.browser.document
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
//    val createRobotControllerHolder = CreateRobotControllerHolder()

//    val robotController = createRobotController()
//    globalRobotController = robotController
//    createRobotControllerHolder.instance = { robotController }

    val url = parseUrl(document.URL)
    val levelName = url?.parameters?.get("levelName") ?: "пробный"
    val level = url?.parameters?.get("level")
        ?.replace('|', '\n')
        ?.let { parseArena(it) }
        ?: demoArena

    ComposeViewport {
        KrobotTheme {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                var isCodeEditing by remember { mutableStateOf(false) }
                var program: Program by remember { mutableStateOf(Program.setLevel(levelName)) }
                val visualProgramEditorViewModel = remember {
                    VisualProgramEditorViewModel(
                        levelName = levelName,
                        programUpdated = {
                            program = it
                            println(program)
                        }
                    )
                }
                Column(modifier = Modifier.padding(innerPadding)) {
                    Button(
                        modifier = Modifier.padding(start = 6.dp, top = 6.dp),
                        onClick = {
                            isCodeEditing = !isCodeEditing
                        }
                    ) {
                        Text(text = if (isCodeEditing) "Уровень" else "Код")
                    }
                    if (isCodeEditing) {
                        VisualProgramEditor(
                            modifier = Modifier.fillMaxWidth().weight(1f),
                            viewModel = visualProgramEditorViewModel,
                        )
                    } else {
                        val levelViewModel = remember(program) {
                            val createRobotControllerHolder = CreateRobotControllerHolder()
                            val controller = ProgramRobotController(
                                program,
                                dynamicLevelName = levelName,
                                dynamicLevel = level,
                            )
                            createRobotControllerHolder.instance = { controller }
                            ArenaViewModel(
                                createRobotControllerHolder = createRobotControllerHolder,
                                executor = RobotExecutorImpl(),
                                statesApplier = RobotStatesApplierImpl(),
//        clipboardReceiver = AndroidClipboardReceiver(AppHolder.instance),
//                                programParser = ProgramParser(),
                                scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
                            )
                        }
                        Maze(
                            modifier = Modifier.fillMaxWidth().weight(1f),
                            viewModel = levelViewModel,
                        )
                    }
                }
            }
        }
    }
}
