package day22

// https://adventofcode.com/2022/day/22

import day22.CellContent.EMPTY
import day22.CellContent.WALL
import getNonBlankFileLines
import kotlin.system.measureTimeMillis

fun main(args: Array<String>) {
    val jungle = Jungle(getNonBlankFileLines(args))

    val elapsed = measureTimeMillis {
        jungle.walk()
        //jungle.print()
        val result = jungle.currentCoords.let { pos -> pos.y * 1000L + pos.x * 4L + jungle.facing.value().toLong() }
        println("Part 1 result: $result")
    }
    println("Executed part 1 in ${elapsed / 1000.0} seconds")

}

class Jungle(lines: List<String>) {
    var currentCoords: Coords
    var facing: Direction = Direction.RIGHT
    val steps: List<Step>
    val positions: Map<Coords, CellContent>

    fun walk() {
        steps.forEach { step ->
            when(step) {
                is TurnStep -> facing = facing.turnTo(step.turnDirection)
                is WalkStep -> repeat(step.steps) { walkOneStep() }
            }
        }
    }

    private fun walkOneStep() {
        val candidatePos = currentCoords.moveTo(facing)
        tryToMoveTo(candidatePos)
    }

    private fun tryToMoveTo(coords: Coords) {
        when {
            positions[coords] == EMPTY -> currentCoords = coords
            coords !in positions -> tryToMoveTo(wrapAroundPosFor(coords))
        }
    }

    private fun wrapAroundPosFor(coords: Coords): Coords =
        when(facing) {
            Direction.RIGHT -> positions.coordsInRow(coords).minBy { it.x }
            Direction.LEFT -> positions.coordsInRow(coords).maxBy { it.x }
            Direction.DOWN -> positions.coordsInColumn(coords).minBy { it.y }
            Direction.UP -> positions.coordsInColumn(coords).maxBy { it.y }
        }

    fun print() {
        val maxY = positions.maxOf { it.key.y }
        val maxX = positions.maxOf { it.key.x }
        for (y in 1..maxY) {
            for (x in 1..maxX) {
                val coords = Coords(x,y)
                if (coords == currentCoords) {
                    when(facing) {
                        Direction.RIGHT -> print(">")
                        Direction.DOWN -> print("v")
                        Direction.LEFT -> print("<")
                        Direction.UP -> print("^")
                    }
                } else {
                    val cell = positions.get(coords)
                    when (cell) {
                        null -> print(" ")
                        EMPTY -> print(".")
                        WALL -> print("#")
                    }
                }
            }
            println()
        }
    }

    init {
        positions = lines.dropLast(1).flatMapIndexed { y, line ->
            line.mapIndexed { x, aChar ->
                when(aChar) {
                    '.' -> Coords(x + 1, y + 1) to EMPTY
                    '#' -> Coords(x + 1, y + 1) to WALL
                    else -> null
                }
            }.filterNotNull()
        }.toMap()
        currentCoords = positions.filter { it.key.y == 1 && it.value == EMPTY }.minBy { it.key.x }.key

        var value = 0
        val stepsList = mutableListOf<Step>()
        lines.last().forEach { aChar ->
            value = when {
                aChar.isDigit() -> (value * 10) + aChar.digitToInt()
                else -> {
                    stepsList.add(WalkStep(value))
                    stepsList.add(TurnStep(Turn.fromChar(aChar)))
                    0
                }
            }
        }
        if (value != 0) stepsList.add(WalkStep(value))
        steps = stepsList.toList()
    }
}

private fun Map<Coords, CellContent>.coordsInRow(coords: Coords): List<Coords> =
    this.filter { it.key.y == coords.y }.map { it.key }

private fun Map<Coords, CellContent>.coordsInColumn(coords: Coords): List<Coords> =
    this.filter { it.key.x == coords.x }.map { it.key }

data class Coords(val x: Int, val y : Int) {
    fun moveTo(facing: Direction): Coords = Coords(x + facing.x, y + facing.y)
}

enum class CellContent { EMPTY, WALL }
enum class Direction(val x: Int, val y:Int) {
    RIGHT(1, 0),
    DOWN(0, 1),
    LEFT(-1, 0),
    UP(0, -1);

    fun value(): Int = when(this) {
        RIGHT -> 0
        DOWN -> 1
        LEFT -> 2
        UP -> 3
    }

    fun turnTo(turn: Turn): Direction = when(turn) {
        Turn.RIGHT -> {
            when(this) {
                RIGHT -> DOWN
                DOWN -> LEFT
                LEFT -> UP
                UP -> RIGHT
            }
        }
        Turn.LEFT -> {
            when(this) {
                RIGHT -> UP
                DOWN -> RIGHT
                LEFT -> DOWN
                UP -> LEFT
            }
        }
    }
}

enum class Turn {
    RIGHT, LEFT;

    companion object {
        fun fromChar(aChar: Char) = when (aChar) {
            'R' -> RIGHT
            'L' -> LEFT
            else -> throw RuntimeException("Unsupported direction $aChar")
        }
    }
}

interface Step
data class WalkStep(val steps: Int): Step
data class TurnStep(val turnDirection: Turn): Step
