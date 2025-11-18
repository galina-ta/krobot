package com.gala.krobot.engine.level.entity

data class Level(
    val initialRobotState: RobotState,
    private val nonVoidBlocks: List<Block>,
) : RobotStateMutationsProvider, RobotState.Source {

    private val maxKeyUsage: Int = nonVoidBlocks.count { it.requiresKey }

    private var usedKeyPositions = mutableSetOf<Position>()

    val size = Size.Virtual(
        width = nonVoidBlocks.maxByOrNull { block -> block.horEnd }?.horEnd ?: 1.vp,
        height = nonVoidBlocks.maxByOrNull { block -> block.verEnd }?.verEnd ?: 1.vp
    )

    val blocks: List<Block> = size.height.rangeTo().flatMap { lineIndex ->
        size.width.rangeTo().map { rowIndex ->
            val currentPosition = Position(x = rowIndex.vp, y = lineIndex.vp)
            val block = nonVoidBlocks.find { it.position == currentPosition }
            block ?: VoidBlock(position = currentPosition)
        }
    }

    fun blockOn(position: Position): Block? {
        return blocks.find { it.position == position }
    }

    fun calculatePointSize(screenSize: Size): Float {
        return if (size.ratio > screenSize.ratio) {
            screenSize.height / size.height
        } else {
            screenSize.width / size.width
        }
    }

    override fun beforeRobotMove(robotState: RobotState): RobotState? {
        if (robotState.currentKey != null) {
            usedKeyPositions.add(robotState.position)
            require(usedKeyPositions.size <= maxKeyUsage) {
                "Key could be used not more than $maxKeyUsage times"
            }
        }
        if (!robotState.position.isIn(size)) {
            return robotState.destroyed().withSource(source = this)
        } else {
            blocks.forEach { block ->
                val newState = block.beforeRobotMove(robotState)
                if (newState != null) {
                    return newState
                }
            }
            return null
        }
    }

    override fun afterRobotMove(robotState: RobotState): RobotState? {
        blocks.forEach { block ->
            val newState = block.afterRobotMove(robotState)
            if (newState != null) {
                return newState
            }
        }
        return null
    }

    override fun sourceRepresentation(): String {
        return this::class.simpleName.toString()
    }

    override fun toString(): String {
        return size.height.rangeTo().joinToString(separator = "\n") { lineIndex ->
            size.width.rangeTo().map { rowIndex ->
                val currentPosition = Position(x = lineIndex.vp, y = rowIndex.vp)
                when (val block = blocks.first { it.position == currentPosition }) {
                    is VoidBlock -> ' '
                    is WallBlock -> block.toString().also { require(it.length == 1) }.first()
                    is TargetBlock -> 'f'
                    else -> IllegalArgumentException("block can not be $block")
                }
            }.joinToString(separator = "")
        }
    }
}

/**
 * 's': initial robot position
 * '0'..'9': wall blocks
 * 'f': finish
 * ' ': pass
 */
fun parseLevel(draw: String): Level {
    var initialRobotPosition: Position? = null
    val blocks = mutableListOf<Block>()
    draw.lines().forEachIndexed { lineIndex, line ->
        line.forEachIndexed { charIndex, char ->
            val position = Position(x = charIndex.vp, y = lineIndex.vp)
            when (char) {
                's' -> initialRobotPosition = position
                in '0'..'9' -> blocks.add(WallBlock(position, colorId = char.digitToInt()))
                'f' -> blocks.add(TargetBlock(position))
                '*' -> blocks.add(CheckCodeBlock(position))
                '#' -> blocks.add(CheckKeyBlock(position))
                'k' -> blocks.add(KeyBlock(position))
                ' ' -> Unit // skip
                else -> throw IllegalArgumentException("char can not be '$char' position=$position")
            }
        }
    }
    return Level(
        initialRobotState = RobotState(
            position = initialRobotPosition ?: throw IllegalArgumentException("no 'r' in draw")
        ),
        nonVoidBlocks = blocks
    )
}
