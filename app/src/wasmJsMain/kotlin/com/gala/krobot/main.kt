package com.gala.krobot

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.gala.krobot.engine.level.entity.parseLevel
import com.gala.krobot.engine.levels.demoLevel
import com.gala.krobot.ui.theme.KrobotTheme
import io.ktor.http.URLBuilder
import io.ktor.http.parseUrl
import kotlinx.browser.document
import kotlinx.browser.window

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
            App(
                isLevelEditor = isLevelEditor,
                level = level,
                levelDraw = levelDraw,
                levelName = levelName,
                levelEditorRequested = {
                    val editorUrl = URLBuilder(url).apply {
                        parameters.append(LEVEL_EDITOR_KEY, "true")
                        if (levelDraw != null) {
                            parameters[LEVEL_KEY] = levelDraw.toUrlLevel()
                        }
                    }
                    window.open(url = editorUrl.buildString(), "_self")
                },
                compileClicked = { levelDraw ->
                    val robotUrl = URLBuilder(url).apply {
                        parameters.remove(LEVEL_EDITOR_KEY)
                        parameters[LEVEL_KEY] = levelDraw.toUrlLevel()
                    }
                    window.open(url = robotUrl.buildString(), "_self")
                },
            )
        }
    }
}

private const val LEVEL_EDITOR_KEY = "levelEditor"
private const val LEVEL_KEY = "level"
private const val LEVEL_NAME_KEY = "levelName"
