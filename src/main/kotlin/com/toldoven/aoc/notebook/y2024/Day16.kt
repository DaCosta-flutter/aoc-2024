package com.toldoven.aoc.notebook.y2024

import com.toldoven.aoc.notebook.AocClient
import utils.geometry.*
import utils.println
import utils.sessionTokenPath
import kotlin.time.measureTime

fun main() {

    val aoc = AocClient.fromFile(sessionTokenPath).interactiveDay(2024, 16)

    data class PointWithDirection(
        val point: Point,
        val dir: Direction
    )

    fun part1(input: List<String>): Long {
        val grid = input.toGrid()

        val start = grid.values().filter { it.value == 'S' }.first().key
        val result = shortestPath(PointWithDirection(start, Direction.RIGHT)) { pd, _ ->
            val (curPoint, curDirection) = pd
            val currentDirMove =
                curPoint.moveTo(curDirection).takeIf { grid[pd.point] != '#' }
                    ?.run { PointWithDirection(this, curDirection) to 1 }
            val cwDirMove = curPoint.moveTo(curDirection.rotateCw4()).takeIf { grid[it] != '#' }
                ?.run { PointWithDirection(this, curDirection.rotateCw4()) to 1001 }
            val ccwDirMove = curPoint.moveTo(curDirection.rotateCcw4()).takeIf { grid[it] != '#' }
                ?.run { PointWithDirection(this, curDirection.rotateCcw4()) to 1001 }

            listOfNotNull(currentDirMove, cwDirMove, ccwDirMove)
        }

        val target = grid.values().filter { it.value == 'E' }.first().key
        return setOf(Direction.UP, Direction.RIGHT, Direction.DOWN, Direction.LEFT).mapNotNull {
            result[PointWithDirection(
                target,
                it
            )]?.distance?.toLong()
        }.min()
    }

    fun part2(input: List<String>): Long {
        val grid = input.toGrid()

        val start = grid.values().filter { it.value == 'S' }.first().key
        val target = grid.values().filter { it.value == 'E' }.first().key
        val result = shortestPath(PointWithDirection(start, Direction.RIGHT)) { (curPoint, curDirection), _ ->
            val currentDirMove =
                curPoint.moveTo(curDirection).takeIf { grid[it] != '#' }
                    ?.run { PointWithDirection(this, curDirection) to 1 }
            val cwDirMove = curPoint.moveTo(curDirection.rotateCw4()).takeIf { grid[it] != '#' }
                ?.run { PointWithDirection(this, curDirection.rotateCw4()) to 1001 }
            val ccwDirMove = curPoint.moveTo(curDirection.rotateCcw4()).takeIf { grid[it] != '#' }
                ?.run { PointWithDirection(this, curDirection.rotateCcw4()) to 1001 }

            listOfNotNull(currentDirMove, cwDirMove, ccwDirMove)
        }

        val bestTarget =
            setOf(Direction.UP, Direction.RIGHT, Direction.DOWN, Direction.LEFT).map { PointWithDirection(target, it) }
                .filter {
                    it in result
                }.minBy { result[it]!!.distance.toLong() }

        fun findTotalPointsInBestPath(pointInBestPath: PointWithDirection): Set<Point> {
            if (pointInBestPath.point == start) {
                return setOf(pointInBestPath.point)
            }
            return result[pointInBestPath]!!.previous.flatMap { findTotalPointsInBestPath(it) }
                .toSet() + pointInBestPath.point
        }

        val points = findTotalPointsInBestPath(bestTarget)

        return points.size.toLong()
    }


    // test if implementation meets criteria from the description, like:
    // Check test inputs
    val testInput = """
        ###############
        #.......#....E#
        #.#.###.#.###.#
        #.....#.#...#.#
        #.###.#####.#.#
        #.#.#.......#.#
        #.#.#####.###.#
        #...........#.#
        ###.#.#####.#.#
        #...#.....#.#.#
        #.#.#.###.#.#.#
        #.....#...#.#.#
        #.###.#.#.#.#.#
        #S..#.....#...#
        ###############
    """.trimIndent().split("\n")

    val secondExample = """
        #################
        #...#...#...#..E#
        #.#.#.#.#.#.#.#.#
        #.#.#.#...#...#.#
        #.#.#.#.###.#.#.#
        #...#.#.#.....#.#
        #.#.#.#.#.#####.#
        #.#...#.#.#.....#
        #.#.#####.#.###.#
        #.#.#.......#...#
        #.#.###.#####.###
        #.#.#...#.....#.#
        #.#.#.#####.###.#
        #.#.#.........#.#
        #.#.#.#########.#
        #S#.............#
        #################
    """.trimIndent().split("\n")
    utils.check(7036, part1(testInput), "Part 1")
    utils.check(11048, part1(secondExample), "Part 1")
    utils.check(45, part2(testInput), "Part 2")
    utils.check(64, part2(secondExample), "Part 2")

    val input = aoc.input()
    measureTime {
        part1(input).also { println("Part 1: $it") }
    }.also { it.println() }
    measureTime {
        part2(input).also { println("Part 2: $it") }
    }.also { it.println() }
}