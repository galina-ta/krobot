package com.gala.krobot

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ComposeViewport
import com.gala.krobot.engine.level.LevelViewModel
import com.gala.krobot.engine.level.CreateRobotControllerHolder
import com.gala.krobot.engine.level.entity.Level
import com.gala.krobot.engine.level.entity.parseLevel
import com.gala.krobot.engine.level.ui.LevelScreen
import com.gala.krobot.engine.program.LevelEditor
import com.gala.krobot.engine.program.Program
import com.gala.krobot.engine.program.ProgramRobotController
import com.gala.krobot.engine.program.visual.VisualProgramEditorViewModel
import com.gala.krobot.engine.program.visual.ui.VisualProgramEditor
import com.gala.krobot.engine.level.impls.RobotExecutorImpl
import com.gala.krobot.engine.level.impls.RobotStatesApplierImpl
import com.gala.krobot.engine.levels.demoLevel
import com.gala.krobot.ui.theme.KrobotTheme
import io.ktor.http.URLBuilder
import io.ktor.http.parseUrl
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
//    val createRobotControllerHolder = CreateRobotControllerHolder()

//    val robotController = createRobotController()
//    globalRobotController = robotController
//    createRobotControllerHolder.instance = { robotController }

    val url = parseUrl(document.URL)!!
    val levelName = url.parameters[LEVEL_NAME_KEY] ?: "пробный"
    val levelDraw = url.parameters[LEVEL_KEY]?.toLevelDraw()
    val level = levelDraw
        ?.let { parseLevel(it) }
        ?: demoLevel

    val isLevelEditor = url.parameters[LEVEL_EDITOR_KEY] != null

    ComposeViewport {
        KrobotTheme {
            if (isLevelEditor) {
                LevelEditor(
                    defaultValue = levelDraw ?: "",
                    compileClicked = { levelDraw ->
                        val robotUrl = URLBuilder(url).apply {
                            parameters.remove(LEVEL_EDITOR_KEY)
                            parameters[LEVEL_KEY] = levelDraw.toUrlLevel()
                        }
                        window.open(url = robotUrl.buildString(), "_self")
                    }
                )
            } else {
                Main(
                    levelName,
                    level,
                    levelEditorRequested = {
                        val editorUrl = URLBuilder(url).apply {
                            parameters.append(LEVEL_EDITOR_KEY, "true")
                            if (levelDraw != null) {
                                parameters[LEVEL_KEY] = levelDraw.toUrlLevel()
                            }
                        }
                        window.open(url = editorUrl.buildString(), "_self")
                    },
                )
            }
        }
    }
}

@Composable
private fun Main(
    levelName: String,
    level: Level,
    levelEditorRequested: () -> Unit,
) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        var isCodeEditing by remember { mutableStateOf(false) }
        var program: Program by remember { mutableStateOf(Program.setLevel(levelName)) }
        val visualProgramEditorViewModel = remember {
            VisualProgramEditorViewModel(
                levelName = levelName,
                programUpdated = {
                    program = it
                }
            )
        }
        Column(modifier = Modifier.padding(innerPadding)) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Button(
                    modifier = Modifier.padding(start = 6.dp, top = 6.dp),
                    onClick = {
                        isCodeEditing = !isCodeEditing
                    }
                ) {
                    Text(text = if (isCodeEditing) "Уровень" else "Код")
                }
                Button(
                    modifier = Modifier
                        .padding(end = 6.dp, top = 6.dp)
                        .align(Alignment.CenterEnd),
                    onClick = levelEditorRequested,
                ) {
                    Text(text = "Редактор уровня")
                }
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
        }
    }
}

private fun String.toUrlLevel(): String =
    replace('\n', '|')

private fun String.toLevelDraw(): String =
    replace('|', '\n')

private const val LEVEL_EDITOR_KEY = "levelEditor"
private const val LEVEL_KEY = "level"
private const val LEVEL_NAME_KEY = "levelName"
