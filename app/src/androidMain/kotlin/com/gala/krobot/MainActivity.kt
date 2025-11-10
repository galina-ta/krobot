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
import com.gala.krobot.engine.level.LevelViewModel
import com.gala.krobot.engine.level.CreateRobotControllerHolder
import com.gala.krobot.engine.level.ui.LevelScreen
import com.gala.krobot.engine.level.impls.RobotExecutorImpl
import com.gala.krobot.engine.level.impls.RobotStatesApplierImpl
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

        val viewModel = LevelViewModel(
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
                        LevelScreen(viewModel)
                    }
                }
            }
        }
    }
}
