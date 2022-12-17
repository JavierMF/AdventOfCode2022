package day17

// https://adventofcode.com/2022/day/17

import day17.LateralMovement.LEFT
import day17.LateralMovement.RIGHT
import getNonBlankFileLines
import kotlin.system.measureTimeMillis

fun main(args: Array<String>) {

    val wind = Wind(getNonBlankFileLines(args).first())

    val cave = Cave(wind, RockFlow())
    val elapsed = measureTimeMillis {
        cave.throwRocks(2022)
        //cave.print()
        val result = cave.highestRockPos
        println("Part 1 result: $result")
    }
    println("Executed part 1 in ${elapsed / 1000.0} seconds")
}

class Cave(
    private val wind: Wind,
    private val rockFlow: RockFlow
) {
    private val rocks = mutableSetOf<Coords>()
    var highestRockPos = 0

    fun throwRocks(rocksToThrow: Int) {
        repeat(rocksToThrow) {
            throwRock(rockFlow.next())
        }
    }

    private fun throwRock(rock: Rock) {
        val initialCoords = Coords(3, highestRockPos + 4)
        val fallingRock = FallingRock(rock, initialCoords, rocks)

        do {
            fallingRock.moveTo(wind.next())
        } while (fallingRock.wasMovedDown)

        updateHighestRockPos(fallingRock.coords)
        rocks += fallingRock.coords
    }

    fun updateHighestRockPos(coords: Set<Coords>) {
        val candidate = coords.maxOf { it.y }
        if (candidate > highestRockPos) highestRockPos = candidate
    }

    fun print() {
        for (y in highestRockPos downTo 1) {
            for (x in 1 .. 7) {
                val aChar = if (rocks.contains(Coords(x,y))) '#' else '.'
                print(aChar)
            }
            println()
        }
    }
}

class FallingRock(
    rock: Rock,
    initialCoords: Coords,
    private val currentRocks: Set<Coords>
) {
    private var rockPos: RockPosition = rock.inCoords(initialCoords)
    var wasMovedDown = false

    fun moveTo(next: LateralMovement) {
        val initialPos = rockPos
        rockPos = rockPos.moveLateral(next).moveDown()
        wasMovedDown = rockPos.lowestY() < initialPos.lowestY()
    }

    private fun RockPosition.moveLateral(next: LateralMovement) = moveWithDiff(Coords(next.move, 0))
    private fun RockPosition.moveDown() = moveWithDiff(Coords(0, -1))

    private fun RockPosition.moveWithDiff(move: Coords): RockPosition {
        val candidatePos = this.moveTo(move)
        return if (candidatePos.canMoveInside(currentRocks)) candidatePos else this
    }

    val coords: Set<Coords> by lazy {rockPos.coords}

}

data class RockPosition(val coords: Set<Coords>) {

    fun moveTo(move: Coords): RockPosition =
        coords
            .map { Coords(it.x + move.x, it.y + move.y) }
            .toSet()
            .let { RockPosition(it) }

    fun canMoveInside(currentRocks: Set<Coords>) = coords.all{ it.inBoundaries() && it.doesNotCollideWith(currentRocks)}

    private fun Coords.doesNotCollideWith(currentRocks: Set<Coords>) = !currentRocks.contains(this)
    private fun Coords.inBoundaries() =  x in 1 .. 7 && y >= 1
    fun lowestY(): Int = coords.minOf { it.y }
}


class Wind(line: String) {
    private var currentPos = 0
    private val movements = line.mapNotNull {
        when (it) {
            '<' -> LEFT
            '>' -> RIGHT
            else -> throw RuntimeException("Unknonw char $it")
        }
    }

    fun next(): LateralMovement = movements[currentPos].also { currentPos = (currentPos + 1) % movements.size }
}

class RockFlow {
    private var currentPos = 0
    private val rocksSequence = listOf(horizontalLine, cross, brokenLine, verticalLine, square)
    fun next(): Rock =
        rocksSequence[currentPos].also { currentPos = (currentPos + 1) % rocksSequence.size }
}

data class Rock (
    val shape: Set<Coords>
) {
    fun inCoords(coords: Coords) =
        shape
            .map { Coords(it.x + coords.x, it.y + coords.y) }
            .toSet()
            .let { RockPosition(it) }
}

val horizontalLine = Rock(
    shape = setOf(
        Coords(0,0),
        Coords(1, 0),
        Coords(2, 0),
        Coords(3, 0)
    )
)
val cross = Rock (
    shape = setOf(
        Coords(0,1),
        Coords(1, 2),
        Coords(1, 1),
        Coords(1, 0),
        Coords(2, 1)
    )
)
val brokenLine = Rock (
    shape = setOf(
        Coords(0,0),
        Coords(1, 0),
        Coords(2, 0),
        Coords(2, 1),
        Coords(2, 2)
    )
)
val verticalLine = Rock (
    shape = setOf(
        Coords(0,0),
        Coords(0, 1),
        Coords(0, 2),
        Coords(0, 3)
    )
)
val square = Rock (
    shape = setOf(
        Coords(0,0),
        Coords(0, 1),
        Coords(1, 0),
        Coords(1, 1)
    )
)

data class Coords(val x:Int, val y:Int)

enum class LateralMovement(val move:Int) {
    LEFT(-1), RIGHT(1)
}
