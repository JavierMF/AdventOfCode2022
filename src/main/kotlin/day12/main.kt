package day12

// https://adventofcode.com/2022/day/12

import getNonBlankFileLines

fun main(args: Array<String>) {
    val map = HeightMapBuilder(getNonBlankFileLines(args)).build()

    println(map.lengthOfSortestRoute())
    println(map.lengthOfSortestRouteFromAnyBottom())
}

data class HeightMap(
    private val positions : List<List<Position>>,
    private val start: Position,
    private val end: Position,
) {
    private val maxX = positions.first().size
    private val maxY = positions.size


    fun lengthOfSortestRoute(): Int = sortestRoute(start)

    fun lengthOfSortestRouteFromAnyBottom(): Int {
        val bottomStartingRoutes = positions.flatMap { it.filter { it.isBottom() } }
        return sortestRoute(*bottomStartingRoutes.toTypedArray())
    }

    private fun sortestRoute(vararg initialPositions: Position): Int {
        var routeHeads = initialPositions.toSet()
        var newRoutes = mutableSetOf<Position>()
        val visited = mutableSetOf<Position>()
        var length = 0

        while (routeHeads.isNotEmpty()) {
            length += 1

            routeHeads.forEach { route ->
                val nextSteps = route.nextStepsAvoidingVisited(visited)

                if (end in nextSteps) return length

                newRoutes += nextSteps
                visited += nextSteps
           }
            routeHeads = newRoutes
            newRoutes = mutableSetOf()
        }
        throw RuntimeException("End node not found")
    }

    private fun Position.nextStepsAvoidingVisited(visited: Set<Position>) =
        this.neighboursCoords(maxX, maxY)
            .map { positions[it.y][it.x] }
            .filterNot { it in visited }
            .filter { it.reachableFrom(this) }
            .toSet()
}

class HeightMapBuilder(lines: List<String>) {
    private val positions : MutableList<List<Position?>> = mutableListOf()
    private var start: Position? = null
    private var end: Position? = null

    init { lines.forEach { addRow(it) } }

    private fun addRow(line: String) {
        val y = positions.size
        val linesPositions = line.mapIndexed{ x, c ->
            when(c) {
                'S' -> { start = Position(x, y, 0); start }
                'E' -> { end = Position(x, y, 'z'-'a'); end }
                else -> Position(x, y, c-'a')
            }
        }
        positions.add(linesPositions)
    }

    fun build() = HeightMap(
        positions = positions.map { it.map { it!! }.toList() },
        start = start!!,
        end = end!!
    )

    override fun toString() = "HeightmapBuilder(positions=$positions, start=$start, end=$end)"
}
data class Coords(val x: Int, val y: Int)

data class Position(
    val x: Int,
    val y: Int,
    val height: Int,
) {
    fun neighboursCoords(maxX: Int, maxY: Int): List<Coords> = listOf(
        Coords(x - 1, y),
        Coords(x + 1, y),
        Coords(x, y - 1),
        Coords(x, y + 1)
    ).filterNot { it.x < 0 || it.y < 0 || it.x >= maxX || it.y >= maxY }

    fun reachableFrom(lastPos: Position) = height <= (lastPos.height + 1)
    fun isBottom() = height == 0
}
