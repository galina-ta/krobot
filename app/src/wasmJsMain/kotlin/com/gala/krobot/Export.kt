package com.gala.krobot

@OptIn(ExperimentalJsExport::class)
@JsExport
fun setVisualProgram(string: String) {
    val program = decodeProgram(string)
    println("setVisualProgram, program=$program")
    updateVisualProgram(program)
}
