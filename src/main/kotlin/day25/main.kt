package day25

// https://adventofcode.com/2022/day/25

import getNonBlankFileLines
import java.math.BigDecimal
import kotlin.math.pow
import kotlin.system.measureTimeMillis

fun main(args: Array<String>) {
    val numbers = getNonBlankFileLines(args)
        .map { it.fromSNAFU() }

    println("Part 1 result: ${numbers.sum().toSNAFU()}")
}

private fun Long.toSNAFU(): String {
    if (this == 0L) return ""

    val rem = this % 5
    return when(rem) {
        0L, 1L, 2L -> (this / 5).toSNAFU() + "$rem"
        3L -> (1 + this/5).toSNAFU() + "="
        4L -> (1 + this/5).toSNAFU() + "-"
        else -> throw RuntimeException("This is really weird")
    }
}

private fun String.fromSNAFU(): Long {
    return this.reversed().mapIndexed { index, c ->
        (5.0.pow(index.toDouble()) * c.fromSNAFU()).toLong()
    }.sum()
}

private fun Char.fromSNAFU(): Int =
    when(this) {
        '-' -> -1
        '=' -> -2
        else -> this.digitToInt()
    }
