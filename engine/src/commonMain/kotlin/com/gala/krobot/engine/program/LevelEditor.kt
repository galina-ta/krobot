package com.gala.krobot.engine.program

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import krobot.engine.generated.resources.Res
import krobot.engine.generated.resources.SpaceMono_Regular
import org.jetbrains.compose.resources.Font

@Composable
fun LevelEditor(
    defaultValue: String?,
    compileClicked: (String) -> Unit,
) {
    var value: String by remember { mutableStateOf(defaultValue ?: "") }
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Button(
            onClick = {
                compileClicked(value)
            }
        ) {
            Text("Собрать")
        }
        TextField(
            modifier = Modifier.fillMaxWidth().weight(1f),
            value = value,
            textStyle = TextStyle(
                fontFamily = FontFamily(Font(Res.font.SpaceMono_Regular, FontWeight.Normal)),
            ),
            onValueChange = {
                value = it
            }
        )
    }
}
