package day19

// https://adventofcode.com/2022/day/19

import getNonBlankFileLines
import kotlin.system.measureTimeMillis

fun main(args: Array<String>) {
    val blueprints = getNonBlankFileLines(args)
        .map { Blueprint(it) }

    part1(blueprints)
    part2(blueprints)
}

fun part1(blueprints: List<Blueprint>) {
    val elapsed = measureTimeMillis {
        val result = blueprints.sumOf {
            println("Working in ${it.id}")
            it.id * it.mineGeodes(24).geode
        }
        println("Part 1 result: $result")
    }
    println("Executed part 1 in ${elapsed / 1000.0} seconds")
}

// It works for tests but not for actual input
fun part2(blueprints: List<Blueprint>) {
    val elapsed = measureTimeMillis {
        val result = blueprints.take(3).fold(1) { acc, blueprint ->
            println("Working in ${blueprint.id}")
            acc * blueprint.mineGeodes(32).geode
        }
        println("Part 2 result: $result")
    }
    println("Executed part 2 in ${elapsed / 1000.0} seconds")
}

class Blueprint(line: String) {
    val id: Int
    val oreRobotCost: Int
    val clayRobotCost: Int
    val obsidianRobotOreCost: Int
    val obsidianRobotClayCost: Int
    val geodeRobotOreCost: Int
    val geodeRobotObsidianCost: Int

    val maxRobotOreCost: Int
    val maxRobotClayCost: Int
    val maxRobotObsidianCost: Int

    fun mineGeodes(minutes: Int): MiningState {
        var currentStates = setOf(MiningState())
        val visited = mutableSetOf(MiningState())
        var maxGeodes: MiningState? = null

        repeat(minutes) { repetition ->
            //println("Minute ${repetition+1}, states ${currentStates.size}")
            val minutesRemaining = minutes - repetition
            val bestCurrentMaxGeodes = maxGeodes?.bestCaseGeodesIn(minutesRemaining) ?: 0

            val candidates = currentStates.flatMap { state -> nextCandidateStates(state, minutesRemaining) }
                .filter { it !in visited }
                .filter { it.bestCaseGeodesIn(minutesRemaining) >= bestCurrentMaxGeodes }

            val maxGeodesInCandidates = candidates.maxByOrNull { it.geode }
            maxGeodes = if ((maxGeodesInCandidates?.geode ?: 0) > (maxGeodes?.geode ?: 0)) maxGeodesInCandidates else maxGeodes
            visited += candidates
            currentStates = candidates.toSet()
        }
        println(maxGeodes)
        return maxGeodes!!
    }

    private fun nextCandidateStates(state: MiningState, minutesRemaining: Int): Set<MiningState> {
        val minedWithPreviousState = state.doMining()
        val newStates = mutableSetOf(minedWithPreviousState)

        if (canCreateOreRobot(state) && oreIsNeeded(state, minutesRemaining))
            newStates.add(minedWithPreviousState.createOreRobot(oreRobotCost))

        if (canCreateClayRobot(state) && clayIsNeeded(state, minutesRemaining))
            newStates.add(minedWithPreviousState.createClayRobot(clayRobotCost))

        if (canCreateObsidianRobot(state) && obsidianIsNeeded(state, minutesRemaining))
            newStates.add(minedWithPreviousState.createObsidianRobot(obsidianRobotOreCost, obsidianRobotClayCost))

        if (canCreateGeodeRobot(state))
            newStates.add(minedWithPreviousState.createGeodeRobot(geodeRobotOreCost, geodeRobotObsidianCost))

        return newStates
    }

    private fun oreIsNeeded(state: MiningState, minutesRemaining: Int) =
        (maxRobotOreCost * minutesRemaining) > (state.ore + (state.oreRobots * minutesRemaining))
    private fun clayIsNeeded(state: MiningState, minutesRemaining: Int) =
        (maxRobotClayCost * minutesRemaining) > (state.clay + (state.clayRobots * minutesRemaining))
    private fun obsidianIsNeeded(state: MiningState, minutesRemaining: Int) =
        (maxRobotObsidianCost * minutesRemaining) > (state.obsidian + (state.obsidianRobots * minutesRemaining))

    private fun canCreateOreRobot(state: MiningState) = state.isAvailable(oreRobotCost, 0, 0)
    private fun canCreateClayRobot(state: MiningState) = state.isAvailable(clayRobotCost, 0, 0)
    private fun canCreateObsidianRobot(state: MiningState) = state.isAvailable(obsidianRobotOreCost, obsidianRobotClayCost, 0 )
    private fun canCreateGeodeRobot(state: MiningState) = state.isAvailable(geodeRobotOreCost, 0, geodeRobotObsidianCost)

    init {
        val split = line.split(": ".toRegex())
        id = split[0].split(" ")[1].toInt()
        val costsSplits = split[1].split("\\. ".toRegex())
        oreRobotCost = costsSplits[0].getCost()
        clayRobotCost = costsSplits[1].getCost()
        obsidianRobotOreCost = costsSplits[2].getCost()
        obsidianRobotClayCost = costsSplits[2].getSecondCost()
        geodeRobotOreCost = costsSplits[3].getCost()
        geodeRobotObsidianCost = costsSplits[3].getSecondCost()

        maxRobotOreCost = listOf(oreRobotCost, clayRobotCost, obsidianRobotOreCost, geodeRobotOreCost).maxOf { it }
        maxRobotClayCost = obsidianRobotClayCost
        maxRobotObsidianCost = geodeRobotObsidianCost
    }

    private fun String.getCost() = this.split(" ")[4].toInt()
    private fun String.getSecondCost() = this.split(" ")[7].toInt()

    override fun toString() = "Blueprint(id=$id, oreRobotCost=$oreRobotCost, clayRobotCost=$clayRobotCost, obsidianRobotOreCost=$obsidianRobotOreCost, obsidianRobotClayCost=$obsidianRobotClayCost, geodeRobotOreCost=$geodeRobotOreCost, geodeRobotClayCost=$geodeRobotObsidianCost)"
}

data class MiningState(
    val ore: Int = 0,
    val clay: Int = 0,
    val obsidian: Int = 0,
    val geode: Int = 0,
    val oreRobots: Int = 1,
    val clayRobots: Int = 0,
    val obsidianRobots: Int = 0,
    val geodeRobots: Int = 0,
) {
    fun doMining() = this.copy(
        ore = ore + oreRobots,
        clay = clay + clayRobots,
        obsidian = obsidian + obsidianRobots,
        geode = geode + geodeRobots
    )

    fun isAvailable(oreRequest: Int, clayRequest: Int, obsidianRequest: Int) =
        ore >= oreRequest && clay >= clayRequest && obsidian >= obsidianRequest

    fun createOreRobot(oreCost: Int) = this.copy(oreRobots = oreRobots + 1, ore = ore - oreCost)
    fun createClayRobot(oreCost: Int) = this.copy(clayRobots = clayRobots + 1, ore = ore - oreCost)
    fun createObsidianRobot(oreCost: Int, clayCost: Int) = this.copy(
        obsidianRobots = obsidianRobots + 1,
        ore = ore - oreCost,
        clay = clay - clayCost,
    )
    fun createGeodeRobot(oreCost: Int, obsidianCost: Int) = this.copy(
        geodeRobots = geodeRobots + 1,
        ore = ore - oreCost,
        obsidian = obsidian - obsidianCost,
    )

    fun bestCaseGeodesIn(minutesRemaining: Int) =
        this.geode + (this.geodeRobots until (minutesRemaining + geodeRobots)).sum()
}
