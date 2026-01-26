package com.gala.krobot

import com.gala.krobot.engine.program.visual.entity.VisualProgram

var updateVisualProgram: (VisualProgram) -> Unit = {}

@OptIn(ExperimentalJsExport::class)
@JsExport
fun setVisualProgram(string: String) {
    val program = decodeProgram(string)
    println("setVisualProgram, program=$program")
    updateVisualProgram(program)
}

fun printVisualProgram(program: VisualProgram) {
    val string = encodeProgram(program)
    println(string)
}
