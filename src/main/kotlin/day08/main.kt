package day08

// https://adventofcode.com/2022/day/8

import getNonBlankFileLines

fun main(args: Array<String>) {
    val forest = getNonBlankFileLines(args)
        .map { row -> row.map { it.digitToInt() }}.toList()

    val visible = forest
        .applyToAll { treeCoords -> forest.isVisible(treeCoords) }
        .count { it }

    println(visible)

    val maxScenicScore = forest
        .applyToAll { treeCoords -> forest.scenicScore(treeCoords) }
        .maxOf { it }

    println(maxScenicScore)
}

typealias Forest = List<List<Int>>
typealias Neighbours = List<Int>
data class TreeCoords(val row: Int, val col: Int)
private fun Forest.get(treeCoords: TreeCoords) = this[treeCoords.row][treeCoords.col]

private fun Forest.scenicScore(treeCoords: TreeCoords): Int =
    if (this.isBoundary(treeCoords)) 0 else
    this.distanceLeft(treeCoords) *
            this.distanceRight(treeCoords) *
            this.distanceTop(treeCoords) *
            this.distanceBottom(treeCoords)

private fun Forest.distanceLeft(treeCoords: TreeCoords): Int =
    this.treesAtLeft(treeCoords).reversed().distance(this.get(treeCoords))


private fun Forest.distanceRight(treeCoords: TreeCoords): Int =
    this.treesAtRight(treeCoords).distance(this.get(treeCoords))

private fun Neighbours.distance(treeSize: Int): Int {
    var distance = 0
    this.forEach {
            distance += 1
            if (it >= treeSize) return distance
        }
    return distance
}
private fun Forest.distanceTop(treeCoords: TreeCoords): Int =
    this.treesAtTop(treeCoords).reversed().distance(this.get(treeCoords))

private fun Forest.distanceBottom(treeCoords: TreeCoords): Int =
    this.treesAtBottom(treeCoords).distance(this.get(treeCoords))



private fun Forest.isVisible(treeCoords: TreeCoords): Boolean =
    this.isBoundary(treeCoords)
            || this.visibleFromLeft(treeCoords)
            || this.visibleFromRight(treeCoords)
            || this.visibleFromTop(treeCoords)
            || this.visibleFromBottom(treeCoords)

private fun Forest.visibleFromLeft(treeCoords: TreeCoords): Boolean =
    this.treesAtLeft(treeCoords).isVisible(this.get(treeCoords))

private fun Forest.visibleFromRight(treeCoords: TreeCoords): Boolean =
    this.treesAtRight(treeCoords).isVisible(this.get(treeCoords))

private fun Forest.visibleFromTop(treeCoords: TreeCoords): Boolean =
    this.treesAtTop(treeCoords).isVisible(this.get(treeCoords))

private fun Forest.visibleFromBottom(treeCoords: TreeCoords): Boolean =
    this.treesAtBottom(treeCoords).isVisible(this.get(treeCoords))

private fun Neighbours.isVisible(treeSize: Int) = this.all { it < treeSize }



private fun Forest.treesAtLeft(treeCoords: TreeCoords): Neighbours =
    this[treeCoords.row].slice(0 until treeCoords.col)

private fun Forest.treesAtRight(treeCoords: TreeCoords): Neighbours =
    this[treeCoords.row].slice((treeCoords.col + 1) until this.size)

private fun Forest.treesAtTop(treeCoords: TreeCoords): Neighbours =
    (0 until treeCoords.row).map { this[it][treeCoords.col] }

private fun Forest.treesAtBottom(treeCoords: TreeCoords): Neighbours =
    ((treeCoords.row + 1) until this.size).map { this[it][treeCoords.col] }

private fun Forest.isBoundary(treeCoords: TreeCoords): Boolean =
    treeCoords.row == 0 || treeCoords.col == 0 || treeCoords.row == (this.size - 1) || treeCoords.col == (this[0].size - 1)


private fun <E> Forest.applyToAll(function: (tree: TreeCoords) -> E): List<E> =
    this.flatMapIndexed { rowIndex, row -> List(row.size) { colIndex -> function(TreeCoords(rowIndex, colIndex)) } }


