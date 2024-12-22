package com.toldoven.aoc.notebook.y2024

import com.toldoven.aoc.notebook.AocClient
import utils.geometry.*
import utils.println
import utils.sessionTokenPath
import java.util.*
import kotlin.time.measureTime

fun main() {

    val aoc = AocClient.fromFile(sessionTokenPath).interactiveDay(2024, 20)

    data class PointWithCheat(
        val p: Point,
        val isCheatIn: Boolean,
        val hasCheatAvailable: Boolean,
        val isCheatOutPoint: Boolean
    )

    data class CheatPoints(
        val inPoint: Point,
        val outPoint: Point
    )

    fun part1(input: List<String>): Long {
        val grid = input.toGridMap()
        val startPosition = grid.values().first { it.value == 'S' }
            .run { PointWithCheat(this.key, false, true, isCheatOutPoint = false) }
        val targetPosition = grid.values().first { it.value == 'E' }.key

        val baseline = shortestPath(startPosition.p) { point, curDistance ->
            point.cardinalNeighbours()
                .filter { it in grid }
                .filter { grid[it] != '#' }
                .map { it to 1 }
                .toSet()
        }
        val shortestTimeBaseline = baseline[targetPosition]!!

        val cheatPoints = mutableMapOf<CheatPoints, Distance>()
        var minDistance = Integer.MIN_VALUE

        outer@ while (minDistance <= (shortestTimeBaseline.distance - 100)) {
            val paths = shortestPath(startPosition) { curPoint, curDistance ->
                curPoint.p.cardinalNeighbours()
                    .asSequence()
                    .filter { it in grid }
                    .mapNotNull {
                        if (grid[it] != '#') curPoint.copy(
                            p = it,
                            hasCheatAvailable = curPoint.hasCheatAvailable,
                            isCheatIn = false,
                            isCheatOutPoint = curPoint.isCheatIn
                        ) else if (curPoint.hasCheatAvailable)
                            PointWithCheat(
                                it,
                                isCheatIn = true,
                                hasCheatAvailable = false,
                                isCheatOutPoint = false
                            ) else null
                    }
                    .filterNot { it.isCheatOutPoint && CheatPoints(curPoint.p, it.p) in cheatPoints }
                    .map { it to 1 }
                    .toSet()
            }

            val target = paths.filter { it.key.p == targetPosition }.minBy { it.value.distance }
            val totalDistanceWithCheat = paths.filter { it.key.p == targetPosition }
                .minOf { it.value.distance }

            var current = target.key
            var cheatOutPoint: PointWithCheat? = null
            var cheatInPoint: PointWithCheat? = null
            do {
                if (current.isCheatOutPoint) {
                    cheatOutPoint = current
                    cheatInPoint = paths[current]!!.previous.first { it.isCheatIn }
                    break
                }
                current = paths[current]?.previous?.firstOrNull() ?: break@outer

            } while (true)
            cheatPoints[CheatPoints(cheatInPoint!!.p, cheatOutPoint!!.p)] = totalDistanceWithCheat
            minDistance = totalDistanceWithCheat
        }

        val toReturn = cheatPoints.count { it.value <= (shortestTimeBaseline.distance - 100) }.toLong()
        return toReturn
    }


    data class PointWithCheatPt2(
        val p: Point,
        val cheatRemainingPicoseconds: Int,
    )

    fun part2(input: List<String>, minToSave: Int): Long {
        val grid = input.toGridMap()
        val startPosition = grid.values().first { it.value == 'S' }
            .run { PointWithCheatPt2(this.key, 20) }
        val targetPosition = grid.values().first { it.value == 'E' }.key

        val toVisitFromPos: (Point, Distance) -> Collection<Pair<Point, Distance>> = { point, _ ->
            point.cardinalNeighbours()
                .filter { it in grid }
                .filter { grid[it] != '#' }
                .map { it to 1 }
                .toSet()
        }
        val baselineFromSource = shortestPath(startPosition.p, toVisitFromPos = toVisitFromPos)

        val baselinePathPointsByDistanceToSource = baselineFromSource.entries
            .groupBy { it.value.distance }
            .run { TreeMap(this) }

        val baselineFromTarget = shortestPath(targetPosition, toVisitFromPos = toVisitFromPos)

        val baselineTargetDistance = baselineFromSource[targetPosition]!!.distance
        val maxDistanceToConsider = baselineTargetDistance - minToSave

        val pointsToTest = baselinePathPointsByDistanceToSource.filter { it.key <= maxDistanceToConsider }

        val cheatPoints = mutableMapOf<CheatPoints, Distance>()

        pointsToTest.flatMap { it.value }.forEach { (cheatPointStart, distanceWithPreviousNode) ->
            val distanceFromSource = baselineFromSource[cheatPointStart]!!.distance

            val possibleEndpointsOfShortcut =
                baselineFromTarget.filter { (it.value.distance + distanceFromSource) < maxDistanceToConsider }

            possibleEndpointsOfShortcut.filter { it.key.manhattanDistance(cheatPointStart) <= 20 }
                .forEach { (endPoint, distanceToTarget) ->
                    val cheatPointToAdd = CheatPoints(cheatPointStart, endPoint)
                    val totalDistance =
                        distanceFromSource + distanceToTarget.distance + endPoint.manhattanDistance(cheatPointStart)
                    val currentDistance = cheatPoints[cheatPointToAdd]
                    if (currentDistance == null || currentDistance > totalDistance) {
                        cheatPoints[cheatPointToAdd] = totalDistance
                    }
                }
        }

        val toReturn = cheatPoints.filter { it.value <= maxDistanceToConsider }.count().toLong()
        return toReturn
    }

    // test if implementation meets criteria from the description, like:
    // Check test inputs
    val testInput = """
        ###############
        #...#...#.....#
        #.#.#.#.#.###.#
        #S#...#.#.#...#
        #######.#.#.###
        #######.#.#...#
        #######.#.###.#
        ###..E#...#...#
        ###.#######.###
        #...###...#...#
        #.#####.#.###.#
        #.#...#.#.#...#
        #.#.#.#.#.#.###
        #...#...#...###
        ###############
    """.trimIndent().split("\n")
    utils.check(0, part1(testInput), "Part 1")
    utils.check(285, part2(testInput, 50), "Part 2")

    val input = aoc.input()
    measureTime {
        //part1(input).also { println("Part 1: $it") }
    }.also { it.println() }
    measureTime {
        part2(input, 100).also { println("Part 2: $it") }
    }.also { it.println() }
}