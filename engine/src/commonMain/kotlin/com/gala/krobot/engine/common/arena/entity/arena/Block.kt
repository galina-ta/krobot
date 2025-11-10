package com.gala.krobot.engine.common.arena.entity.arena

import com.gala.krobot.engine.common.arena.entity.Position
import com.gala.krobot.engine.common.arena.entity.RobotState
import com.gala.krobot.engine.common.arena.entity.Size
import com.gala.krobot.engine.common.arena.entity.SizePoint
import com.gala.krobot.engine.common.arena.entity.vp
import kotlin.math.absoluteValue
import kotlin.random.Random

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
    data class Password(val password: String) : Asset()
    data class Code(val randomCode: Int) : Asset()
    object CheckCode : Asset()
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

class MaybeCheckKeyBlock(position: Position) : CheckKeyBlock(position) {
    private val needCheck = Random.nextInt() % 5 == 0 // Шанс 1/5

    override val requiresKey = needCheck
    override val asset: Asset = if (needCheck) Asset.CheckKey else Asset.Pass

    override fun beforeRobotMove(robotState: RobotState): RobotState? {
        return if (needCheck)
            super.beforeRobotMove(robotState)
        else
            null
    }
}

class PasswordBlock(position: Position) : Block(position) {
    private val password = (position.intHash() % 10).toString()

    override val asset = Asset.Password(password)

    override fun beforeRobotMove(robotState: RobotState): RobotState? {
        return if (robotState.position == position && robotState.text != password)
            robotState.destroyed().withSource(source = this)
        else
            null
    }
}

class RandomCodeBlock(position: Position) : Block(position) {
    val randomCode = Random.nextInt().absoluteValue % 10

    override val asset = Asset.Code(randomCode)

    override fun afterRobotMove(robotState: RobotState): RobotState? {
        return if (robotState.position == position)
            robotState.withCode(randomCode)
        else
            null
    }
}

class CheckCodeBlock(position: Position) : Block(position) {
    override val asset = Asset.CheckCode

    override fun beforeRobotMove(robotState: RobotState): RobotState? {
        return if (robotState.position == position)
            robotState.also { state ->
                state.checkCode()
            }
        else
            null
    }
}
