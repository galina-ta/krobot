package com.gala.krobot

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.gala.krobot.engine.level.CreateRobotControllerHolder
import com.gala.krobot.engine.level.LevelViewModel
import com.gala.krobot.engine.level.entity.Level
import com.gala.krobot.engine.level.impls.RobotExecutorImpl
import com.gala.krobot.engine.level.impls.RobotStatesApplierImpl
import com.gala.krobot.engine.level.ui.LevelScreen
import com.gala.krobot.engine.program.Program
import com.gala.krobot.engine.program.ProgramRobotController
import com.gala.krobot.engine.program.visual.VisualProgramEditorViewModel
import com.gala.krobot.engine.program.visual.entity.toProgram
import com.gala.krobot.engine.program.visual.ui.VisualProgramEditor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

@Composable
fun Robot(
    level: Level,
    levelName: String,
    router: Router,
    currentRoute: Route.Robot,
    levelEditorRequested: () -> Unit,
) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        var program: Program by remember { mutableStateOf(Program.setLevel(levelName)) }
        val visualProgramEditorViewModel = remember {
            VisualProgramEditorViewModel(
                levelName = levelName,
                programUpdated = { visualProgram ->
                    program = visualProgram.toProgram()
                }
            )
        }
        LaunchedEffect(visualProgramEditorViewModel) {
            updateVisualProgram = { visualProgram ->
                visualProgramEditorViewModel.programRestored(visualProgram)
            }
        }
        Column(modifier = Modifier.padding(innerPadding)) {
            TopBar(
                router = router,
                levelEditorRequested = levelEditorRequested,
                currentProgramString = {
                    encodeProgram(visualProgramEditorViewModel.state.program)
                },
                programRestored = { programString ->
                    try {
                        val program = decodeProgram(programString)
                        visualProgramEditorViewModel.programRestored(program)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                },
            )
            when (currentRoute) {
                Route.Robot.Level -> {
                    val levelViewModel = remember(program) {
                        val createRobotControllerHolder = CreateRobotControllerHolder()
                        val controller = ProgramRobotController(
                            program,
                            dynamicLevelName = levelName,
                            dynamicLevel = level,
                        )
                        createRobotControllerHolder.instance = { controller }
                        LevelViewModel(
                            createRobotControllerHolder = createRobotControllerHolder,
                            executor = RobotExecutorImpl(),
                            statesApplier = RobotStatesApplierImpl(),
                            scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
                        )
                    }
                    LevelScreen(
                        viewModel = levelViewModel,
                    )
                }

                Route.Robot.CodeEditor ->
                    VisualProgramEditor(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        viewModel = visualProgramEditorViewModel,
                    )
            }
        }
    }
}
