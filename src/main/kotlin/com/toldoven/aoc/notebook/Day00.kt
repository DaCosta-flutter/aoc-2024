package com.toldoven.aoc.notebook

import utils.println

fun main() {

    val aoc = AocClient.fromEnv().interactiveDay(2024, 0)

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
    val resultPart1 = part1(input).also { it.println() }
    //aoc.submitPartOne(resultPart1)
    val resultPart2 = part2(input).also { it.println() }
    //aoc.submitPartTwo(resultPart2)
}