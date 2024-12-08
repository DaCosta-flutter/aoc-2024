package com.toldoven.aoc.notebook

import utils.geometry.*
import utils.println
import utils.sessionTokenPath
import kotlin.math.abs
import kotlin.math.roundToInt
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

        val antennaSets = antennasByFrequency.flatMap { it.value.pairwiseCombinations() }.toSet()

        val antinodes = antennaSets.mapNotNull { (p1, p2) ->
            val line = p1.lineTo(p2)
            (input.indices)
                .asSequence()
                .map { it to line.yAt(it) }
                .filter { (_, y) -> abs(y.roundToInt() - y) < 0.00000001 }
                .map { Point(it.first, it.second.roundToInt()) }
                .firstOrNull { it in grid && it.manhattanDistance(p1) == 2 * it.manhattanDistance(p2) }
        }.toSet()

        return antinodes.size.toLong()
    }

    fun part2(input: List<String>): Long {
        val grid = input.toGrid()
        val antennasByFrequency = grid.values()
            .filter { it.value != '.' }
            .groupBy({ it.value }, { it.key })

        val antennaSets = antennasByFrequency.flatMap { it.value.pairwiseCombinations() }.toSet()

        val antinodes = antennaSets.flatMap { (p1, p2) ->
            val line = p1.lineTo(p2)
            (input.indices)
                .asSequence()
                .map { it to line.yAt(it) }
                .filter { (_, y) -> abs(y.roundToInt() - y) < 0.00000001 }
                .map { Point(it.first, it.second.roundToInt()) }
                .filter { it in grid }
        }.toSet()

        return antinodes.size.toLong()
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