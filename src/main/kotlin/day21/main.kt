package day21

// https://adventofcode.com/2022/day/21

import day21.Operation.*
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
    val left = rootMonkey.leftMonkey!!
    val right = rootMonkey.rightMonkey!!

    return if (left.hasHumanNode())
        left.valueNeeded(right.eval())
     else
        right.valueNeeded(left.eval())
}


interface Monkey {
    val name: String
    fun eval(): Long
    fun hasNode(node: String): Boolean
    fun hasHumanNode() = hasNode("humn")
    fun valueNeeded(needed: Long): Long
}

data class LeafMonkey(
    override val name: String,
    var value: Long
): Monkey {
    override fun eval() = value
    override fun hasNode(node: String) = node == name
    override fun valueNeeded(needed: Long) = needed
}

data class OperationMonkey(
    override val name: String,
    val leftName: String,
    val rightName: String,
    val op: Operation
): Monkey {
    var leftMonkey: Monkey? = null
    var rightMonkey: Monkey? = null
    private var cachedValue: Long? = null
    private val canBeCached: Boolean by lazy { !hasHumanNode() }

    override fun eval(): Long =
        when {
            cachedValue != null -> cachedValue!!
            canBeCached -> { cachedValue = computeValue(); cachedValue!! }
            else -> computeValue()
        }

    private fun computeValue(): Long =
        when (op) {
            ADD -> leftMonkey!!.eval() + rightMonkey!!.eval()
            MINUS -> leftMonkey!!.eval() - rightMonkey!!.eval()
            MULT -> leftMonkey!!.eval() * rightMonkey!!.eval()
            DIV -> leftMonkey!!.eval() / rightMonkey!!.eval()
        }

    override fun hasNode(node: String): Boolean =
        leftMonkey!!.hasNode(node) || rightMonkey!!.hasNode(node)

    override fun valueNeeded(needed: Long): Long =
        when (op) {
            ADD -> {
              if (rightMonkey!!.hasHumanNode()) {
                  rightMonkey!!.valueNeeded(needed - leftMonkey!!.eval())
              } else {
                  leftMonkey!!.valueNeeded(needed - rightMonkey!!.eval())
              }
            }
            MINUS ->  {
                if (rightMonkey!!.hasHumanNode()) {
                    rightMonkey!!.valueNeeded(leftMonkey!!.eval() - needed)
                } else {
                    leftMonkey!!.valueNeeded(needed + rightMonkey!!.eval())
                }
            }
            MULT -> {
                if (rightMonkey!!.hasHumanNode()) {
                    rightMonkey!!.valueNeeded(needed.div(leftMonkey!!.eval() ))
                } else {
                    leftMonkey!!.valueNeeded(needed.div(rightMonkey!!.eval()))
                }
            }
            DIV -> {
                if (rightMonkey!!.hasHumanNode()) {
                    rightMonkey!!.valueNeeded(leftMonkey!!.eval().div(needed))
                } else {
                    leftMonkey!!.valueNeeded(needed * rightMonkey!!.eval())
                }
            }
        }
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
