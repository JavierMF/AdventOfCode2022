package day21

// https://adventofcode.com/2022/day/21

import getNonBlankFileLines
import kotlin.system.measureTimeMillis

fun main(args: Array<String>) {
    val monkeys = getNonBlankFileLines(args)
        .map { parseMonkey(it) }
        .associateBy { it.name }

    monkeys.forEach { (_, monkey) ->
        if (monkey is OperationMonkey) {
            monkey.leftMonkey = monkeys[monkey.leftName]
            monkey.rightMonkey = monkeys[monkey.rightName]
        }
    }

    val elapsed = measureTimeMillis {
        val rootMonkey = monkeys["root"]!!
        val result = rootMonkey.eval()
        println("Part 1 result: $result")
    }
    println("Executed part 1 in ${elapsed / 1000.0} seconds")

    val elapsed2 = measureTimeMillis {
        val result2 = findMatching(monkeys)
        println("Part 2 result: $result2")
    }
    println("Executed part 2 in ${elapsed2 / 1000.0} seconds")
}

fun findMatching(monkeys: Map<String, Monkey>): Long {
    val rootMonkey = monkeys["root"] as OperationMonkey
    val human = monkeys["humn"] as LeafMonkey

    var result = 0L

    human.value = result
    while (true) {
        if ((result.mod(100000)) == 0) println(result)

        human.value = result
        if (rootMonkey.match()) return result
        human.value = -result
        if (rootMonkey.match()) return -result
        result++
    }
}


interface Monkey {
    val name: String
    fun eval(): Long
    fun hasNode(node: String): Boolean
}
data class LeafMonkey(
    override val name: String,
    var value: Long
): Monkey {
    override fun eval() = value
    override fun hasNode(node: String) = node == name
}
data class OperationMonkey(
    override val name: String,
    val leftName: String,
    val rightName: String,
    val op: Operation
): Monkey {
    var leftMonkey: Monkey? = null
    var rightMonkey: Monkey? = null
    var cachedValue: Long? = null
    val canBeCached: Boolean by lazy { !hasNode("humn") }

    override fun eval(): Long {
        if (cachedValue != null) return cachedValue!!
        if (canBeCached) {
            cachedValue = computeValue()
            return cachedValue!!
        }
        return computeValue()
    }

    private fun computeValue(): Long =
        when (op) {
            Operation.ADD -> leftMonkey!!.eval() + rightMonkey!!.eval()
            Operation.MINUS -> leftMonkey!!.eval() - rightMonkey!!.eval()
            Operation.MULT -> leftMonkey!!.eval() * rightMonkey!!.eval()
            Operation.DIV -> leftMonkey!!.eval() / rightMonkey!!.eval()
        }

    override fun hasNode(node: String): Boolean =
        leftMonkey!!.hasNode(node) || rightMonkey!!.hasNode(node)

    fun compare(): Int  {
        val leftVal = leftMonkey!!.eval()
        val rightVal = rightMonkey!!.eval()
        //println("$leftVal, $rightVal")
        return leftVal.compareTo(rightVal)
    }

    fun match(): Boolean = compare() == 0

}

enum class Operation {
    ADD, MINUS, MULT, DIV;

    companion object {
        fun fromString(value: String): Operation =
            when(value) {
                "+" -> ADD
                "-" -> MINUS
                "*" -> MULT
                "/" -> DIV
                else -> throw RuntimeException("Unknown operation $value")
            }
    }
}

fun parseMonkey(line: String): Monkey {
    val split = line.split(" ")
    return if (split.size == 2)
        LeafMonkey(split[0].dropLast(1), split[1].toLong())
    else {
        OperationMonkey(
            split[0].dropLast(1),
            split[1],
            split[3],
            Operation.fromString(split[2])
        )
    }
}
