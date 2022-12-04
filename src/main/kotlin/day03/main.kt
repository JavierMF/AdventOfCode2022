package day03

// https://adventofcode.com/2022/day/3

import getNonBlankFileLines

fun main(args: Array<String>) {
    val rucksacks = getNonBlankFileLines(args)
        .map { Rucksack(it) }

    val result1 = rucksacks
        .map { it.priority() }
        .sumBy { it }

    println(result1)

    val result2 = rucksacks
        .toGroupRucksackList()
        .map { it.priority() }
        .sumBy { it }

    println(result2)
}


data class Rucksack(private val items: String) {
    private val compartment1: String
    private val compartment2: String

    init {
        val half = items.length / 2
        compartment1 = items.slice(0 until half)
        compartment2 = items.slice(half until items.length)
    }

    fun priority(): Int {
        val sharedItem = compartment1.toSet().intersect(compartment2.toSet()).firstOrNull()
        return sharedItem?.toPriority() ?: throw RuntimeException("No shared item")
    }

    fun itemsSet() = items.toSet()
}

data class RucksackGroup(val rucksacks: List<Rucksack>) {

    fun priority(): Int {
        val sharedItem = rucksacks
            .fold(rucksacks.first().itemsSet()) { acc, rucksack -> acc.intersect(rucksack.itemsSet()) }
            .firstOrNull()
        return sharedItem?.toPriority() ?: throw RuntimeException("No shared item")
    }
}

private fun List<Rucksack>.toGroupRucksackList(): List<RucksackGroup> {
    var rucksacksToProcess = this
    val groups = mutableListOf<RucksackGroup>()
    while (rucksacksToProcess.isNotEmpty()) {
        groups.add(RucksackGroup(rucksacksToProcess.take(3)))
        rucksacksToProcess = rucksacksToProcess.drop(3)
    }
    return groups
}

private fun Char.toPriority(): Int =
    when {
        this.isLowerCase() -> (this.toInt() - 'a'.toInt()) + 1
        this.isUpperCase() -> (this.toInt() - 'A'.toInt()) + 27
        else -> throw RuntimeException("This should not happen: $this")
    }

