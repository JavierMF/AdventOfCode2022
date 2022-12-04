package day04

// https://adventofcode.com/2022/day/4

import getNonBlankFileLines

fun main(args: Array<String>) {
    val elvePairs = getNonBlankFileLines(args)
        .map { lines ->
            val assignments = lines.split(",")
            ElvesPair(Assignment(assignments[0]), Assignment(assignments[1]))
         }

    val result1 = elvePairs.count { it.assignmentFullyOverlap() }
    val result2 = elvePairs.count { !it.assignmentDontOverlap() }

    println(result1)
    println(result2)
}

class ElvesPair(val elve1: Assignment, val elve2: Assignment) {
    fun assignmentFullyOverlap() = elve1.containedIn(elve2) || elve2.containedIn(elve1)

    fun assignmentDontOverlap() = elve1.dontOverlapWith(elve2)
}

class Assignment(assignmentString: String) {
    private val lowEnd: Int
    private val highEnd: Int

    init {
        assignmentString.split("-").let {
            lowEnd = it[0].toInt()
            highEnd = it[1].toInt()
        }
    }

    fun containedIn(other: Assignment) = this.lowEnd >= other.lowEnd && this.highEnd <= other.highEnd

    fun dontOverlapWith(other: Assignment) = this.lowEnd > other.highEnd || other.lowEnd > this.highEnd

}
