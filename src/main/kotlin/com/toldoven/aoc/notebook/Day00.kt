package com.toldoven.aoc.notebook

import utils.println
import utils.sessionTokenPath
import kotlin.time.measureTime

fun main() {

    val aoc = AocClient.fromFile(sessionTokenPath).interactiveDay(2024, 0)

    fun part1(input: List<String>): Long {
        return input.size.toLong()
    }

    fun part2(input: List<String>): Long {
        return input.size.toLong()
    }

    // test if implementation meets criteria from the description, like:
    // Check test inputs
    val testInput = """
        
    """.trimIndent().split("\n")
    utils.check(testInput.size.toLong(), part1(testInput), "Part 1")
    utils.check(testInput.size.toLong(), part2(testInput), "Part 2")

    val input = aoc.input()
    measureTime {
        val result = part1(input).also { println("Part 1: $it") }
        //aoc.submitPartOne(result)
    }.also { it.println() }
    measureTime {
        val result = part2(input).also { println("Part 2: $it") }
        //aoc.submitPartTwo(result)
    }.also { it.println() }
}