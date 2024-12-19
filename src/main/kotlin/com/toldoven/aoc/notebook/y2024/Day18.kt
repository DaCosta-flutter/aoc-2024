package com.toldoven.aoc.notebook.y2024

import com.toldoven.aoc.notebook.AocClient
import utils.geometry.Point
import utils.geometry.cardinalNeighbours
import utils.geometry.shortestPath
import utils.println
import utils.sessionTokenPath
import kotlin.time.measureTime

fun main() {

    val aoc = AocClient.fromFile(sessionTokenPath).interactiveDay(2024, 18)

    fun part1(input: List<String>, target: Point = Point(70, 70)): Long {
        val corrupted = input
            .mapIndexed { idx, str -> str.split(",").run { Point(this[0].toInt(), this[1].toInt()) } to idx + 1 }
            .take(1024)
            .toMap()

        //val mapGrid = corrupted.associateWith { '#' }.toMutableMap().run { GridMap(this) }
        //mapGrid.printGrid('.')

        val result = shortestPath(Point(0, 0), target) { point, distance ->
            point.cardinalNeighbours()
                .filter { it.x >= 0 && it.y >= 0 }
                .filter { it.x <= target.x && it.y <= target.y }
                .filter { it !in corrupted }
                .map { it to 1 }
        }

        //result.forEach { t, u -> mapGrid[t] = 'O' }

        return result[target]!!.distance.toLong()
    }

    fun part2(input: List<String>, target: Point = Point(70, 70)): Long {
        var i =-1
        while (i <= input.lastIndex) {
            val corrupted = input
                .mapIndexed { idx, str -> str.split(",").run { Point(this[0].toInt(), this[1].toInt()) } to idx + 1 }
                .take(++i)
                .toMap()

            //val mapGrid = corrupted.associateWith { '#' }.toMutableMap().run { GridMap(this) }
            //mapGrid.printGrid('.')


            val result = shortestPath(Point(0, 0), target) { point, distance ->
                point.cardinalNeighbours()
                    .filter { it.x >= 0 && it.y >= 0 }
                    .filter { it.x <= target.x && it.y <= target.y }
                    .filter { it !in corrupted }
                    .map { it to 1 }
            }

            if (target !in result) {
                break
            }

        }

        //result.forEach { t, u -> mapGrid[t] = 'O' }
        println(input[i-1])

        return input.size.toLong()
    }

    // test if implementation meets criteria from the description, like:
    // Check test inputs
    val testInput = """
        5,4
        4,2
        4,5
        3,0
        2,1
        6,3
        2,4
        1,5
        0,6
        3,3
        2,6
        5,1
        1,2
        5,5
        2,5
        6,5
        1,4
        0,4
        6,4
        1,1
        6,1
        1,0
        0,5
        1,6
        2,0
    """.trimIndent().split("\n")
    utils.check(22, part1(testInput.take(12), Point(6, 6)), "Part 1")
    utils.check(testInput.size.toLong(), part2(testInput, Point(6,6)), "Part 2")

    val input = aoc.input()
    measureTime {
        part1(input).also { println("Part 1: $it") }
    }.also { it.println() }
    measureTime {
        part2(input).also { println("Part 2: $it") }
    }.also { it.println() }
}