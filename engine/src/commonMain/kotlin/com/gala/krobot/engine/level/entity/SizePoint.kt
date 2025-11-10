package com.gala.krobot.engine.level.entity

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

sealed class SizePoint : Comparable<SizePoint> {

    protected abstract val value: Int

    operator fun div(other: SizePoint): Float = this.value.toFloat() / other.value.toFloat()

    override fun compareTo(other: SizePoint): Int {
        return value.compareTo(other.value)
    }

    fun render(pointSize: Float): Dp {
        return (value * pointSize).dp
    }

    fun rangeTo(): IntRange {
        return 0 until value
    }

    fun hash(): String {
        return value.toString()
    }

    fun intHash(): Int {
        return value
    }

    data class Virtual(override val value: Int) : SizePoint() {
        operator fun plus(other: SizePoint) = Virtual(this.value + other.value)
        operator fun minus(other: SizePoint) = Virtual(this.value - other.value)
        override fun toString(): String = "${value}.vp"
    }

    class Real(override val value: Int) : SizePoint() {
        override fun toString(): String = "${value}.rp"
    }
}

val Int.vp get() = SizePoint.Virtual(value = this)
val Int.rp get() = SizePoint.Real(value = this)
