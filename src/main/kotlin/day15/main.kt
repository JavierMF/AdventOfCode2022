package day15

// https://adventofcode.com/2022/day/15

import getNonBlankFileLines
import kotlin.system.measureTimeMillis

fun main(args: Array<String>) {
    val isDemo = false;
    val constraints = if (isDemo) Pair(10, 20) else Pair(2000000, 4000000)

    val rows = getNonBlankFileLines(args).map { Row(it) }.toSet()
    val cave = Cave(rows)

    val result =  cave.freeCellsInRow(constraints.first)
    println("Part 1: $result")

    val elapsed = measureTimeMillis {
        val unknownPos = cave.findUnknownPosition(constraints.second)
        val result2 = (unknownPos.x * 4000000L) + unknownPos.y
        println("Part 2: $result2")
    }
    println("Executed part 2 in ${elapsed / 1000.0} seconds")
}

class Cave(private val posSet: Set<Row>) {
    private val minX = posSet.minOf { it.sensor.x }
    private val maxX = posSet.maxOf { it.sensor.x }
    private val maxDistance = posSet.maxOf { it.distance }
    private val occoupied = posSet.flatMap { it.positions() }.toSet()

    fun freeCellsInRow(rowNumber: Int): Int =
        ((minX - maxDistance)..(maxX + maxDistance))
            .map { isFree(Coords(it, rowNumber)) }
            .count { it }

    fun findUnknownPosition(max: Int): Coords {
        posSet.forEach {
            val result = it.borderCoords(max)
                .firstOrNull { coord -> !occoupied.contains(coord) && isUnknown(coord) }
            if (result != null) return result
        }
        throw RuntimeException("Not found")
    }

    private fun isUnknown(coords: Coords): Boolean = !posSet.any { it.isFree(coords) }

    private fun isFree(coords: Coords): Boolean =
        posSet.any { !occoupied.contains(coords) && it.isFree(coords) }
}
enum class Content(val aChar: Char) { SENSOR('S'), BEACON('B'), EMPTY('#'), UNKNOWN('.') }

data class Position(val coords: Coords, val content: Content)

class Row(line: String) {
    val sensor: Coords
    private val beacon: Coords
    val distance: Int

    init {
        val values = rowPattern.find(line)?.groupValues ?: throw RuntimeException("Weird lines found: $line")
        sensor = Coords(x = values[1].toInt(), y = values[2].toInt())
        beacon = Coords(x = values[3].toInt(), y = values[4].toInt())
        distance = sensor.distanceTo(beacon)
    }

    fun positions() = setOf(sensor,  beacon)

    fun isFree(coords: Coords) = this.sensor.distanceTo(coords) <= distance

    fun borderCoords(max: Int): Set<Coords> {
        val coords = mutableSetOf<Coords>()
        for (j in 0..distance + 1) {
            val i = (distance + 1) - j
            with(this.sensor) {
                coords +=  setOf(
                    Coords(this.x + i, this.y + j),
                    Coords(this.x - i, this.y + j),
                    Coords(this.x + i, this.y - j),
                    Coords(this.x - i, this.y - j)
                ).filter { it.inBoundaries(max) }
            }
        }
        return coords
    }

    companion object {
        val rowPattern = """Sensor at x=([-\d]+), y=([-\d]+): closest beacon is at x=([-\d]+), y=([-\d]+)$""".toRegex()
    }
}

data class Coords(val x: Int, val y: Int) {
    fun distanceTo(other: Coords): Int = distanceAxis(other.x, this.x) + distanceAxis(other.y, this.y)

    private fun distanceAxis(a: Int, b: Int) = if (a > b) a - b else b - a

    fun inDistance(distance: Int): Set<Coords> {
        val coords = mutableSetOf<Coords>()
        for (i in 0..distance) {
            for (j in 0..distance) {
                if ((i + j) > distance || (i == 0 && j == 0)) continue
                coords += Coords(this.x + i, this.y + j)
                coords += Coords(this.x - i, this.y + j)
                coords += Coords(this.x + i, this.y - j)
                coords += Coords(this.x - i, this.y - j)
            }
        }
        return coords
    }

    fun inBoundaries(max: Int): Boolean = this.x in 0..max && this.y in 0..max
}
