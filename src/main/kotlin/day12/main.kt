package day12

// https://adventofcode.com/2022/day/12

import getNonBlankFileLines

fun main(args: Array<String>) {
    val map = HeightMapBuilder(getNonBlankFileLines(args)).build()

    println(map.sortestRouteLength())
    println(map.sortestRouteFromAnyBottomLength())
}

data class HeightMap(
    private val positions : List<List<Position>>,
    private val start: Position,
    private val end: Position,
) {
    private val maxX = positions.first().size
    private val maxY = positions.size


    fun sortestRouteLength() = sortestRoute().size - 1
    private fun sortestRoute(): Route = sortestRoute(start)

    fun sortestRouteFromAnyBottomLength() = sortestRouteFromAnyBottom().size - 1
    private fun sortestRouteFromAnyBottom(): Route {
        val bottomStartingRoutes = positions.flatMap { it.filter { it.isBottom() } }
        return sortestRoute(*bottomStartingRoutes.toTypedArray())
    }

    private fun sortestRoute(vararg initialPositions: Position): Route {
        var routes = initialPositions.map { listOf(it) }
        var newRoutes = mutableListOf<Route>()
        val visited = mutableSetOf<Position>()

        while (true) {
           routes.forEach { route ->
               val nextSteps = route.nextSteps(visited)

               if (end in nextSteps) return route + end

               nextSteps.forEach { candidate ->
                   newRoutes.add(route + candidate).also { visited.add(candidate) }
               }
           }
            routes = newRoutes
            newRoutes = mutableListOf()
        }
    }

    private fun Route.nextSteps(visited: Set<Position>): List<Position> {
        val lastPos = this.last()
        return lastPos.neighboursCoords(maxX, maxY)
            .map { positions[it.y][it.x] }
            .filterNot { it in visited }
            .filter { it.reachableFrom(lastPos) }
    }
}

typealias Route = List<Position>

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
