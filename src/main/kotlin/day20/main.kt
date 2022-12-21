package day20

// https://adventofcode.com/2022/day/20

import getNonBlankFileLines
import kotlin.math.absoluteValue
import kotlin.system.measureTimeMillis

fun main(args: Array<String>) {
    val file = File(getNonBlankFileLines(args))

    // Works for demo but not for input :(
    val elapsed = measureTimeMillis {
        file.mix()
        val result =  file.fromZero(1000, 2000, 3000).sum()
        println("Part 1 result: $result")
    }
    println("Executed part 1 in ${elapsed / 1000.0} seconds")
}

data class Node(
    val value: Int,
    var prev : Node?,
    var next : Node? = null,
) {
    override fun toString(): String {
        return "Node(value=$value, prev=${prev!!.value}, next=${next!!.value})"
    }
}

class File(lines: List<String>) {
    private val nodes: MutableList<Node> = mutableListOf()
    private var first: Node? = null
    private val listSize: Int = lines.size

    init {
        var lastNode: Node? = null
        lines.map {
            val node = Node(it.toInt(), lastNode)
            if (first == null) first = node
            lastNode?.let { it.next = node }
            lastNode = node
            nodes.add(node)
        }
        first!!.prev = lastNode
        lastNode?.let { it.next = first }
    }

    fun mix() {
       nodes.forEach { node ->
           repeat(node.value.absoluteValue) {
               if (node.value > 0)
                    moveRight(node)
               else
                   moveLeft(node)
           }
       }
        println(nodesToIntList())
    }

    private fun moveRight(node: Node) {
        val nodeAtLeft = node.prev!!
        val nodeAtRight = node.next!!


        nodeAtRight.prev = nodeAtLeft

        node.prev = nodeAtLeft.prev
        node.next = nodeAtLeft
        nodeAtLeft.prev!!.next = node

        nodeAtLeft.next = nodeAtRight
        nodeAtLeft.prev = node

        if (first == nodeAtLeft) first = node
        if (first == node) first = nodeAtLeft
    }

    private fun moveLeft(node: Node) {
        val nodeAtLeft = node.prev!!
        val nodeAtRight = node.next!!

        nodeAtLeft.next = nodeAtRight
        nodeAtRight.prev = nodeAtLeft

        node.prev = nodeAtRight
        node.next = nodeAtRight.next
        nodeAtRight.next!!.prev = node

        nodeAtRight.next = node
        if (first == nodeAtRight) first = node
        if (first == node) first = nodeAtRight
    }

    fun fromZero(vararg indexes: Int): List<Int> {
        val valuesList: List<Int> = nodesToIntList()
        val zeroPos = valuesList.indexOfFirst { it == 0 }
        return indexes.map {
            val index = (zeroPos + it) % listSize
            valuesList[index]
        }
    }

    private fun nodesToIntList(): List<Int> {
        val intList = mutableListOf<Int>()
        var current = first!!
        do {
            intList.add(current.value)
            current = current.next!!
        } while (current != first)
        return intList
    }

}



class FileInt(lines: List<String>) {
    private var nodes: List<Int> = lines.map { value -> value.toInt() }
    private val listSize: Int = lines.size

    fun mix() {
        val newNodes = nodes.toMutableList()
        nodes.forEach { value ->
            val index = newNodes.indexOf(value)
            val candidateNewIndex = index + value
            val borderJumps = if (candidateNewIndex < 0) {
                -1 + value / listSize
            } else {value / listSize}
            val newIndex = ((2*listSize + candidateNewIndex + borderJumps) % listSize)
            newNodes.removeAt(index)
            newNodes.add(newIndex, value)
        }
        nodes = newNodes
        println(nodes)
    }

    fun fromZero(vararg indexes: Int): List<Int> {
        val zeroPos = nodes.indexOfFirst { it == 0 }
        return indexes.map {
            val index = (zeroPos + it) % listSize
            nodes[index]
        }
    }

}
