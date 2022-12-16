package day16

// https://adventofcode.com/2022/day/16

import getNonBlankFileLines
import kotlin.system.measureTimeMillis

fun main(args: Array<String>) {

    val valves = getNonBlankFileLines(args).map { Valve(it) }
    val cave = Cave(valves)
    val elapsed = measureTimeMillis {
        val result = cave.routes().maxBy { it.released }
        println(result.released)
        println(result.visited.map { it.name }.joinToString("->"))
    }
    println("Executed part 1 in ${elapsed / 1000.0} seconds")

//    println("Part 1: $result")

}

data class Route(
    val current: Valve,
    val released: Int,
    val opened: Set<Valve>,
    val visited: List<Valve> = emptyList()
) {
    fun allOpen(targets: Set<Valve>): Boolean = opened == targets

    fun justFlow(): Route = this.copy(released = this.released + flowOpened())

    private fun flowOpened() = opened.sumOf { it.rate }

    fun canBeOpened(): Boolean = current.hasFlow() && !opened.contains(current)

    fun toNextValves(valves: Map<String, Valve>): Set<Route> = current.leadTo
        .map { valves[it]!! }
        .map { this.justFlow().copy(current = it, visited = this.visited + it) }
        .toSet()

    fun withOpenValve(): Route = this.justFlow().copy(opened = this.opened + current, visited = this.visited+current)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Route

        if (current != other.current) return false
        if (released != other.released) return false
        if (opened != other.opened) return false

        return true
    }

    override fun hashCode(): Int {
        var result = current.hashCode()
        result = 31 * result + released
        result = 31 * result + opened.hashCode()
        return result
    }

    fun existsBetterRoute(candidates: Set<Route>): Boolean =
        candidates.any { it.isBetterRouteThan(this) }

    private fun isBetterRouteThan(other: Route) =
        this.current == other.current &&
                this.opened.containsAll(other.opened) &&
                this.released > other.released

}

class Cave(valvesList: List<Valve>) {
    private val valves = valvesList.associateBy { it.name }
    private val targets = valvesList.filter { it.rate > 0 }.toSet()

    fun routes(): Set<Route> {
        val initialRoute = Route(current = valves["AA"]!!, released = 0, opened = emptySet())
        var onGoingRoutes = setOf(initialRoute)
        var minute = 1
        var beenTere = setOf<Route>()
        while (minute <= 30){
            println("Minute $minute, beenThere ${beenTere.size}, routes ${onGoingRoutes.size}")
            val candidates = onGoingRoutes.flatMap { nextStep(it) }.toSet()

            val nextRoutes = candidates
                .filterNot { it.existsBetterRoute(candidates) }
                .filterNot { it.existsBetterRoute(beenTere) }
                .toSet()

            beenTere = beenTere
                .filterNot { it.existsBetterRoute(candidates) }
                .toSet() + nextRoutes

            onGoingRoutes = nextRoutes
            minute++
        }
        return onGoingRoutes
    }

    private fun nextStep(route: Route): Set<Route> =
        when {
            route.allOpen(targets) -> setOf(route.justFlow())
            route.canBeOpened() -> route.toNextValves(valves) + route.withOpenValve()
            else -> route.toNextValves(valves)
        }
}

class Valve(line: String) {
    val name: String
    val rate: Int
    val leadTo: Set<String>

    init {
        val values = rowPattern.find(line)?.groupValues ?: throw RuntimeException("Weird lines found: $line")
        name = values[1]
        rate = values[2].toInt()
        leadTo = values[3].split(", ".toRegex()).toSet()
    }

    companion object {
        val rowPattern = """Valve (.+) has flow rate=(\d+); tunnel[s]? lead[s]? to valve[s]? (.+)$""".toRegex()
    }

    override fun toString() = "Valve(valve='$name', rate=$rate, leadTo=$leadTo)"
    fun hasFlow() = rate > 0

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Valve

        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int = name.hashCode()

}
