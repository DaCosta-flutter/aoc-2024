package com.toldoven.aoc.notebook

import utils.geometry.GridMap
import utils.geometry.Point
import utils.geometry.neighbours
import utils.println
import utils.sessionTokenPath
import kotlin.math.abs
import kotlin.time.measureTime

fun main() {

    val aoc = AocClient.fromFile(sessionTokenPath).interactiveDay(2024, 14)

    data class Robot(
        val initialPos: Point,
        val velocity: Point
    )

    fun Robot.posAfter(seconds: Int, sizeOfGrid: Point = Point(11, 7)): Point {
        val xRealPos = abs((initialPos.x + seconds * velocity.x.toLong()).mod(sizeOfGrid.x))
        val yRealPos = abs((initialPos.y + (seconds * velocity.y.toLong())).mod(sizeOfGrid.y))
        return Point(xRealPos, yRealPos)
    }

    fun parseRobots(input: List<String>): MutableList<Robot> {
        val robots = mutableListOf<Robot>()

        var currentIdx = 0
        while (currentIdx in input.indices) {
            val part = input[currentIdx++].split(" ")
            val initialPos = part[0].removePrefix("p=").split(",").run {
                Point(this[0].toInt(), this[1].toInt())
            }
            val vel = part[1].removePrefix("v=").split(",").run {
                Point(this[0].toInt(), this[1].toInt())
            }
            robots.add(Robot(initialPos, vel))
        }
        return robots
    }

    fun part1(input: List<String>, gridSize: Point): Long {
        val robots = parseRobots(input)

        val robotPos = robots.map { robot -> robot.posAfter(100, gridSize) }
        val grid = GridMap(robotPos.associateWith { '*' }.toMutableMap())
        grid.printGrid('.')

        var upperLeftNumRobots = 0
        var upperRightNumRobots = 0
        var bottomLeftNumRobots = 0
        var bottomRightNumRobots = 0

        robotPos.forEach {
            when {
                it.x > gridSize.x / 2 && it.y < gridSize.y / 2 -> upperRightNumRobots++
                it.x < gridSize.x / 2 && it.y < gridSize.y / 2 -> upperLeftNumRobots++
                it.x > gridSize.x / 2 && it.y > gridSize.y / 2 -> bottomRightNumRobots++
                it.x < gridSize.x / 2 && it.y > gridSize.y / 2 -> bottomLeftNumRobots++
            }
        }

        return upperLeftNumRobots.toLong() * upperRightNumRobots * bottomRightNumRobots * bottomLeftNumRobots
    }

    fun part2(input: List<String>, gridSize: Point): Long {
        val robots = parseRobots(input)
        var currentSec = 0

        while (true) {
            ++currentSec

            val robotPos = robots.map { robot -> robot.posAfter(currentSec, gridSize) }.toSet()
            val numConnectedPoints = robotPos.sumOf { it.neighbours().count { neigh -> neigh in robotPos } }
            if (numConnectedPoints > 1000) {
                return currentSec.toLong()
            }
        }
    }

    // test if implementation meets criteria from the description, like:
    // Check test inputs
    val testInput = """
        p=0,4 v=3,-3
        p=6,3 v=-1,-3
        p=10,3 v=-1,2
        p=2,0 v=2,-1
        p=0,0 v=1,3
        p=3,0 v=-2,-2
        p=7,6 v=-1,-3
        p=3,0 v=-1,-2
        p=9,3 v=2,3
        p=7,3 v=-1,2
        p=2,4 v=2,-3
        p=9,5 v=-3,-3
    """.trimIndent().split("\n")
    utils.check(12, part1(testInput, Point(11, 7)), "Part 1")

    val input = aoc.input()
    measureTime {
        part1(input, Point(101, 103)).also { println("Part 1: $it") }
    }.also { it.println() }
    measureTime {
        part2(input, Point(101, 103)).also { println("Part 2: $it") }
    }.also { it.println() }
}