package com.toldoven.aoc.notebook.y2024

import com.toldoven.aoc.notebook.AocClient
import utils.println
import utils.sessionTokenPath
import kotlin.time.measureTime

fun main() {

    val aoc = AocClient.fromFile(sessionTokenPath).interactiveDay(2024, 11)

    val cache = mutableMapOf<Pair<Long, Int>, Long>()

    fun Long.numberOfRocksFrom(remaining: Int): Long {
        cache[this to remaining]?.run { return this }
        return if (remaining == 0) {
            1L
        } else if (this == 0L) {
            1L.numberOfRocksFrom(remaining - 1)
        } else if (this.toString().length % 2 == 0) {
            val toSplit = this.toString()
            val firstValue = toSplit.substring(0 until (toSplit.length / 2)).toLong()
            val secondValue = toSplit.substring((toSplit.length / 2)).toLong()
            firstValue.numberOfRocksFrom(remaining - 1) +
                    secondValue.numberOfRocksFrom(remaining - 1)
        } else {
            (this * 2024).numberOfRocksFrom(remaining - 1)
        }.also {
            cache[Pair(this, remaining)] = it
        }
    }

    fun part1(input: List<String>): Long {
        val rocks = input[0].split(" ").map { it.toLong() }

        return rocks.sumOf { it.numberOfRocksFrom(25) }
    }

    fun part2(input: List<String>): Long {
        val rocks = input[0].split(" ").map { it.toLong() }

        return rocks.sumOf { it.numberOfRocksFrom(75) }
    }

    // test if implementation meets criteria from the description, like:
    // Check test inputs
    val testInput = """
        125 17
    """.trimIndent().split("\n")
    //utils.check(55312L, part1(testInput), "Part 1")
    //utils.check(testInput.size.toLong(), part2(testInput), "Part 2")

    val input = aoc.input()
    measureTime {
        val result = part1(input).also { println("Part 1: $it") }
    }.also { it.println() }
    measureTime {
        val result = part2(input).also { println("Part 2: $it") }
    }.also { it.println() }
}