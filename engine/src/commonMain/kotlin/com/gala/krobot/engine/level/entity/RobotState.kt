package com.gala.krobot.engine.level.entity

data class RobotState(
    val position: Position,
    val finishReason: RobotException? = null,
    val initKeyPosition: Position? = null,
    val nextStepKey: String? = null,
    val currentKey: String? = null,
    val nextStepCode: Int? = null,
    val currentCode: Int? = null,
    val beforeMove: () -> Unit = {},
    val isWon: Boolean = false,
    val source: Source? = null,
) {
    val size = Size.Virtual(width = 1.vp, height = 1.vp)

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
        )
    }

    fun withCode(code: Int): RobotState {
        return copy(nextStepCode = code)
    }

    fun withInitKey(): RobotState {
        return if (initKeyPosition == null)
            copy(initKeyPosition = position)
        else
            throw AlreadyHaveKeyException()
    }

    fun isKeyValid(): Boolean {
        val hash = initKeyPosition?.hash() ?: throw KeyIsNotProducedException()
        return currentKey == hash
    }

    fun withKey(key: String): RobotState {
        return copy(nextStepKey = key)
    }

    fun getKey(): String {
        return initKeyPosition?.hash() ?: throw KeyIsNotProducedException()
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

class AlreadyHaveKeyException : IllegalStateException("You've already got key. Use it")

class KeyIsNotProducedException : IllegalStateException("You need to produce the key")
