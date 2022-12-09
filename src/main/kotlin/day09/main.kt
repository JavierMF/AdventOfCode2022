package day09

// https://adventofcode.com/2022/day/9

import getNonBlankFileLines
import kotlin.math.absoluteValue

fun main(args: Array<String>) {

    val moves = getNonBlankFileLines(args).map { HeadMoves(it) }

    val state = State()
    moves.forEach { state.applyMoves(it) }
    println(state.visited.size)

    val state2 = State(knots = 10)
    moves.forEach { state2.applyMoves(it) }
    println(state2.visited.size)
}

class State(knots: Int = 2) {
    private var knotsPositions = (0 until knots).map { initialPos }
    val visited = mutableSetOf(initialPos)

    private val head get() = knotsPositions.first()
    private val tail get() = knotsPositions.last()

    fun applyMoves(headMoves: HeadMoves) {
        repeat(headMoves.moves) { applyMove(headMoves.dir) }
    }

    private fun applyMove(dir: Direction) {
        val newPositions = mutableListOf(head.moveTo(dir))

        knotsPositions.slice(1 until knotsPositions.size)
            .forEach { pos ->
                val newPos = nextFollowerPos(lead = newPositions.last(), follower= pos)
                newPositions.add(newPos)
            }
        knotsPositions = newPositions
        visited.add(tail)
    }

    private fun nextFollowerPos(lead: Pos, follower: Pos): Pos {
        val nextMove = lead.diff(follower).followerMove()
        return Pos(follower.x + nextMove.x, follower.y + nextMove.y)
    }

    companion object {
        val initialPos = Pos(0, 0)
    }
}

data class Pos(val x:Int, val y:Int) {
    fun moveTo(dir: Direction) = Pos(this.x + dir.x, this.y + dir.y)
    fun diff(other: Pos) = PosDiff(this.x - other.x, this.y - other.y)
}

data class PosDiff(val x:Int, val y:Int) {
    fun followerMove() = FollowerMove(nextValue(x), nextValue(y))

    private fun nextValue(value:Int) =
        if (isDiagonalMove() && (value/2) == 0) value else value/2

    private fun isDiagonalMove(): Boolean =
        (x.absoluteValue > 1 && y.absoluteValue > 0) ||
                (x.absoluteValue > 0 && y.absoluteValue > 1)

}

data class FollowerMove(val x:Int, val y:Int)

class HeadMoves(line: String) {
    val dir: Direction
    val moves: Int

    init {
        val split = line.split(" ")
        dir = Direction.fromString(split[0])
        moves = split[1].toInt()
    }
}

enum class Direction(val x: Int, val y: Int) {
    UP(0, 1),
    DOWN(0, -1),
    RIGHT(1, 0),
    LEFT(-1, 0);

    companion object {
        fun fromString(input: String) =
            when (input) {
                "U" -> UP
                "D" -> DOWN
                "R" -> RIGHT
                "L" -> LEFT
                else -> throw RuntimeException("Unknown direction")
            }
    }
}
