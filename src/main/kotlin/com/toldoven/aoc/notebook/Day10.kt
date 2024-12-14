package com.toldoven.aoc.notebook

import utils.geometry.Grid
import utils.geometry.Point
import utils.geometry.cardinalNeighbours
import utils.geometry.toGrid
import utils.println
import utils.sessionTokenPath
import kotlin.time.measureTime

fun main() {

    val aoc = AocClient.fromFile(sessionTokenPath).interactiveDay(2024, 10)

    fun Grid.positions9Reached(pos: Point): Set<Point> {
        val currentValue = this[pos].toString().toInt()
        if (currentValue == 9) {
            return setOf(pos)
        }
        return pos.cardinalNeighbours()
            .filter { it in this }
            .filter { this[it].toString().toInt() == currentValue + 1 }
            .flatMap { this.positions9Reached(it) }
            .toSet()
    }

    fun Grid.numberOfPathsFrom(pos: Point): Int {
        val currentValue = this[pos].toString().toInt()
        if (currentValue == 9) {
            return 1
        }
        return pos.cardinalNeighbours()
            .filter { it in this }
            .filter { this[it].toString().toInt() == currentValue + 1 }
            .sumOf { this.numberOfPathsFrom(it) }
    }

    fun part1(input: List<String>): Long {
        val map = input.toGrid()

        val trailheads = map.values().filter { it.value == '0' }.map { it.key }.toSet()


        return trailheads.sumOf { map.positions9Reached(it).size }.toLong()
    }

    fun part2(input: List<String>): Long {
        val map = input.toGrid()

        val trailheads = map.values().filter { it.value == '0' }.map { it.key }.toSet()


        return trailheads.sumOf { map.numberOfPathsFrom(it) }.toLong()
    }

    // test if implementation meets criteria from the description, like:
    // Check test inputs
    val testInput = """
        89010123
        78121874
        87430965
        96549874
        45678903
        32019012
        01329801
        10456732
    """.trimIndent().split("\n")
    utils.check(36, part1(testInput), "Part 1")
    utils.check(81, part2(testInput), "Part 2")

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