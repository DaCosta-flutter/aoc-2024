package com.toldoven.aoc.notebook.y2024

import com.toldoven.aoc.notebook.AocClient
import utils.geometry.*
import utils.println
import utils.sessionTokenPath
import kotlin.time.measureTime

fun main() {

    val aoc = AocClient.fromFile(sessionTokenPath).interactiveDay(2024, 8)

    fun Collection<Point>.pairwiseCombinations(): List<Pair<Point, Point>> {
        return this.flatMap { i ->
            this.filter { it != i }
                .map { i to it }
        }
    }

    fun part1(input: List<String>): Long {
        val grid = input.toGrid()
        val antennasByFrequency = grid.values()
            .filter { it.value != '.' }
            .groupBy({ it.value }, { it.key })

        val antennaPairs = antennasByFrequency.flatMap { it.value.pairwiseCombinations() }.toSet()

        return antennaPairs
            .map { (a1, a2) -> a1 + (-a1.vecTo(a2)) }
            .filter { it in grid }
            .toSet()
            .size.toLong()
    }

    fun part2(input: List<String>): Long {
        val grid = input.toGrid()
        val antennasByFrequency = grid.values()
            .filter { it.value != '.' }
            .groupBy({ it.value }, { it.key })

        val antennaPairs = antennasByFrequency.flatMap { it.value.pairwiseCombinations() }.toSet()

        return antennaPairs
            .flatMap { (a1, a2) ->
                var curIdx = 0
                generateSequence { a1 + a1.vecTo(a2) * curIdx-- }.takeWhile { it in grid }
            }
            .toSet()
            .size.toLong()
    }

    // test if implementation meets criteria from the description, like:
    // Check test inputs
    val testInput = """
        ............
        ........0...
        .....0......
        .......0....
        ....0.......
        ......A.....
        ............
        ............
        ........A...
        .........A..
        ............
        ............
    """.trimIndent().split("\n")
    utils.check(14, part1(testInput), "Part 1")
    utils.check(34, part2(testInput), "Part 2")

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