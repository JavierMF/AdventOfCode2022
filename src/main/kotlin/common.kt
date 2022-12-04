import java.io.File
import kotlin.system.exitProcess

fun getFileFromArgs(args: Array<String>): File {
    if (args.isEmpty()) {
        println("Input file path required")
        exitProcess(-1)
    }
    val file = File(args[0])
    if (!file.exists()) {
        println("Input file does not exist")
        exitProcess(-1)
    }
    return file
}

fun getNonBlankFileLines(args: Array<String>) =
    getFileFromArgs(args)
        .readLines()
        .filter { it.isNotBlank() }
