package day11

// https://adventofcode.com/2022/day/11

import getFileFromArgs

fun main(args: Array<String>) {
    val monkeys = getMonkeys(args)
    val result = monkeys.rounds(20) { input: Long -> input / 3 }
    println(result)

    val monkeys2 = getMonkeys(args)
    val worryReducer = monkeys2.map { it.divideFactor() }.reduce { acc, l ->  acc * l }
    val result2 = monkeys2.rounds(10000) { input: Long -> input % worryReducer }
    println(result2)
}

fun List<Monkey>.rounds(repetitions: Int, worryReducerFunc: (Long) -> Long): Long {
    repeat(repetitions) {
        this.forEach { monkey ->
            monkey.sendItems(worryReducerFunc).forEach {
                this[it.toMonkey].receiveItem(it.item)
            }
        }
    }

    return this.map { it.itemsInspected }
        .sortedDescending()
        .take(2)
        .reduce { acc, i ->  acc * i }
}

data class ItemSend(val toMonkey: Int, val item: Long)

fun getMonkeys(args: Array<String>): List<Monkey> {
    val monkeys = mutableListOf<Monkey>()
    val monkeyLines = mutableListOf<String>()
    getFileFromArgs(args).readLines()
        .forEach {
            if (it.isNotEmpty()) monkeyLines.add(it)
            else { monkeys.add(Monkey(monkeyLines)); monkeyLines.clear()
            }
        }
    monkeys.add(Monkey(monkeyLines))
    return monkeys
}

class Monkey(lines: List<String>) {
    private var items = lines[1].split(":")[1].split(",").map { it.trim().toLong() }.toMutableList()
    private val operation: Operation = Operation(lines[2])
    private val sender: Sender =  Sender(lines.slice(3 until 6))

    var itemsInspected = 0L

    fun sendItems(worryReducer: (Long) -> Long): List<ItemSend> =
        items.map {
            val newValue = worryReducer(operation.apply(it))
            val targetMonkey = sender.sendTo(newValue)
            ItemSend(targetMonkey, newValue)
        }.also {
            itemsInspected += items.size
            items = mutableListOf()
        }

    fun receiveItem(item: Long) { items.add(item) }

    fun divideFactor() = sender.divisible

    override fun toString(): String {
        return "Monkey(items=$items, operation=$operation, sender=$sender)"
    }
}

class Operation(line: String) {
    private val operator: Char
    private val value: String

    init {
        val split = line.split("= old ".toRegex())[1].split(" ")
        operator = split.first().first()
        value = split.last()
    }

    fun apply(input: Long): Long {
        val secondVal = if (value == "old") input else value.toLong()
        return when(operator) {
            '+' -> input + secondVal
            '*' -> input.times(secondVal)
            else -> throw RuntimeException("Unknown operator")
        }
    }

    override fun toString(): String {
        return "Operation(operator=$operator, value='$value')"
    }
}

class Sender(lines: List<String>) {
    val divisible = lines[0].lastInt().toLong()
    private val whenTrue = lines[1].lastInt()
    private val whenFalse = lines[2].lastInt()

    fun sendTo(value: Long) = if (value.mod(divisible) == 0L) whenTrue else whenFalse

    private fun String.lastInt() = this.split(" ").last().toInt()

    override fun toString(): String {
        return "Sender(divisible=$divisible, whenTrue=$whenTrue, whenFalse=$whenFalse)"
    }
}
