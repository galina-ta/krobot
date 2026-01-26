package com.gala.krobot

import androidx.compose.foundation.layout.Box
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
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Button(
            modifier = Modifier.padding(start = 6.dp, top = 6.dp),
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
                    Route.Robot.CodeEditor -> "Уровень"
                    Route.Robot.Level -> "Код"
                    Route.LevelEditor -> throw IllegalStateException("LevelEditor has no TopBar")
                }
            )
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
}
