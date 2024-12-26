package com.toldoven.aoc.notebook.y2024

import com.toldoven.aoc.notebook.AocClient
import utils.geometry.Grid
import utils.geometry.Point
import utils.geometry.toGrid
import utils.println
import utils.sessionTokenPath
import kotlin.time.measureTime

fun main() {

    val aoc = AocClient.fromFile(sessionTokenPath).interactiveDay(2024, 25)

    fun Grid.isLock(): Boolean = this.values().filter { it.key.y == 0 }.all { it.value == '#' }

    fun Grid.codeOf(): List<Int> = (0..4)
        .map { x ->
            (0..6).count { y -> this[Point(x, y)] == '#' } - 1
        }

    fun Grid.keyFor(): List<Int> = codeOf().run {
        listOf(5, 5, 5, 5, 5).mapIndexed { idx, value -> value - this[idx] }
    }

    fun divideListOnEmptyString(strings: List<String>): List<List<String>> {
        return strings
            .withIndex()
            .fold(mutableListOf(mutableListOf<String>())) { acc, (index, value) ->
                if (value.isEmpty()) {
                    acc.add(mutableListOf()) // Start a new list when encountering an empty string
                } else {
                    acc.last().add(value) // Add to the current list
                }
                acc
            }
            .filter { it.isNotEmpty() } // Remove any empty groups
    }

    fun part1(input: List<String>): Long {
        val grids = divideListOnEmptyString(input)
            .map { it.toGrid() }

        val keys = mutableSetOf<List<Int>>()
        val locks = mutableSetOf<List<Int>>()

        grids.forEach {
            if (it.isLock()) {
                locks.add(it.codeOf())
            } else {
                keys.add(it.codeOf())
            }
        }

        val numLocksWithKeys = grids
            .filter { it.isLock() }
            .sumOf {
                val targetKeyForLock = it.keyFor()
                keys
                    .count { keyCode -> keyCode.filterIndexed { idx, value -> targetKeyForLock[idx] - value >= 0 }.size == 5 }
            }

        return numLocksWithKeys.toLong()
    }


    // test if implementation meets criteria from the description, like:
    // Check test inputs
    val testInput = """
        #####
        .####
        .####
        .####
        .#.#.
        .#...
        .....

        #####
        ##.##
        .#.##
        ...##
        ...#.
        ...#.
        .....

        .....
        #....
        #....
        #...#
        #.#.#
        #.###
        #####

        .....
        .....
        #.#..
        ###..
        ###.#
        ###.#
        #####

        .....
        .....
        .....
        #....
        #.#..
        #.#.#
        #####
    """.trimIndent().split("\n")
    utils.check(3, part1(testInput), "Part 1")

    val input = aoc.input()
    measureTime {
        part1(input).also { println("Part 1: $it") }
    }.also { it.println() }
}