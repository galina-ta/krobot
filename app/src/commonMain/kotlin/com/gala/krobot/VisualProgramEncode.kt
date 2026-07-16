package com.gala.krobot

import com.gala.krobot.engine.program.visual.entity.VisualProgram
import kotlinx.serialization.json.Json

private val json = Json {
    ignoreUnknownKeys = true
}

fun encodeProgram(program: VisualProgram): String =
    json.encodeToString(program)

fun decodeProgram(string: String): VisualProgram =
    json.decodeFromString<VisualProgram>(string)
