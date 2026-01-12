package com.gala.krobot

import com.gala.krobot.engine.program.visual.entity.VisualProgram
import kotlinx.serialization.json.Json

private val json = Json {
    ignoreUnknownKeys = true
}

var updateVisualProgram: (VisualProgram) -> Unit = {}

@OptIn(ExperimentalJsExport::class)
@JsExport
fun setVisualProgram(string: String) {
    val program = json.decodeFromString<VisualProgram>(string)
    println("setVisualProgram, program=$program")
    updateVisualProgram(program)
}

fun printVisualProgram(program: VisualProgram) {
    val string = json.encodeToString(program)
    println(string)
}
