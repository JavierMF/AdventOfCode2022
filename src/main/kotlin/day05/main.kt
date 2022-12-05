package day05

// https://adventofcode.com/2022/day/5

import getFileFromArgs

fun main(args: Array<String>) {
    val lines =  getFileFromArgs(args).readLines()
    val stackLines = mutableListOf<String>()
    val movements = mutableListOf<Movement>()

    var stackFinished = false
    lines.forEach { line ->
        if (!stackFinished) {
            if (line.isNotEmpty()) stackLines.add(line)
            else stackFinished = true
        } else {
            if (lines.isNotEmpty()) movements.add(Movement(line))
        }
    }

    val dock1 = Dock(stackLines)
    movements.forEach { movement -> dock1.applyMovement1(movement) }
    println(dock1.stacksTopsString())

    val dock2 = Dock(stackLines)
    movements.forEach { movement -> dock2.applyMovement2(movement) }
    println(dock2.stacksTopsString())
}

class Dock(stackLines: List<String>) {
    private val stacks = mutableMapOf <Int, MutableList<Char>>()

    init {
        val numberOfStacks = stackLines.last().count { !it.isWhitespace() }
        for (i in 1 until (numberOfStacks+1)) stacks[i] = mutableListOf()

        stackLines.dropLast(1).forEach { line ->
            var pos = 1
            for (i in (1 until numberOfStacks + 1)) {
                val item = line[pos]
                if (!item.isWhitespace()) stacks[i]?.add(item)
                pos += 4
            }
        }
        stacks.forEach { (_, stack) -> stack.reverse() }
    }

    override fun toString(): String {
        return "Dock(stacks=$stacks)"
    }

    fun stacksTopsString(): String = stacks.map { (_, stack) -> stack.last() }.joinToString("")

    fun applyMovement1(movement: Movement) {
        repeat(movement.movements) {
            val elementToMove = stacks[movement.source]?.removeLast() ?: throw RuntimeException("Element not found for stack ${movement.source}")
            stacks[movement.target]?.add(elementToMove)
        }
    }

    fun applyMovement2(movement: Movement) {
        val elementsToMove = stacks[movement.source]?.takeLast(movement.movements) ?: throw RuntimeException("Element not found for stack ${movement.source}")
        stacks[movement.source] = stacks[movement.source]?.dropLast(movement.movements)?.toMutableList()  ?: throw RuntimeException("Element not found for stack ${movement.source}")
        stacks[movement.target]?.addAll(elementsToMove)
    }
}

class Movement(movementLine: String) {
    val source: Int
    val target: Int
    val movements: Int

    init {
        val result = pattern.find(movementLine)
        val (m, s, t) = result!!.destructured
        movements = m.toInt()
        source = s.toInt()
        target = t.toInt()
    }

    override fun toString(): String {
        return "Movement(source=$source, target=$target, movements=$movements)"
    }

    companion object {
        private val pattern = """move (\d+) from (\d+) to (\d+)""".toRegex()
    }
}
