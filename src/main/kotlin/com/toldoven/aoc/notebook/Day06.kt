package com.toldoven.aoc.notebook

import utils.geometry.*
import utils.println
import utils.sessionTokenPath
import kotlin.time.measureTime

fun main() {

    val aoc = AocClient.fromFile(sessionTokenPath).interactiveDay(2024, 6)


    fun Direction.rotate90() = when (this) {
        Direction.UP -> Direction.RIGHT
        Direction.LEFT -> Direction.UP
        Direction.DOWN -> Direction.LEFT
        Direction.RIGHT -> Direction.DOWN
        else -> throw IllegalArgumentException()
    }

    fun Grid.move(cur: Point, curDir: Direction): Pair<Point, Direction> {
        var nextPoint = cur.moveTo(curDir)
        var nextDir = curDir
        while (this[nextPoint] == '#') {
            nextDir = nextDir.rotate90()
            nextPoint = cur.moveTo(nextDir)
        }
        return nextPoint to nextDir
    }

    fun part1(input: List<String>): Long {
        val grid = input.toGrid()

        var curPoint = grid.values().filter { it.value == '^' }.map { it.key }.first()
        var curDirection = Direction.UP
        val visited = mutableSetOf(curPoint)

        while (curPoint in grid) {
            visited.add(curPoint)
            val (newPoint, newDirection) = grid.move(curPoint, curDirection)
            curPoint = newPoint
            curDirection = newDirection
        }

        return visited.size.toLong()
    }

    fun Grid.hasLoop(startPos: Point, startDir: Direction): Boolean {
        val visited = mutableSetOf(startPos to startDir)
        var curPoint = startPos
        var curDir = startDir
        while (curPoint in this) {
            val (newPoint, newDir) = this.move(curPoint, curDir)
            curPoint = newPoint
            curDir = newDir
            if ((curPoint to curDir) in visited) {
                return true // we have a loop
            }
            visited.add(curPoint to curDir)
        }

        return false
    }

    fun part2(input: List<String>): Long {
        val grid = input.toGrid()
        val startPoint = grid.values().filter { it.value == '^' }.map { it.key }.first()
        var curPoint = startPoint
        var curDirection = Direction.UP
        val visited = mutableSetOf(curPoint)

        while (curPoint in grid) {
            visited.add(curPoint)
            val (newPoint, newDirection) = grid.move(curPoint, curDirection)
            curPoint = newPoint
            curDirection = newDirection
        }

        return visited
            .filter { grid[it] != '^' }
            .count { point ->
                val previous = grid[point]!!
                grid[point] = '#'
                grid.hasLoop(startPoint, Direction.UP).also {
                    grid[point] = previous
                }
            }.toLong()
    }

// test if implementation meets criteria from the description, like:
// Check test inputs
    val testInput = """
        ....#.....
        .........#
        ..........
        ..#.......
        .......#..
        ..........
        .#..^.....
        ........#.
        #.........
        ......#...
    """.trimIndent().split("\n")
    utils.check(41, part1(testInput), "Part 1")
    utils.check(6, part2(testInput), "Part 2")

    val input = aoc.input()

    val resultPart1 = measureTime { part1(input).also { it.println() } }.also { it.println() }

//aoc.submitPartOne(resultPart1)
    val resultPart2 = measureTime { part2(input).also { it.println() } }.also { it.println() }
//aoc.submitPartTwo(resultPart2)
}