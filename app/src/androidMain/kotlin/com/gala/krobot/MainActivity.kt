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
import com.gala.krobot.engine.level.entity.parseLevel
import com.gala.krobot.ui.theme.KrobotTheme
import io.ktor.http.decodeURLPart

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        val createRobotControllerHolder = CreateRobotControllerHolder()
//
//        val robotController = createRobotController()
//        globalRobotController = robotController
//        createRobotControllerHolder.instance = { robotController }

        val urlLevel = "00000|0s++0|00o00|0+++0|0+0+0|0%3F0%3F0|0+0+0|0+++0|00f00|00000"
            .decodeURLPart()
            .replace("+", " ")
        val levelDraw = urlLevel.toLevelDraw()
        val level = parseLevel(levelDraw)

        enableEdgeToEdge()
        setContent {
            KrobotTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        App(
                            isLevelEditor = false,
                            level = level,
                            levelDraw = levelDraw,
                            levelName = "Demo",
                            levelEditorRequested = {
                                // Do nothing for now
                            },
                            compileClicked = { levelDraw ->
                                // Do nothing for now
                            },
                        )
                    }
                }
            }
        }
    }
}
