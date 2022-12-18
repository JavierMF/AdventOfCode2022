package day18

// https://adventofcode.com/2022/day/18

import getNonBlankFileLines
import kotlin.system.measureTimeMillis

fun main(args: Array<String>) {

    val rock = Rock(getNonBlankFileLines(args))

    val elapsed = measureTimeMillis {
        val result = rock.sides()
        println("Part 1 result: $result")
    }
    println("Executed part 1 in ${elapsed / 1000.0} seconds")

    val elapsed2 = measureTimeMillis {
        val result = rock.sidesWithoutInterior()
        println("Part 2 result: $result")
    }
    println("Executed part 2 in ${elapsed2 / 1000.0} seconds")
}

class Rock(lines: List<String>) {
    private val cubes:Set<Cube> = lines.map {
        it.split(",").let {
            Cube(it[0].toInt(), it[1].toInt(), it[2].toInt())
        }
    }.toSet()

    private val maxX = cubes.maxOf { it.x }
    private val minX = cubes.minOf { it.x }
    private val maxY = cubes.maxOf { it.y }
    private val minY = cubes.minOf { it.y }
    private val maxZ = cubes.maxOf { it.z }
    private val minZ = cubes.minOf { it.z }

    fun sides(): Long = cubes.sumOf { freeSides(it) }

    fun sidesWithoutInterior(): Long = cubes.sumOf { exteriorFreeSides(it) }

    private fun freeSides(cube: Cube): Long = cube
        .neighbours()
        .count{ !cubes.contains(it) }.toLong()

    private fun exteriorFreeSides(cube: Cube): Long = cube
        .neighbours()
        .count{ !cubes.contains(it) && isExterior(it) }.toLong()

    private fun isExterior(cube: Cube): Boolean {
        var pendingToVisit = setOf(cube)
        val alreadyVisited = mutableSetOf(cube)

        while (pendingToVisit.isNotEmpty()) {
            val candidates = pendingToVisit.flatMap { it.neighbours() }
                .filter { !cubes.contains(it) && ! alreadyVisited.contains(it) }
                .toSet()

            if (candidates.any { it.outsideBoundaries() }) return true

            pendingToVisit = candidates
            alreadyVisited += candidates
        }

        return false
    }

    private fun Cube.outsideBoundaries(): Boolean =
        this.x > maxX || this.x < minX ||
                this.y > maxY || this.y < minY ||
                this.z > maxZ || this.z < minZ

}

data class Cube(val x:Int, val y:Int, val z:Int) {
    fun neighbours(): Set<Cube> = neighboursDiffs
        .map { Cube(x + it.x, y + it.y, z + it.z) }
        .toSet()

    companion object {
        val neighboursDiffs = setOf(
            Cube(0, 0, 1),
            Cube(0, 0, -1),
            Cube(0, 1, 0),
            Cube(0, -1, 0),
            Cube(1, 0, 0),
            Cube(-1, 0, 0),
        )
    }
}
