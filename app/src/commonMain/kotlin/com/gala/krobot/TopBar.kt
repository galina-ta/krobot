package com.gala.krobot

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TopBar(
    router: Router,
    levelEditorRequested: () -> Unit,
    currentProgramString: () -> String,
    programRestored: (String) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Button(
            onClick = {
                router.navigate(
                    route = when (router.currentRoute) {
                        Route.Robot.CodeEditor -> Route.Robot.Level
                        Route.Robot.Level -> Route.Robot.CodeEditor
                        Route.LevelEditor ->
                            throw IllegalStateException("LevelEditor is not in the graph")
                    }
                )
            }
        ) {
            Text(
                text = when (router.currentRoute) {
                    Route.Robot.CodeEditor -> "Проект"
                    Route.Robot.Level -> "Код"
                    Route.LevelEditor -> throw IllegalStateException("LevelEditor has no TopBar")
                }
            )
        }

        if (router.currentRoute == Route.Robot.CodeEditor) {
            SaveRestoreCode(
                currentProgramString = currentProgramString,
                programRestored = programRestored,
            )
        }

        Spacer(Modifier.weight(1f))

        Button(onClick = levelEditorRequested) {
            Text(text = "Редактор уровня")
        }
    }
}
