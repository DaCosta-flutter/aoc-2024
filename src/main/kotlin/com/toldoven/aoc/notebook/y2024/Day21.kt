package com.toldoven.aoc.notebook.y2024

import com.toldoven.aoc.notebook.AocClient
import utils.geometry.*
import utils.println
import utils.sessionTokenPath
import kotlin.time.measureTime


typealias Instructions = List<Char>

fun main() {

    val aoc = AocClient.fromFile(sessionTokenPath).interactiveDay(2024, 21)

    fun Direction.toChar() = when (this) {
        Direction.UP -> '^'
        Direction.LEFT -> '<'
        Direction.DOWN -> 'v'
        Direction.RIGHT -> '>'
        else -> throw Exception()
    }

    fun allPathsFrom(
        source: Point,
        destination: Point,
        allPoints: Set<Point>,
        visited: Set<Point> = setOf(source),
        currentPath: List<Char> = listOf()
    ): List<Instructions> {
        if (source == destination) {
            return listOf(currentPath)
        }
        return setOf(Direction.UP, Direction.LEFT, Direction.RIGHT, Direction.DOWN)
            .filter { source.moveTo(it) in allPoints && source.moveTo(it) !in visited }
            .flatMap {
                allPathsFrom(
                    source.moveTo(it),
                    destination,
                    allPoints,
                    visited + source.moveTo(it),
                    currentPath + it.toChar()
                )
            }
    }

    abstract class Robot {
        abstract val grid: Grid
        abstract var currentPoint: Point

        fun possibleInstructionsToNextTarget(initialChar: Char, target: Char): List<Instructions> {
            val sourcePos = grid.values().first { it.value == initialChar }.key
            val destinationPos = grid.values().first { it.value == target }.key
            return allPathsFrom(
                sourcePos,
                destinationPos,
                grid.keys()
            ).map { path -> path + 'A' }.sortedBy { it.size }
        }

        fun printStatus() {
            grid.printGrid()
            println("Current: ${grid[currentPoint]}")
        }
    }

    class KeyPadRobot : Robot() {
        override var currentPoint = Point(2, 3)

        override val grid: Grid = mutableMapOf(
            Point(0, 0) to '7',
            Point(1, 0) to '8',
            Point(2, 0) to '9',

            Point(0, 1) to '4',
            Point(1, 1) to '5',
            Point(2, 1) to '6',

            Point(0, 2) to '1',
            Point(1, 2) to '2',
            Point(2, 2) to '3',

            Point(1, 3) to '0',
            Point(2, 3) to 'A',
        ).run { GridMap(this) }
    }

    class DirectionalRobot : Robot() {
        val startingPoint = Point(2, 0)
        override var currentPoint: Point = startingPoint
        override val grid: Grid = mutableMapOf(
            Point(1, 0) to '^',
            Point(2, 0) to 'A',

            Point(0, 1) to '<',
            Point(1, 1) to 'v',
            Point(2, 1) to '>',
        ).run { GridMap(this) }
    }

    data class CacheKey(
        val curPos: Char,
        val target: Char,
        val depth: Int
    )

    fun minNumberOfKeyPresses(
        curPos: Char,
        target: Char,
        robots: List<Robot>,
        depth: Int = 0,
        cache: MutableMap<CacheKey, Long> = mutableMapOf()
    ): Long {
        if (depth >= robots.size) {
            return 1L
        }
        val cacheKey = CacheKey(curPos, target, depth)
        if (cacheKey in cache) {
            return cache[cacheKey]!!
        }

        return robots[depth].possibleInstructionsToNextTarget(curPos, target).minOf { instructions ->
            var currentPoint = 'A'
            instructions.sumOf { instruction ->
                minNumberOfKeyPresses(currentPoint, instruction, robots, depth + 1, cache).also {
                    currentPoint = instruction
                }
            }
        }.also { cache[cacheKey] = it }
    }


    fun complexityScoreFor(input: List<String>, robots: List<Robot>): Long {
        val totalKeyPressesForInput = mutableMapOf<String, Long>()

        for (code in input) {
            var currentRobotKey = 'A'
            val targets = code.toCharArray().toMutableList()
            var totalKeyPresses = 0L
            while (targets.isNotEmpty()) {
                val nextKeyToPress = targets.removeFirst()
                totalKeyPresses += minNumberOfKeyPresses(currentRobotKey, nextKeyToPress, robots)
                currentRobotKey = nextKeyToPress
            }
            totalKeyPressesForInput[code] = totalKeyPresses
        }

        return totalKeyPressesForInput.mapKeys { it.key.removeSuffix("A").toInt() }.map { it.key.toLong() * it.value }
            .sum()
    }

    fun part1(input: List<String>): Long {
        val keyPadRobot = KeyPadRobot()
        val directionalRobot1 = DirectionalRobot()
        val directionalRobot2 = DirectionalRobot()

        val robots = listOf(keyPadRobot, directionalRobot1, directionalRobot2)

        return complexityScoreFor(input, robots)
    }

    fun part2(input: List<String>): Long {
        val keyPadRobot = KeyPadRobot()
        val directionalRobots = (1..25).map { DirectionalRobot() }
        val robots = listOf(keyPadRobot) + directionalRobots

        return complexityScoreFor(input, robots)
    }

    // test if implementation meets criteria from the description, like:
    // Check test inputs
    val testInput = """
        029A
        980A
        179A
        456A
        379A
    """.trimIndent().split("\n")
    utils.check(126384, part1(testInput), "Part 1")
    //utils.check(testInput.size.toLong(), part2(testInput), "Part 2")

    val input = aoc.input()
    measureTime {
        part1(input).also { println("Part 1: $it") }
    }.also { it.println() }
    measureTime {
        part2(input).also { println("Part 2: $it") }
    }.also { it.println() }
}