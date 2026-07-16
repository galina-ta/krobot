package com.gala.krobot

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.Clipboard

@OptIn(ExperimentalComposeUiApi::class, ExperimentalWasmJsInterop::class)
internal actual fun Clipboard.setText(text: String) {
    try {
        nativeClipboard.writeText(text)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
