package day07

// https://adventofcode.com/2022/day/7

import getNonBlankFileLines

fun main(args: Array<String>) {
    val commandLineBuilder = CommandLineBuilder()
    val commandLines = getNonBlankFileLines(args).mapNotNull { commandLineBuilder.build(it) }

    val fileSystem = toFileSystem(commandLines)

    val result1 = fileSystem
        .allSubdirs()
        .map { it.totalDirSize() }
        .filter { it <= 100000 }
        .sumOf { it }
    println(result1)

    val totalSize   = 70000000
    val desiredSize = 30000000
    val freeSpace = totalSize - fileSystem.totalDirSize()
    val minSizeToRemove = desiredSize - freeSpace
    val result2 = fileSystem
        .allSubdirs()
        .filter { it.totalDirSize() >= minSizeToRemove }
        .minBy { it.totalDirSize() }
        .totalDirSize()
    println(result2)
}

class CommandLineBuilder() {

    fun build(line: String): CommandLine? =
        when {
            line == "$ cd .." -> CdBack()
            cdPattern.matches(line) -> cdPattern.firstGroup(line)?.let { CdCommand(it) }
            filePattern.matches(line) -> filePattern.firstGroup(line)?.let { FileContents(it.toLong()) }
            else -> null
        }

    companion object {
        private val cdPattern = """\$ cd (.+)""".toRegex()
        private val filePattern = """(\d+) .+""".toRegex()
    }

    private fun Regex.firstGroup(line: String) = this.find(line)?.groupValues?.get(1)
}

fun toFileSystem(commandLines: List<CommandLine>): Dir {
    var currentDir: Dir? = null

    commandLines.forEach { command ->
        when(command) {
            is CdBack -> currentDir = currentDir?.parentDir
            is FileContents -> currentDir?.addFileContents(command)
            is CdCommand -> {
                val newDir = Dir(command.dirName, currentDir)
                currentDir?.addSubdir(newDir)
                currentDir = newDir
            }
        }
    }

    return currentDir!!.getRootDir()
}

data class Dir(
    val dirName: String,
    val parentDir: Dir?
) {

    private var fileSize = 0L
    private val subdirs = mutableListOf<Dir>()

    fun allSubdirs(): List<Dir> = subdirs + subdirs.flatMap { it.allSubdirs() }

    fun totalDirSize(): Long = fileSize + allSubdirs().sumOf { it.fileSize }

    fun getRootDir(): Dir = parentDir?.getRootDir() ?: this

    fun addFileContents(newFileContents: FileContents) {
        fileSize += newFileContents.fileSize
    }

    fun addSubdir(newDir: Dir) {
        subdirs.add(newDir)
    }

    override fun toString(): String {
        val subdirsNames = subdirs.map { it.dirName }.joinToString(",")
        return "Dir(dirName='$dirName', parentDir=${parentDir?.dirName}, fileSize=$fileSize, subdirs=$subdirsNames)"
    }
}

interface CommandLine
data class CdCommand(val dirName: String): CommandLine
class CdBack: CommandLine
data class FileContents(val fileSize: Long): CommandLine
