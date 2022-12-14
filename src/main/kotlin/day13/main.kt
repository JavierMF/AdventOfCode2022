package day13

// https://adventofcode.com/2022/day/13

import getFileFromArgs

fun main(args: Array<String>) {
    val pairs = getPairs(args)

    val sumPosRightPairs = pairs
        .map { it.inRightOrder() }
        .mapIndexed { index, it -> if (it) index+1 else 0 }
        .sum()
    println(sumPosRightPairs)

    val allPackets = pairs.flatMap { listOf(
        PacketInList(it.left, false),
        PacketInList(it.right, false)
    )} + listOf(
        PacketInList(Packet("[[2]]").contents, true),
        PacketInList(Packet("[[6]]").contents, true),
    )
    val result2 = allPackets.sortedBy { it.packet }
        .mapIndexed { index, packetInList -> if (packetInList.isDivider) index + 1 else null }
        .filterNotNull()
        .reduce { acc, i ->  acc * i }
    println(result2)
}

data class PacketInList(val packet: PacketList, val isDivider: Boolean)

interface PacketContent: Comparable<PacketContent> {
    override fun compareTo(other: PacketContent): Int
}

data class Value(val value: Int): PacketContent {
    override fun compareTo(other: PacketContent): Int =
        when(other) {
            is Value -> this.value.compareTo(other.value)
            is PacketList -> PacketList(listOf(this)).compareTo(other)
            else -> throw RuntimeException("Unknown type to compare")
        }
}

data class PacketList(val contents: List<PacketContent>): PacketContent {
    override fun compareTo(other: PacketContent): Int {
        val otherList = if (other is Value) listOf(other) else (other as PacketList).contents
        this.contents.forEachIndexed { index, thisContent ->
            val otherContent = otherList.getOrNull(index) ?: return 1
            val result = thisContent.compareTo(otherContent)
            if (result != 0) return result
        }

        return if (this.contents.size < otherList.size) return -1 else 0
    }
}

class Packet(line: String) {
    val contents: PacketList = buildPacketList(line.toList(), 1).list

    private fun buildPacketList(charList: List<Char>, nextIndex: Int): BuildListResult {
        val contents = mutableListOf<PacketContent>()
        var i = nextIndex
        var currentValueString = ""
        while (i < charList.size) {
            when (val aChar = charList[i]) {
                ',' -> if (currentValueString.isNotEmpty()) contents.add(Value(currentValueString.toInt())).also { currentValueString = "" }
                '[' -> {
                    val result = buildPacketList(charList, i+1)
                    contents.add(result.list)
                    i = result.nextIndex
                }
                ']' -> {
                    if (currentValueString.isNotEmpty()) contents.add(Value(currentValueString.toInt())).also { currentValueString = "" }
                    return BuildListResult(PacketList(contents), i + 1)
                }
                else -> currentValueString += aChar
            }
            i += 1
        }
        return BuildListResult(PacketList(contents), i + 1)
    }

    override fun toString() = "Packet(contents=$contents)"

    data class BuildListResult(val list: PacketList, val nextIndex: Int)
}

class PairOfPackets(lines: List<String>) {
    val left = Packet(lines.first()).contents
    val right = Packet(lines.last()).contents

    override fun toString() = "PairOfPackets(left=$left, right=$right)"

    fun inRightOrder(): Boolean = left.compareTo(right) == -1
}

fun getPairs(args: Array<String>): List<PairOfPackets> {
    val pairs = mutableListOf<PairOfPackets>()
    val pairLines = mutableListOf<String>()
    getFileFromArgs(args).readLines()
        .forEach {
            if (it.isNotEmpty()) pairLines.add(it)
            else { pairs.add(PairOfPackets(pairLines)); pairLines.clear() }
        }
    pairs.add(PairOfPackets(pairLines))
    return pairs
}
