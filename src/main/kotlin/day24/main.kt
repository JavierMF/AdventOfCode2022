package day24

// https://adventofcode.com/2022/day/24

import getNonBlankFileLines
import kotlin.system.measureTimeMillis

fun main(args: Array<String>) {
    val valley = Valley(getNonBlankFileLines(args))

    val elapsed = measureTimeMillis {
        valley.findExit()
        val result = valley.minutes
        println("Part 1 result: $result")
    }
    println("Executed part 1 in ${elapsed / 1000.0} seconds")
}

private fun List<Blizzard>.toPositions(): Set<Coords> = this.map { it.currentPos }.toSet()

class Valley(lines: List<String>) {
    var minutes = 0L
    val initialBlizzards: List<Blizzard>
    val maxX: Int
    val maxY: Int
    val targetPos: Coords

    fun findExit() {
        var currentPositions = setOf(Coords(1, 0))
        var blizzards = initialBlizzards
        while (currentPositions.isNotEmpty() && !currentPositions.targetStateReached()) {
            blizzards = blizzards.map { it.moveInBoundaries(maxX, maxY)}
            val blizzardsPositions = blizzards.toPositions()
            currentPositions = currentPositions
                .flatMap { coords -> coords.nextCoords(blizzardsPositions, maxX, maxY) }
                .toSet()
            minutes += 1
            //println("Minute: $minutes")
            //print(blizzards, currentPositions.first())
        }
    }

    fun print(blizards: List<Blizzard>, currentPos: Coords) {
        repeat(maxX + 2) { print("#")}
        println()
        for (y in 1..maxY) {
            print("#")
            for (x in 1..maxX) {
                val coords = Coords(x, y)
                if (currentPos == coords) { print("E"); continue }
                val contents = blizards.positionContents(coords)
                when {
                    contents.isEmpty() -> print(".")
                    contents.size > 1 -> print(contents.size)
                    contents.first().direction == Direction.NORTH -> print("^")
                    contents.first().direction == Direction.SOUTH -> print("v")
                    contents.first().direction == Direction.EAST -> print(">")
                    contents.first().direction == Direction.WEST -> print("<")
                }
            }
            println("#")
        }
        repeat(maxX + 2) { print("#")}
        println()
    }
    private fun List<Blizzard>.positionContents(coords: Coords) =
        this.filter { it.currentPos == coords }

    private fun Set<Coords>.targetStateReached(): Boolean =
        this.any{ it == targetPos }

    private fun Char.toDirection(): Direction? =
        when(this) {
            '^' -> Direction.NORTH
            '>' -> Direction.EAST
            '<' -> Direction.WEST
            'v' -> Direction.SOUTH
            else -> null
        }

    init {
        initialBlizzards = lines.flatMapIndexed { y, line ->
            line.mapIndexed { x, aChar ->
                aChar.toDirection()?.let {
                    Blizzard(Coords(x, y), it)
                }
            }.filterNotNull()
        }
        maxY = lines.size - 2
        maxX = lines.first().length - 2
        targetPos = Coords(maxX, maxY + 1)
    }
}

data class Blizzard(
    val currentPos: Coords,
    val direction: Direction
) {
    fun moveInBoundaries(maxX: Int, maxY: Int): Blizzard {
        var nextPos = currentPos.movedTo(direction.moveDiff)
        nextPos = when {
            nextPos.x == 0 -> Coords(maxX, nextPos.y)
            nextPos.y == 0 -> Coords(nextPos.x, maxY)
            nextPos.x > maxX -> Coords(1, nextPos.y)
            nextPos.y > maxY && nextPos.x != maxX -> Coords(nextPos.x, 1)
            else -> nextPos

        }
        val b = Blizzard(nextPos, direction)
        return b
    }
}

enum class Direction(val moveDiff: Coords) {
    NORTH(moveDiff = Coords(0, -1)),
    SOUTH(moveDiff = Coords(0, 1)),
    WEST(moveDiff = Coords(-1, 0)),
    EAST(moveDiff = Coords(1, 0)),
}

data class Coords(val x: Int, val y: Int) {
    fun movedTo(move: Coords): Coords = Coords(x + move.x, y + move.y)

    fun movementsInBoundaries(maxX: Int, maxY: Int): Set<Coords> =
        (Direction.values().map { this.movedTo(it.moveDiff) } + this)
            .filter { it.inBoundaries(maxX, maxY) }
            .toSet()

    private fun inBoundaries(maxX: Int, maxY: Int): Boolean =
        (this.x in 1 .. maxX && this.y in 1 .. maxY)
                || (this.y > maxY && this.x == maxX)
                || (this.y == 0 && this.x == 1)

    fun nextCoords(blizzardsPositions: Set<Coords>, maxX: Int, maxY: Int) =
        this.movementsInBoundaries(maxX, maxY)
            .filter { it !in blizzardsPositions }
            .toSet()

}
