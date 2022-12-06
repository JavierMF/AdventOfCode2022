package day06

// https://adventofcode.com/2022/day/6

import getFileFromArgs

fun main(args: Array<String>) {
    val line =  getFileFromArgs(args).readLines().first()

    val result1 = diffCharsSequence(line, diffChars = 4) ?: "Not found"
    println(result1)

    val result2 = diffCharsSequence(line, diffChars = 14) ?: "Not found"
    println(result2)
}

fun diffCharsSequence(line: String, diffChars: Int): Int? {
    for (i in diffChars until line.length) {
        val candidates = line.slice(i-diffChars until i)
        if (candidates.allCharsDifferent()) {
            return i
        }
    }
    return null
}

fun String.allCharsDifferent() = this.toSet().size == this.length

