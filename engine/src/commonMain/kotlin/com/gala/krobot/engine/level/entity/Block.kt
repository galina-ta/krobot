package com.gala.krobot.engine.level.entity

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

sealed class Block(
    val position: Position,
) : RobotStateMutationsProvider, RobotState.Source {

    abstract val asset: Asset

    open val requiresKey: Boolean = false

    val size = Size.Virtual(width = 1.vp, height = 1.vp)

    val horEnd: SizePoint.Virtual get() = position.x + size.width
    val verEnd: SizePoint.Virtual get() = position.y + size.height

    override fun toString(): String {
        return "${this::class.simpleName} $position $size"
    }

    override fun sourceRepresentation(): String {
        return "${this::class.simpleName} $position $size"
    }

    override fun equals(other: Any?): Boolean {
        return other != null &&
                this::class == other::class &&
                other is Block &&
                this.position == other.position
    }

    override fun hashCode(): Int {
        return position.hashCode()
    }
}

sealed class Asset {
    object Pass : Asset()
    data class Wall(val colorId: Int) : Asset()
    object Target : Asset()
    object CheckKey : Asset()
    object Key : Asset()
    data class CheckCode(val code: Int) : Asset()
}

class VoidBlock(position: Position) : Block(position) {
    override val asset = Asset.Pass
}

class WallBlock(position: Position, colorId: Int) : Block(position) {
    override val asset = Asset.Wall(colorId)

    override fun beforeRobotMove(robotState: RobotState): RobotState? {
        return if (robotState.position == position)
            robotState.destroyed().withSource(source = this)
        else
            null
    }
}

class TargetBlock(position: Position) : Block(position) {
    override val asset = Asset.Target

    override fun afterRobotMove(robotState: RobotState): RobotState? {
        return if (robotState.position == position) {
            robotState.won().withSource(source = this)
        } else {
            null
        }
    }
}


open class CheckKeyBlock(position: Position) : Block(position) {
    override val asset: Asset = Asset.CheckKey

    override val requiresKey = true

    override fun beforeRobotMove(robotState: RobotState): RobotState? {
        return if (robotState.position == position && !robotState.isKeyValid()) {
            robotState.destroyed().withSource(this)
        } else {
            null
        }
    }

    override fun sourceRepresentation(): String {
        return "Key is not entered. ${super.sourceRepresentation()}"
    }
}

class CheckCodeBlock(position: Position) : Block(position) {
    private val code = position.intHash() % 10

    override val asset = Asset.CheckCode(code)

    override fun beforeRobotMove(robotState: RobotState): RobotState? {
        return if (robotState.position == position && robotState.currentCode != code)
            robotState.destroyed().withSource(source = this)
        else
            null
    }
}

class KeyBlock(position: Position) : Block(position) {
    override var asset: Asset by mutableStateOf(Asset.Key)

    private var key: Key? = Key(
        collected = {
            key = null
            asset = Asset.Pass
        },
    )

    override fun afterRobotMove(robotState: RobotState): RobotState? {
        return if (robotState.position == position && robotState.currentBlockCollectable == null)
            robotState.withGettable(key)
        else
            null
    }
}
