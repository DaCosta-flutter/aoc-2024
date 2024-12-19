package com.toldoven.aoc.notebook.y2024

import com.toldoven.aoc.notebook.AocClient
import utils.geometry.*
import utils.println
import utils.sessionTokenPath
import java.util.*
import kotlin.time.measureTime

enum class Edge {
    UP_LEFT,
    UP_RIGHT,
    DOWN_LEFT,
    DOWN_RIGHT
}

fun main() {

    val aoc = AocClient.fromFile(sessionTokenPath).interactiveDay(2024, 12)

    data class Region(
        val letter: Char,
        val points: Set<Point>
    )

    fun Region.area() = this.points.size

    fun Region.perimeter(): Long = this.points.sumOf {
        it.cardinalNeighbours().count { neigh -> neigh !in this.points }
    }.toLong()


    fun Region.edgesOf(pos: Point): Set<Direction> =
        setOf(Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT)
            .filter { pos.moveTo(it) !in this.points }
            .toSet()

    fun Direction.directionToMoveFromEdge(): Set<Direction> = when (this) {
        Direction.UP, Direction.DOWN -> setOf(Direction.LEFT, Direction.RIGHT)
        Direction.LEFT, Direction.RIGHT -> setOf(Direction.UP, Direction.DOWN)
        else -> throw IllegalArgumentException()
    }

    fun Region.sidesScan(): Long {
        val pointsInPerimeter = points
            .filter { it.cardinalNeighbours().any { p -> p !in points } }
            .toSet()

        val minX = pointsInPerimeter.minOf { it.x }
        val minY = pointsInPerimeter.minOf { it.y }
        val maxX = pointsInPerimeter.maxOf { it.x }
        val maxY = pointsInPerimeter.maxOf { it.y }

        var numSides = 0
        (minY..maxY + 1)
            .forEach { y ->
                var lastEdgeDownX: Int? = null
                var lastEdgeUpX: Int? = null
                (minX..maxX).forEach { x ->
                    val up = Point(x, y - 1)
                    val down = Point(x, y + 1)
                    if (Point(x, y) in pointsInPerimeter && down !in points) {
                        if (lastEdgeDownX == null || lastEdgeDownX!! < (x - 1)) {
                            numSides++
                        }
                        lastEdgeDownX = x
                    }
                    if (Point(x, y) in pointsInPerimeter && up !in points) {
                        if (lastEdgeUpX == null || lastEdgeUpX!! < (x - 1)) {
                            numSides++
                        }
                        lastEdgeUpX = x
                    }
                }
            }

        GridMap(this.points.associateWith { 'A' }.toMutableMap()).printGrid()
        GridMap(pointsInPerimeter.associateWith { 'A' }.toMutableMap()).printGrid()
        println("Num sides ${numSides * 2}")


        return numSides.toLong() * 2
    }

    fun Region.numberOfSides(): Long {
        val hasSides = mutableSetOf<Pair<Point, Direction>>()

        val pointsInPerimeter = points
            .filter { it.cardinalNeighbours().any { p -> p !in points } }
            .toSet()
        var numSides = 0
        val visited = mutableSetOf<Point>()
        val toVisit = LinkedList<Point>().apply { this.add(pointsInPerimeter.first()) }

        fun hasAnySideUntilEndOfEdge(edgeDirection: Direction, pos: Point): Boolean =
            edgeDirection.directionToMoveFromEdge().any { direction ->
                var currentPos = pos.moveTo(direction)
                while (currentPos in pointsInPerimeter) {
                    if (edgeDirection !in this.edgesOf(currentPos)) {
                        return@any false
                    }
                    if ((currentPos to edgeDirection) !in hasSides) {
                        return@any true
                    }
                    currentPos = currentPos.moveTo(direction)
                }
                false
            }

        while (toVisit.isNotEmpty()) {
            val current = toVisit.removeFirst()
            visited.add(current)

            val sides = this.edgesOf(current)
            sides.forEach { side ->
                hasSides.add(current to side)
                if (!hasAnySideUntilEndOfEdge(side, current)) {
                    numSides++
                }
            }
            current.cardinalNeighbours().filter { it in points }
                .filter { it !in visited }
                .filter { it !in toVisit }
                .forEach { toVisit.addLast(it) }
        }

        return numSides.toLong()
    }

    fun Grid.findRegionFrom(p: Point, currentPoints: MutableSet<Point> = mutableSetOf()): Set<Point> {
        val thisRegion = this[p]
        currentPoints.add(p)
        p.cardinalNeighbours()
            .asSequence()
            .filter { it !in currentPoints }
            .filter { this[it] == thisRegion }
            .forEach { this.findRegionFrom(it, currentPoints) }

        return currentPoints
    }

    fun part1(input: List<String>): Long {
        val grid = input.toGrid()

        val regions = mutableListOf<Region>()
        val visited = mutableSetOf<Point>()

        grid.keys()
            .asSequence()
            .filter { it !in visited }
            .forEach {
                grid.findRegionFrom(it).run {
                    regions.add(Region(grid[it]!!, this))
                    visited.addAll(this)
                }
            }

        return regions.sumOf { it.area() * it.perimeter() }
    }

    fun part2(input: List<String>): Long {
        val grid = input.toGrid()

        val regions = mutableListOf<Region>()
        val visited = mutableSetOf<Point>()

        grid.keys()
            .asSequence()
            .filter { it !in visited }
            .forEach {
                grid.findRegionFrom(it).run {
                    regions.add(Region(grid[it]!!, this))
                    visited.addAll(this)
                }
            }

        return regions.sumOf { it.area() * it.numberOfSides() }
    }
    // test if implementation meets criteria from the description, like:
    // Check test inputs
    val testInput = """
        RRRRIICCFF
        RRRRIICCCF
        VVRRRCCFFF
        VVRCCCJFFF
        VVVVCJJCFE
        VVIVCCJJEE
        VVIIICJJEE
        MIIIIIJJEE
        MIIISIJEEE
        MMMISSJEEE
    """.trimIndent().split("\n")
    utils.check(1930, part1(testInput), "Part 1")

    val otherTestInput = """
        AAAAAA
        AAABBA
        AAABBA
        ABBAAA
        ABBAAA
        AAAAAA
    """.trimIndent().split("\n")
    utils.check(368, part2(otherTestInput), "Part 2")
    utils.check(1206, part2(testInput), "Part 2")
    utils.check(
        80, part2(
            """
        AAAA
        BBCD
        BBCC
        EEEC
    """.trimIndent().split("\n")
        ), "Part 2"
    )
    utils.check(
        236, part2(
            """
        EEEEE
        EXXXX
        EEEEE
        EXXXX
        EEEEE
    """.trimIndent().split("\n")
        ), "Part 2"
    )

    val input = aoc.input()
    measureTime {
        part1(input).also { println("Part 1: $it") }
    }.also { it.println() }
    measureTime {
        part2(input).also { println("Part 2: $it") }
    }.also { it.println() }
}