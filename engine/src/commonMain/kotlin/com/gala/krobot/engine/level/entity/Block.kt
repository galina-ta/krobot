package com.gala.krobot.engine.level.entity

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
    class KeyIfNotCollected(val key: Key) : Asset()
    data class CheckCode(val code: Int) : Asset()
    data class ConditionalLock(val number: Int) : Asset()
    data class ConditionalOpenedLockNumber(val number: Number) : Asset()
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
    override val asset = Asset.CheckKey

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
    private val key = Key()
    override val asset = Asset.KeyIfNotCollected(key)

    override fun afterRobotMove(robotState: RobotState): RobotState? {
        return if (robotState.position == position && robotState.currentBlockCollectable == null)
            robotState.withCollectable(key)
        else
            null
    }
}

class ConditionalLockBlock(
    position: Position,
    private val number: Int,
) : Block(position) {
    override val asset = Asset.ConditionalLock(number)

    override fun beforeRobotMove(robotState: RobotState): RobotState? {
        val lockNumber = robotState.openedConditionalLockNumber
        return if (
            robotState.position == position &&
            (lockNumber == null || lockNumber.value != number)
        ) {
            robotState.destroyed().withSource(source = this)
        } else {
            null
        }
    }
}

class ConditionalOpenedLockNumberBlock(
    position: Position,
) : Block(position) {
    private lateinit var number: Number

    override val asset: Asset get() = Asset.ConditionalOpenedLockNumber(number)

    override fun afterRobotStateCreate(robotState: RobotState) {
        number = robotState.openedConditionalLockNumber!!
    }

    override fun afterRobotMove(robotState: RobotState): RobotState? {
        return if (robotState.position == position && robotState.currentBlockCollectable == null)
            robotState.withCollectable(number)
        else
            null
    }
}
