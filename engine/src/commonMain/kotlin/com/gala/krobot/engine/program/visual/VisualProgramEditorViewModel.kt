package com.gala.krobot.engine.program.visual

import com.gala.krobot.engine.base.BaseViewModel
import com.gala.krobot.engine.program.Program
import com.gala.krobot.engine.program.visual.entity.Action
import com.gala.krobot.engine.program.visual.entity.VisualProgram
import com.gala.krobot.engine.program.visual.entity.VisualProgramLine
import com.gala.krobot.engine.program.visual.entity.toProgram

class VisualProgramEditorViewModel(
    levelName: String,
    private val programUpdated: (Program) -> Unit,
) : BaseViewModel<VisualProgramEditorState>(
    VisualProgramEditorState(
        program = VisualProgram.empty(levelName),
        levelName = levelName,
    )
) {
    fun executeAction(action: Action) {
        updateState { copy(program = program.modified(action)) }
        programUpdated(state.program.toProgram())
    }

    fun selectLine(line: VisualProgramLine) {
        updateState { copy(program = program.withLineSelected(line)) }
    }
}

data class VisualProgramEditorState(
    val program: VisualProgram,
    val levelName: String,
)
