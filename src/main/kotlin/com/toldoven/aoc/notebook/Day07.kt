package com.toldoven.aoc.notebook

import utils.println
import utils.sessionTokenPath
import kotlin.time.measureTime

fun main() {

    val aoc = AocClient.fromFile(sessionTokenPath).interactiveDay(2024, 7)

    data class Equation(
        val target: Long,
        val operands: List<Long>
    )

    fun Equation.isValidPt1(current: Long = operands[0], idx: Int = 1): Boolean {
        if (idx !in operands.indices || current > target) {
            return current == target
        }
        return isValidPt1(current + operands[idx], idx + 1) ||
                isValidPt1(current * operands[idx], idx + 1)
    }

    fun Equation.isValidPt2(current: Long = operands[0], idx: Int = 1): Boolean {
        if (idx !in operands.indices || current > target) {
            return current == target
        }
        val concatenation = "${current.toBigDecimal().toPlainString()}${operands[idx]}".toLong()
        return isValidPt2(concatenation, idx + 1) ||
                isValidPt2(current + operands[idx], idx + 1) ||
                isValidPt2(current * operands[idx], idx + 1)
    }

    fun parseEquations(input: List<String>): List<Equation> {
        return input.map { str ->
            val result = str.split(":").map { it.trim() }
            Equation(result[0].toLong(), result[1].split(" ").map { it.trim() }.map { it.toLong() })
        }
    }

    fun part1(input: List<String>): Long {
        val equations = parseEquations(input)

        return equations.filter { it.isValidPt1() }.sumOf { it.target }
    }

    fun part2(input: List<String>): Long {
        val equations = parseEquations(input)

        return equations
            .filter { it.isValidPt2() }
            .sumOf { it.target }
    }

    // test if implementation meets criteria from the description, like:
    // Check test inputs
    val testInput = """
        190: 10 19
        3267: 81 40 27
        83: 17 5
        156: 15 6
        7290: 6 8 6 15
        161011: 16 10 13
        192: 17 8 14
        21037: 9 7 18 13
        292: 11 6 16 20
    """.trimIndent().split("\n")
    //utils.check(3749L, part1(testInput), "Part 1")
    utils.check(11387L, part2(testInput), "Part 2")

    val input = aoc.input()
    val resultPart1 = measureTime { part1(input).also { println("Part 1: $it") } }.also { it.println() }
    //aoc.submitPartOne(resultPart1)
    val resultPart2 = measureTime { part2(input).also { println("Part 2: $it") } }.also { it.println() }
//aoc.submitPartTwo(resultPart2)
}