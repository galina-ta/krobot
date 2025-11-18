package com.gala.krobot.engine.level.entity

data class RobotState(
    val position: Position,
    val finishReason: RobotException? = null,
    val currentBlockCollectable: Collectable? = null,
    val nextStepKey: Key? = null,
    val currentKey: Key? = null,
    val nextStepCode: Int? = null,
    val currentCode: Int? = null,
    val beforeMove: () -> Unit = {},
    val isWon: Boolean = false,
    val source: Source? = null,
) {
    val size = Size.Virtual(width = 1.vp, height = 1.vp)

    val key = currentKey ?: nextStepKey
    val code = currentCode ?: nextStepCode

    fun destroyed(): RobotState {
        return copy(finishReason = RobotException("Robot is destroyed"))
    }

    fun won(): RobotState {
        return copy(isWon = true)
    }

    fun moved(direction: Position.Direction): RobotState {
        return copy(
            position = position.moved(direction),
            nextStepKey = null,
            currentKey = nextStepKey,
            nextStepCode = null,
            currentCode = nextStepCode,
            currentBlockCollectable = null,
        )
    }

    fun withCode(code: Int): RobotState {
        return copy(nextStepCode = code)
    }

    fun isKeyValid(): Boolean {
        return currentKey != null
    }

    fun withKey(key: Key): RobotState {
        return copy(nextStepKey = key)
    }

    fun getKey(): Key {
        return currentKey ?: throw NoKeyCollectedException()
    }

    fun withBeforeMove(beforeMove: () -> Unit): RobotState {
        return copy(beforeMove = beforeMove)
    }

    fun finished(reason: RobotException?): RobotState {
        return copy(finishReason = reason)
    }

    fun withSource(source: Source): RobotState {
        return copy(source = source)
    }

    fun withGettable(collectable: Collectable?): RobotState {
        return copy(currentBlockCollectable = collectable)
    }

    fun collectKey(): Key {
        return currentBlockCollectable
            ?.also { it.collected() } as? Key
            ?: throw NoKeyOnBlockException()
    }

    override fun toString(): String {
        return "$position finishReason=$finishReason from=${source?.sourceRepresentation()}"
    }

    interface Source {
        fun sourceRepresentation(): String
    }
}

class RobotException : RuntimeException {
    constructor(message: String?) : super(message)
    constructor(cause: Throwable?) : super(cause)

    override fun equals(other: Any?): Boolean {
        return other is RobotException && message == other.message
    }

    override fun hashCode(): Int {
        return message?.hashCode() ?: 0
    }
}

class NoKeyOnBlockException : IllegalStateException("There is no key on the current block")

class NoKeyCollectedException : IllegalStateException("Key is not collected")
