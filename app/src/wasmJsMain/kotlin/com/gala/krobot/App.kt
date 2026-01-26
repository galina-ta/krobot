package com.gala.krobot

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.gala.krobot.engine.level.entity.Level
import com.gala.krobot.engine.program.LevelEditor

@Composable
fun App(
    isLevelEditor: Boolean,
    level: Level,
    levelDraw: String?,
    levelName: String,
    levelEditorRequested: () -> Unit,
    compileClicked: (String) -> Unit,
) {
    val router = remember {
        Router(
            initial = when {
                isLevelEditor -> Route.LevelEditor
                else -> Route.Robot.Level
            }
        )
    }
    when (val currentRoute = router.currentRoute) {
        Route.LevelEditor -> {
            LevelEditor(
                defaultValue = levelDraw ?: "",
                compileClicked = compileClicked,
            )
        }

        is Route.Robot -> {
            Robot(
                level = level,
                levelName = levelName,
                router = router,
                currentRoute = currentRoute,
                levelEditorRequested = levelEditorRequested,
            )
        }
    }
}
