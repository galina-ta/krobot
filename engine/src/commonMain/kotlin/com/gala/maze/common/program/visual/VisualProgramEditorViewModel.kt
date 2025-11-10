package com.gala.maze.common.program.visual

import com.gala.maze.common.BaseViewModel
import com.gala.maze.common.program.Program
import com.gala.maze.common.program.visual.model.Action
import com.gala.maze.common.program.visual.model.VisualProgram
import com.gala.maze.common.program.visual.model.VisualProgramLine
import com.gala.maze.common.program.visual.model.toProgram

class VisualProgramEditorViewModel(
    private val levelName: String,
    private val programUpdated: (Program) -> Unit,
) : BaseViewModel<VisualProgramEditorState>(
    VisualProgramEditorState(
        program = VisualProgram.empty,
        levelName = levelName,
    )
) {
    fun executeAction(action: Action) {
        updateState { copy(program = program.modified(action)) }
        programUpdated(state.program.toProgram(levelName))
    }

    fun selectLine(line: VisualProgramLine) {
        updateState { copy(program = program.withLineSelected(line)) }
    }
}

data class VisualProgramEditorState(
    val program: VisualProgram,
    val levelName: String,
)
