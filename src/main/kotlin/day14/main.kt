package day14

// https://adventofcode.com/2022/day/14

import getNonBlankFileLines

fun main(args: Array<String>) {
    val cave = Cave(getNonBlankFileLines(args))
    println(cave.dropSand())

    val cave2 = Cave(getNonBlankFileLines(args))
    println(cave2.dropSand2())
}

class Cave(rockLines: List<String>) {
    private val grid: MutableSet<Coords> = mutableSetOf()
    private val lowestRock: Int
    private val initialSandPosition = Pair(500, 0)

    fun dropSand2(): Int {
        var sandDropped = 0
        while (initialSandPosition.isFree2()) {
            var currentSandPos = initialSandPosition
            sandDropped += 1
            do {
                val nextSandPos = currentSandPos.getNextSandPost { it.isFree2() }
                val moved = nextSandPos != currentSandPos
                currentSandPos = nextSandPos
            } while (moved)
            grid.add(currentSandPos)
        }
        return sandDropped
    }

    private fun Coords.isFree2() = !grid.contains(this) && this.second < (lowestRock + 2)

    fun dropSand(): Int {
        var sandDropped = 0
        var sandLost = false

        while (!sandLost) {
            var currentSandPos = initialSandPosition
            sandDropped += 1
            do {
                val nextSandPos = currentSandPos.getNextSandPost { it.isFree() }
                val moved = nextSandPos != currentSandPos
                currentSandPos = nextSandPos
            } while (moved && !currentSandPos.isLost())
            grid.add(currentSandPos)
            sandLost = currentSandPos.isLost()
        }
        return sandDropped - 1
    }

    private fun Coords.getNextSandPost(isFree: (Coords) -> Boolean): Coords =
        when {
            isFree(this.down()) -> this.down()
            isFree(this.bottomLeft()) -> this.bottomLeft()
            isFree(this.bottomRight())-> this.bottomRight()
            else -> this
        }

    private fun Coords.isFree() = !grid.contains(this)

    private fun Coords.down() = Pair(this.first, this.second + 1)
    private fun Coords.bottomLeft() = Pair(this.first - 1, this.second + 1)
    private fun Coords.bottomRight() = Pair(this.first + 1, this.second + 1)
    private fun Coords.isLost() = this.second >= lowestRock

    init {
        rockLines.forEach { rockLine ->
            val rockCorners = rockLine.split(" -> ".toRegex()).map { it.toCoords() }
            for (i in 1 until rockCorners.size) {
                buildRockLinePositions(rockCorners[i-1], rockCorners[i])
                    .forEach { grid.add(it) }
            }
        }
        lowestRock = grid.map { it.second }.max()
    }

    private fun buildRockLinePositions(from: Coords, to: Coords): Set<Coords> {
        val positions = mutableSetOf<Coords>()
        val diffX = to.first - from.first
        val diffY = to.second - from.second
        for (i in diffX.toZeroRange()) positions.add(Pair(from.first + i, from.second))
        for (i in diffY.toZeroRange()) positions.add(Pair(from.first, from.second + i))
        return positions
    }

    private fun String.toCoords(): Coords = this.split(",").let { Pair(it[0].toInt(), it[1].toInt()) }
    private fun Int.toZeroRange() = if (this <= 0) this..0 else 0..this
}

typealias Coords = Pair<Int, Int>
