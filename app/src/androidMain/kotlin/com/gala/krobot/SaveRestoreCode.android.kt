package com.gala.krobot

import androidx.compose.ui.platform.Clipboard

internal actual fun Clipboard.setText(text: String) {
    nativeClipboard.text = text
}
