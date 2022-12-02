package day02

// https://adventofcode.com/2022/day/2

import day02.ExpectedResult.*
import day02.ExpectedResult.Companion.fromCode
import day02.RSPChoice.*
import day02.RSPChoice.Companion.fromElveCode
import day02.RSPChoice.Companion.fromResponseCode
import getFileFromArgs

fun main(args: Array<String>) {
    val file = getFileFromArgs(args)

    val result1 = file.readLines()
        .filter { it.isNotBlank() }
        .map { line -> line.toRound() }
        .map { round -> round.result() }
        .sumBy { it }

    val result2 = file.readLines()
        .filter { it.isNotBlank() }
        .map { line -> line.toDesiredRound() }
        .map { desired -> desired.toRound() }
        .map { round -> round.result() }
        .sumBy { it }

    println(result1)
    println(result2)
}

private fun String.toDesiredRound() = this.split(" ").let {
    DesiredRound(fromElveCode(it[0]), fromCode(it[1]))
}

private fun String.toRound()= this.split(" ").let {
    Round(fromElveCode(it[0]), fromResponseCode(it[1]))
}

class Round(
    private val elveChoice: RSPChoice,
    private val responseChoice: RSPChoice
) {
    fun result(): Int = when {
        elveChoice == responseChoice -> 3
        responseChoice.wins(elveChoice) -> 6
        else -> 0
    } + responseChoice.value
}

class DesiredRound(
    private val elveChoice: RSPChoice,
    private val expectedResult: ExpectedResult
) {
    fun toRound(): Round {
        val responseChoice = when(expectedResult) {
            Draw -> elveChoice
            Win -> elveChoice.losesTo()
            Lose -> elveChoice.winsTo()
        }
        return Round(elveChoice, responseChoice)
    }
}

enum class ExpectedResult(val code: String) {
    Lose(code = "X"),
    Draw(code = "Y"),
    Win(code = "Z");

    companion object {
        fun fromCode(code: String) = values()
            .firstOrNull { it.code == code }
            ?: throw RuntimeException("Unknown result code $code")
    }
}

enum class RSPChoice(val elveCode:String, val responseCode: String, val value: Int) {
    Rock(elveCode = "A", responseCode = "X", value = 1),
    Paper(elveCode = "B", responseCode = "Y", value = 2),
    Scissors(elveCode = "C", responseCode = "Z", value = 3);

    fun wins(elveChoice: RSPChoice) = this.winsTo() == elveChoice

    companion object {
        fun fromElveCode(elveCode: String) = values()
            .firstOrNull { it.elveCode == elveCode }
            ?: throw RuntimeException("Unknown elveCode $elveCode")

        fun fromResponseCode(responseCode: String) = values()
            .firstOrNull { it.responseCode == responseCode }
            ?: throw RuntimeException("Unknown responseCode $responseCode")
    }
}

fun RSPChoice.losesTo(): RSPChoice = when(this) {
    Rock -> Paper
    Paper -> Scissors
    Scissors -> Rock
}

fun RSPChoice.winsTo(): RSPChoice = when(this) {
    Rock -> Scissors
    Paper -> Rock
    Scissors -> Paper
}

