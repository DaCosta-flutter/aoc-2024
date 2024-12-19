package com.toldoven.aoc.notebook

import utils.println
import utils.sessionTokenPath
import kotlin.time.measureTime

fun main() {

    val aoc = AocClient.fromFile(sessionTokenPath).interactiveDay(2024, 19)


    fun part1(input: List<String>): Long {
        val towelPatterns = input[0].trim().split(", ").toSet()
        val designs = input.subList(2, input.size).map { it.trim() }

        val cache = mutableMapOf<String, Boolean>()

        fun String.canBeDesignedWith(patterns: Set<String>): Boolean {
            cache[this]?.run { return this }
            if (this.isEmpty()) {
                return true
            }
            return patterns.filter { this.startsWith(it) }
                .any { pattern ->
                    this.substring(pattern.length).canBeDesignedWith(patterns)
                }.also { cache[this] = it }
        }

        return designs.count { it.canBeDesignedWith(towelPatterns) }.toLong()
    }

    fun part2(input: List<String>): Long {
        val towelPatterns = input[0].trim().split(", ").toSet()
        val designs = input.subList(2, input.size).map { it.trim() }

        val cache = mutableMapOf<String, Long>()

        fun String.numWaysDesign(patterns: Set<String>): Long {
            cache[this]?.run { return this }
            if (this.isEmpty()) {
                return 1L
            }
            return patterns.filter { this.startsWith(it) }
                .sumOf { pattern ->
                    this.substring(pattern.length).numWaysDesign(patterns)
                }.also { cache[this] = it }
        }

        return designs.sumOf { it.numWaysDesign(towelPatterns) }.toLong()
    }

    // test if implementation meets criteria from the description, like:
    // Check test inputs
    val testInput = """
        r, wr, b, g, bwu, rb, gb, br

        brwrr
        bggr
        gbbr
        rrbgbr
        ubwu
        bwurrg
        brgr
        bbrgwb
    """.trimIndent().split("\n")
    utils.check(6, part1(testInput), "Part 1")
    utils.check(16, part2(testInput), "Part 2")

    val input = aoc.input()
    measureTime {
        part1(input).also { println("Part 1: $it") }
    }.also { it.println() }
    measureTime {
        part2(input).also { println("Part 2: $it") }
    }.also { it.println() }
}