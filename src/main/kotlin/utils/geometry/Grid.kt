package utils.geometry

import java.math.BigInteger
import java.util.LinkedList
import java.util.PriorityQueue
import kotlin.math.max
import kotlin.math.min

typealias Grid = Map<Point, Char>

fun List<String>.toGrid(): Grid =
    this.indices.flatMap { y ->
        this[y].indices.map { x ->
            Point(x, y)
        }
    }.associateWith { this.atPos(it) }

fun Set<Point>.cartesianNeighboursInGrid(pos: Point) = pos.cardinalNeighbours().filter { it in this }.toSet()

fun Grid.bottomRightPoint() = this.keys.maxBy { it.x + it.y }

fun Grid.get2DArray(nonFilled: Char = ' '): MutableList<MutableList<Char>> {
    val minY = max(0, this.minOf { it.key.y })
    val minX = max(0, this.minOf { it.key.x })
    val maxY = this.maxOf { it.key.y }
    val maxX = this.maxOf { it.key.x }

    return (minY..maxY).map { y ->
        (minX..maxX).map { x ->
            this[Point(x, y)] ?: nonFilled
        }.toMutableList()
    }.toMutableList()
}

fun Grid.printGrid(nonFilled: Char = ' ') {
    val arrayToPrint = this.get2DArray(nonFilled)
    arrayToPrint.forEach { line ->
        println()
        line.forEach { print(it) }
    }
    println()
}

fun Set<Point>.bfs(
    startPoint: Point,
    inclusionCriteria: (cur: Point, new: Point) -> Boolean = { _, _ -> true },
    toVisitFromPos: (Point) -> Collection<Point> = { curPos -> this.cartesianNeighboursInGrid(curPos) }
): List<Point> {
    val toVisit = LinkedList<Point>().apply { add(startPoint) }
    val visited = mutableSetOf<Point>()

    while (toVisit.isNotEmpty()) {
        val curPos = toVisit.pop()
        visited.add(curPos)
        toVisitFromPos(curPos)
            .filter { it !in visited }
            .filter { inclusionCriteria(curPos, it) }
            .forEach {
                toVisit.add(it)
            }
    }
    return visited.toList()
}

typealias Distance = Int

fun Set<Point>.shortestPath(
    startPos: Point,
    targetPos: Point? = null,
    toVisitFromPos: (Point) -> Collection<Pair<Point, Distance>> = { curPos -> this.cartesianNeighboursInGrid(curPos).map { it to 1 } },
): Map<Point, Distance> {
    val minDistanceByPoint = mutableMapOf<Point, Distance>().apply {
        put(startPos, 0)
    }
    val toVisit = PriorityQueue<Point>(compareBy { minDistanceByPoint[it] }).apply {
        add(startPos)
    }

    while (toVisit.isNotEmpty() && targetPos !in minDistanceByPoint) {
        val curPos = toVisit.remove()
        val curDistance = minDistanceByPoint[curPos]!!
        toVisitFromPos(curPos)
            .forEach { (newPos, distanceFromPrevious) ->
                val newDistance = curDistance + distanceFromPrevious
                val curDistanceByPoint = minDistanceByPoint[newPos]
                if (curDistanceByPoint == null || newDistance < curDistanceByPoint) {
                    minDistanceByPoint[newPos] = newDistance
                    toVisit.add(newPos)
                }
            }
    }

    return minDistanceByPoint
}

fun Grid.floodFill(startPoint: Point, newValue: Char): Grid {
    val originalValue = get(startPoint) ?: return this
    val visited = mutableSetOf<Point>()
    val queue = ArrayDeque<Point>()
    queue.add(startPoint)

    val newGrid = mutableMapOf<Point, Char>()

    while (queue.isNotEmpty()) {
        val current = queue.removeFirst()
        if (current in visited || get(current) != originalValue) continue
        newGrid[current] = newValue
        visited.add(current)
        queue.addAll(current.cardinalNeighbours())
    }
    return newGrid
}

/**
 * Calculate the area of a polygon, from its vertices coordinates - uses the Shoelace formula
 * https://en.wikipedia.org/wiki/Shoelace_formula
 * if charsToConsider is null, all positions in the grid will be considered
 */
fun Grid.areaOfPolygon(charsToConsider: Char? = null): Long {
    val vertices = this
        .filter { charsToConsider == null || it.value == charsToConsider }
        .keys.toList()

    var n = vertices.size - 1
    var area = BigInteger.ZERO

    for (i in 0 until n) {
        area = area.add(
            vertices[n].x.toBigInteger().add(vertices[i].x.toBigInteger())
                .multiply(vertices[n].y.toBigInteger().subtract(vertices[i].y.toBigInteger()))
        );
        n = i
    }

    return area.abs().divide(2.toBigInteger()).toLong()
}

/**
 * Uses Pick theorem
 * https://en.wikipedia.org/wiki/Pick%27s_theorem
 */
fun Grid.numInteriorPoints(numBoundaryPoints: Long, charsToConsider: Char? = null) =
    this.areaOfPolygon(charsToConsider) - numBoundaryPoints / 2 + 1