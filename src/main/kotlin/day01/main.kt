package day01

// https://adventofcode.com/2022/day/1

import getFileFromArgs

fun main(args: Array<String>) {
    val file = getFileFromArgs(args)

    val elves = file.readText()
        .replace("\n\n","|")
        .replace("\n",":")
        .split("|")


    val caloriesByElve = elves.map { elve ->
        elve.split(":")
            .map { it.toIntOrNull() }
            .sumBy { it ?: 0 }
    }

    val maxThree = caloriesByElve.sortedDescending().subList(0, 3).sum()

    println(caloriesByElve.maxOrNull())
    println(maxThree)
}

