package com.gala.krobot

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.Clipboard
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SaveRestoreCode(
    currentProgramString: () -> String,
    programRestored: (String) -> Unit,
) {
    val clipboard = LocalClipboard.current
    var programTextValue by remember { mutableStateOf("") }
    Button(onClick = {
        val programString = currentProgramString()
        programTextValue = programString
        clipboard.setText(programString)
    }) {
        Text("Сохранить")
    }
    TextField(
        modifier = Modifier.width(130.dp).height(55.dp),
        maxLines = 1,
        value = programTextValue,
        placeholder = {
            Text("Программа", maxLines = 1)
        },
        onValueChange = { newValue ->
            programTextValue = newValue
        }
    )
    Button(onClick = {
        programRestored(programTextValue)
    }) {
        Text("Восстановить")
    }
}

internal expect fun Clipboard.setText(text: String)
