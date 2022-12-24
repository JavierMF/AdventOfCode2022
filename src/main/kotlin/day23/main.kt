package day23

// https://adventofcode.com/2022/day/23

import getNonBlankFileLines
import kotlin.system.exitProcess
import kotlin.system.measureTimeMillis

fun main(args: Array<String>) {
    val jungle = Plantation(getNonBlankFileLines(args))

    val elapsed = measureTimeMillis {
        jungle.move(10000)
        jungle.print()
        val result = jungle.emptySpaces()
        println("Part 1 result: $result")
    }
    println("Executed part 1 in ${elapsed / 1000.0} seconds")
}

class Plantation(lines: List<String>) {
    var elves: MutableSet<Coords>

    fun move(rounds: Int) {
        repeat(rounds) { moveIteration(it) }
    }

    private fun moveIteration(round: Int) {
        val candidates = elves.map { it to it.candidateMove(round) }.toList()
        val nextElves = candidates.map { candidate ->
            if (candidates.count { it.second == candidate.second } == 1)
                candidate.second
            else
                candidate.first
        }.toMutableSet()
        if (nextElves == elves) {
            println(round + 1)
            exitProcess(0)
        }
        elves = nextElves
    }

    fun Coords.candidateMove(round: Int): Coords {
        val directions = Movement.forRound(round)
        if (directions.all { this.canMoveTo(it) }) return this

        val direction = directions.firstOrNull { this.canMoveTo(it) }
        return direction?.let { this.movedTo(direction.dir) } ?: this
    }

    private fun Coords.canMoveTo(movement: Movement): Boolean =
        movement.candidates.all { this.movedTo(it) !in elves }

    fun emptySpaces(): Int {
        val maxY = elves.maxOf { it.y }
        val maxX = elves.maxOf { it.x }
        val minY = elves.minOf { it.y }
        val minX = elves.minOf { it.x }
        return (((maxX - minX) + 1) * ((maxY - minY) +1)) - elves.size
    }

    init {
        elves = lines.flatMapIndexed { y, line ->
            line.mapIndexed { x, aChar -> if (aChar == '#') Coords(x, y) else null }
                .filterNotNull()
        }.toMutableSet()
    }

    fun print() {
        val maxY = elves.maxOf { it.y }
        val maxX = elves.maxOf { it.x }
        val minY = elves.minOf { it.y }
        val minX = elves.minOf { it.x }
        for (y in minY..maxY) {
            for (x in minX..maxX) {
                if (elves.contains(Coords(x, y))) print("#")
                else print (".")
            }
            println()
        }
    }
}


data class Coords(val x: Int, val y: Int) {
    fun movedTo(move: Coords): Coords = Coords(x + move.x, y + move.y)
}

enum class Movement(val dir: Coords, val candidates: Set<Coords>) {
    NORTH(
        dir = Coords(0, -1),
        candidates = setOf(
            Coords(-1, -1),
            Coords(0, -1),
            Coords(1, -1),
        )
    ),
    EAST(
        dir = Coords(1, 0),
        candidates = setOf(
            Coords(1, -1),
            Coords(1, 0),
            Coords(1, 1),
        )
    ),
    SOUTH(
        dir = Coords(0, 1),
        candidates = setOf(
            Coords(-1, 1),
            Coords(0, 1),
            Coords(1, 1),
        )
    ),
    WEST(
        dir = Coords(-1, 0),
        candidates = setOf(
            Coords(-1, -1),
            Coords(-1, 0),
            Coords(-1, 1),
        )
    );

    companion object {
        val movementOrder = listOf(
            listOf(NORTH, SOUTH, WEST, EAST),
            listOf(SOUTH, WEST, EAST, NORTH),
            listOf(WEST, EAST, NORTH, SOUTH),
            listOf(EAST, NORTH, SOUTH, WEST),
        )
        fun forRound(round: Int) = movementOrder[round % movementOrder.size]
    }
}
