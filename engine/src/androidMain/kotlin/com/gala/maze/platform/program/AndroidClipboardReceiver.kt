package com.gala.maze.platform.program

import android.content.ClipboardManager
import android.content.Context
import com.gala.maze.common.program.text.ClipboardReceiver

class AndroidClipboardReceiver(
    private val context: Context,
) : ClipboardReceiver {

    override fun get(): String? {
        val manager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        return manager.primaryClip?.getItemAt(0)?.text?.toString()
    }
}
