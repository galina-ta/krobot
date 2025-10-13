package com.gala.maze.common.arena.entity

import androidx.compose.ui.unit.DpSize

sealed class Size {
    abstract val width: SizePoint
    abstract val height: SizePoint

    /**
     * For which value you must multiply [width] to get [height]
     */
    val ratio: Float get() = height / width

    fun render(pointSize: Float): DpSize = DpSize(
        width = width.render(pointSize),
        height = height.render(pointSize),
    )

    data class Virtual(
        override val width: SizePoint.Virtual,
        override val height: SizePoint.Virtual,
    ) : Size()

    data class Real(
        override val width: SizePoint,
        override val height: SizePoint,
    ) : Size()

    override fun toString(): String = "$width*$height"
}
