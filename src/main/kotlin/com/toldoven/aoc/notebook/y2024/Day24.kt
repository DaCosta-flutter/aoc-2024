package com.toldoven.aoc.notebook.y2024

import kotlin.io.path.Path
import kotlin.io.path.readLines
import com.toldoven.aoc.notebook.AocClient
import utils.println
import utils.readInput
import utils.sessionTokenPath
import kotlin.io.path.readLines
import kotlin.time.measureTime

enum class Operand {
    XOR, OR, AND
}

fun main() {

    val aoc = AocClient.fromFile(sessionTokenPath).interactiveDay(2024, 24)


    data class Instruction(
        val a: String,
        val b: String,
        val operand: Operand,
        val toStore: String,
    )

    fun part1(input: List<String>): Long {
        val wireValues = mutableMapOf<String, Boolean>()
        val instructions = mutableListOf<Instruction>()

        var idx = 0
        while (idx < input.size) {
            if (input[idx].isBlank()) {
                break
            }
            val (wire, value) = input[idx++].split(": ").run {
                this[0] to (this[1].toInt() == 1)
            }
            wireValues[wire] = value
        }

        while (idx < input.size) {
            if (input[idx].isBlank()) {
                idx++
                continue
            }
            val (instructionStr, toStore) = input[idx++].split(" -> ").run {
                this[0] to this[1]
            }
            val instruction = instructionStr.split(" ").run {
                val a = this[0]
                val b = this[2]
                val operand = when (this[1]) {
                    "AND" -> Operand.AND
                    "OR" -> Operand.OR
                    "XOR" -> Operand.XOR
                    else -> throw IllegalArgumentException()
                }
                Instruction(a, b, operand, toStore)
            }
            instructions.add(instruction)
        }

        println("x")
        wireValues
            .filter { it.key.startsWith("x") }
            .toSortedMap()
            .map { if (it.value) 1 else 0 }
            .joinToString("").reversed().also { it.println() }
        println("y")
        wireValues
            .filter { it.key.startsWith("y") }
            .toSortedMap()
            .map { if (it.value) 1 else 0 }
            .joinToString("").reversed().also { it.println() }


        println()
        println()
        instructions.forEach { instruction ->

            val join = "${instruction.operand}_${instruction.a}_${instruction.b}"
            println("${instruction.a} -> $join [label=\"${instruction.operand}\"]")
            println("${instruction.b} -> $join [label=\"${instruction.operand}\"]")
            println("$join -> ${instruction.toStore} [label=\"${instruction.operand}\"]")
        }
        println()
        println()
        println()

        fun Instruction.perform(): Boolean = when (this.operand) {
            Operand.XOR -> wireValues[a]!!.xor(wireValues[b]!!)
            Operand.OR -> wireValues[a]!!.or(wireValues[b]!!)
            Operand.AND -> wireValues[a]!!.and(wireValues[b]!!)
        }

        idx = 0
        while (instructions.isNotEmpty()) {
            if (idx >= instructions.size) {
                idx = 0
            }
            val instruction = instructions[idx++]
            if (instruction.a in wireValues && instruction.b in wireValues) {
                wireValues[instruction.toStore] = instruction.perform()
                instructions.remove(instruction)
            }
        }

        val result = wireValues
            .filter { it.key.startsWith("z") }
            .toSortedMap()
            .map { if (it.value) 1 else 0 }
            .joinToString("").reversed().also { it.println() }

        return result.toLong(2)
    }

    fun part2(input: List<String>): Long {
        return input.size.toLong()
    }

    // test if implementation meets criteria from the description, like:
    // Check test inputs
    val testInput = """
        x00: 1
        x01: 1
        x02: 1
        y00: 0
        y01: 1
        y02: 0

        x00 AND y00 -> z00
        x01 XOR y01 -> z01
        x02 OR y02 -> z02
    """.trimIndent().split("\n")

    val anotherInput = """
       x00: 1
       x01: 0
       x02: 1
       x03: 1
       x04: 0
       y00: 1
       y01: 1
       y02: 1
       y03: 1
       y04: 1

       ntg XOR fgs -> mjb
       y02 OR x01 -> tnw
       kwq OR kpj -> z05
       x00 OR x03 -> fst
       tgd XOR rvg -> z01
       vdt OR tnw -> bfw
       bfw AND frj -> z10
       ffh OR nrd -> bqk
       y00 AND y03 -> djm
       y03 OR y00 -> psh
       bqk OR frj -> z08
       tnw OR fst -> frj
       gnj AND tgd -> z11
       bfw XOR mjb -> z00
       x03 OR x00 -> vdt
       gnj AND wpb -> z02
       x04 AND y00 -> kjc
       djm OR pbm -> qhw
       nrd AND vdt -> hwm
       kjc AND fst -> rvg
       y04 OR y02 -> fgs
       y01 AND x02 -> pbm
       ntg OR kjc -> kwq
       psh XOR fgs -> tgd
       qhw XOR tgd -> z09
       pbm OR djm -> kpj
       x03 XOR y03 -> ffh
       x00 XOR y04 -> ntg
       bfw OR bqk -> z06
       nrd XOR fgs -> wpb
       frj XOR qhw -> z04
       bqk OR frj -> z07
       y03 OR x01 -> nrd
       hwm AND bqk -> z03
       tgd XOR rvg -> z12
       tnw OR pbm -> gnj
    """.trimIndent().split("\n")
    utils.check(4, part1(testInput), "Part 1")
    utils.check(2024, part1(anotherInput), "Part 1")

    val inputPt2Example = """
        x00: 0
        x01: 1
        x02: 0
        x03: 1
        x04: 0
        x05: 1
        y00: 0
        y01: 0
        y02: 1
        y03: 1
        y04: 0
        y05: 1

        x00 AND y00 -> z05
        x01 AND y01 -> z02
        x02 AND y02 -> z01
        x03 AND y03 -> z03
        x04 AND y04 -> z04
        x05 AND y05 -> z00
    """.trimIndent().split("\n")

    //utils.check(2024, part2(inputPt2Example), "Part 2")

    val input = aoc.input()

    measureTime {
        part1(input).also { println("Part 1: $it") }
    }.also { it.println() }
    measureTime {
        part2(input).also { println("Part 2: $it") }
    }.also { it.println() }
}