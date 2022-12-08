package day08

// https://adventofcode.com/2022/day/8

import getNonBlankFileLines

fun main(args: Array<String>) {
    val forest = getNonBlankFileLines(args)
        .map { row -> row.map { it.digitToInt() }}.toList()

    val visible = forest
        .applyToAll { rowIndex, colIndex -> forest.isVisible(rowIndex, colIndex) }
        .count { it }

    println(visible)

    val maxScenicScore = forest
        .applyToAll { rowIndex, colIndex -> forest.scenicScore(rowIndex, colIndex) }
        .maxOf { it }

    println(maxScenicScore)
}

typealias Forest = List<List<Int>>
typealias Neighbours = List<Int>

private fun Forest.scenicScore(rowIndex: Int, colIndex: Int): Int =
    if (this.isBoundary(rowIndex, colIndex)) 0 else
    this.distanceLeft(rowIndex, colIndex) *
            this.distanceRight(rowIndex, colIndex) *
            this.distanceTop(rowIndex, colIndex) *
            this.distanceBottom(rowIndex, colIndex)

private fun Forest.distanceLeft(rowIndex: Int, colIndex: Int): Int =
    this.treesAtLeft(rowIndex, colIndex).reversed().distance(this[rowIndex][colIndex])

private fun Forest.distanceRight(rowIndex: Int, colIndex: Int): Int =
    this.treesAtRight(rowIndex, colIndex).distance(this[rowIndex][colIndex])

private fun Neighbours.distance(treeSize: Int): Int {
    var distance = 0
    this.forEach {
            distance += 1
            if (it >= treeSize) return distance
        }
    return distance
}
private fun Forest.distanceTop(rowIndex: Int, colIndex: Int): Int =
    this.treesAtTop(rowIndex, colIndex).reversed().distance(this[rowIndex][colIndex])

private fun Forest.distanceBottom(rowIndex: Int, colIndex: Int): Int =
    this.treesAtBottom(rowIndex, colIndex).distance(this[rowIndex][colIndex])



private fun Forest.isVisible(rowIndex: Int, colIndex: Int): Boolean =
    this.isBoundary(rowIndex, colIndex)
            || this.visibleFromLeft(rowIndex, colIndex)
            || this.visibleFromRight(rowIndex, colIndex)
            || this.visibleFromTop(rowIndex, colIndex)
            || this.visibleFromBottom(rowIndex, colIndex)

private fun Forest.visibleFromLeft(rowIndex: Int, colIndex: Int): Boolean =
    this.treesAtLeft(rowIndex, colIndex).isVisible(this[rowIndex][colIndex])

private fun Forest.visibleFromRight(rowIndex: Int, colIndex: Int): Boolean =
    this.treesAtRight(rowIndex, colIndex).isVisible(this[rowIndex][colIndex])

private fun Forest.visibleFromTop(rowIndex: Int, colIndex: Int): Boolean =
    this.treesAtTop(rowIndex, colIndex).isVisible(this[rowIndex][colIndex])

private fun Forest.visibleFromBottom(rowIndex: Int, colIndex: Int): Boolean =
    this.treesAtBottom(rowIndex, colIndex).isVisible(this[rowIndex][colIndex])

private fun Neighbours.isVisible(treeSize: Int) = this.all { it <  treeSize }



private fun Forest.treesAtLeft(rowIndex: Int, colIndex: Int): Neighbours =
    this[rowIndex].slice(0 until colIndex)

private fun Forest.treesAtRight(rowIndex: Int, colIndex: Int): Neighbours =
    this[rowIndex].slice((colIndex+ 1) until this.size)

private fun Forest.treesAtTop(rowIndex: Int, colIndex: Int): Neighbours =
    (0 until rowIndex).map { this[it][colIndex] }

private fun Forest.treesAtBottom(rowIndex: Int, colIndex: Int): Neighbours =
    ((rowIndex + 1) until this.size).map { this[it][colIndex] }

private fun Forest.isBoundary(rowIndex: Int, colIndex: Int): Boolean =
    rowIndex == 0 || colIndex == 0 || rowIndex == (this.size - 1) || colIndex == (this[0].size - 1)


private fun <E> Forest.applyToAll(function: (Int, Int) -> E): List<E> =
    this.flatMapIndexed { rowIndex, row -> List(row.size) { colIndex -> function(rowIndex, colIndex) } }
