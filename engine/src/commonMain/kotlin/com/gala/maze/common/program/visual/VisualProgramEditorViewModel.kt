package com.gala.maze.common.program.visual

import com.gala.maze.common.BaseViewModel
import com.gala.maze.common.program.Program
import com.gala.maze.common.program.visual.model.VisualProgram

class VisualProgramEditorViewModel(
    private val levelName: String,
    private val programUpdated: (Program) -> Unit,
) : BaseViewModel<VisualProgramEditorState>(
    VisualProgramEditorState(
        program = VisualProgram.empty,
        levelName = levelName,
    )
) {
    fun executeAction(action: VisualProgram.Action) {
        updateState { copy(program = program.modified(action)) }
        programUpdated(state.program.toProgram(levelName))
    }

    fun selectLine(line: VisualProgram.Line) {
        updateState { copy(program = program.withLineSelected(line)) }
    }
}

data class VisualProgramEditorState(
    val program: VisualProgram,
    val levelName: String,
)
