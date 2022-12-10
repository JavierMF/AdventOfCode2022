package day10

// https://adventofcode.com/2022/day/10

import getNonBlankFileLines

fun main(args: Array<String>) {

    val instructions = getNonBlankFileLines(args)
        .map { Instruction.fromString(it) }

    val cpu = Cpu(instructions)
    cpu.executeInstructions()

    println(cpu.signalStrength)
    println(cpu.crt.printable())

}

class Crt {
    private var spritePositions = setOf(0, 1, 2)
    private var beam = 0
    private val lines = mutableListOf<String>()
    private var currentLine = "#"

    fun nextCycle(position: Int) {
        spritePositions = setOf(position - 1, position, position + 1)
        drawPixel()
    }

    private fun drawPixel() {
        beam += 1
        val index = beam % 40
        if (index == 0) {
            lines.add(currentLine)
            currentLine = ""
        }
        val newPixel = if (index in spritePositions) "#" else "."
        currentLine += newPixel
    }

    fun printable(): String = lines.joinToString("\n")
}

data class Cpu(
    val instructions: List<Instruction>
) {
    private var cpuState = CpuState(cycle = 1)
    val crt = Crt()
    var signalStrength = 0

    fun executeInstructions() {
        instructions.forEach {
            when (it) {
                is Noop -> executeAdd(0)
                is Addx -> {
                    executeAdd(0)
                    executeAdd(it.value)
                }
            }
        }
    }

    private fun executeAdd(value: Int) {
        cpuState = CpuState(cycle = cpuState.cycle + 1, register = cpuState.register + value)
        crt.nextCycle(position = cpuState.register)
        updateSignalStrength()
    }

    private fun updateSignalStrength() {
        if ((cpuState.cycle-20) % 40 == 0) {
            signalStrength += cpuState.cycle * cpuState.register
        }
    }
}

data class CpuState(
    val cycle: Int,
    val register: Int = 1,
)

interface Instruction {
    companion object {
        fun fromString(line: String) = when(line) {
            "noop" -> Noop()
            else -> addxPattern.firstGroup(line)?.let { Addx(it.toInt()) } ?: throw RuntimeException("Instruction not found")
        }

        private val addxPattern = """addx (.+)""".toRegex()
        private fun Regex.firstGroup(line: String) = this.find(line)?.groupValues?.get(1)
    }
}
class Noop : Instruction
data class Addx(val value: Int):Instruction

