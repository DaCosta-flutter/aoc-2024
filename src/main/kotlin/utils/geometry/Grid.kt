package utils.geometry

import java.math.BigInteger
import java.util.*
import kotlin.collections.ArrayDeque
import kotlin.collections.set
import kotlin.math.max

class GridMap(
    val map: MutableMap<Point, Char>
) : Grid {
    override operator fun contains(point: Point) = point in this.map

    override operator fun get(point: Point): Char? = this.map[point]
    override operator fun set(point: Point, ch: Char) {
        this.map[point] = ch
    }

    override fun keys(): Set<Point> = this.map.keys
    override fun values(): Sequence<Map.Entry<Point, Char>> = map.asSequence()
}

class GridList(
    input: List<String>
) : Grid {
    private val values = input.map { it.toMutableList() }.toMutableList()
    private val xRange: IntRange = values[0].indices
    private val yRange: IntRange = values.indices
    private lateinit var keysPresent: Set<Point>

    override operator fun contains(point: Point) = point.x in xRange && point.y in yRange

    override operator fun get(point: Point): Char? = if (point in this) this.values[point.y][point.x] else null
    override operator fun set(point: Point, ch: Char) {
        this.values[point.y][point.x] = ch
    }

    override fun keys(): Set<Point> {
        if (!this::keysPresent.isInitialized) {
            keysPresent = xRange.flatMap { x -> yRange.map { y -> Point(x, y) } }.toSet()
        }
        return keysPresent
    }

    override fun values(): Sequence<Map.Entry<Point, Char>> = values
        .asSequence()
        .flatMapIndexed { y, str ->
            str.mapIndexed { x, ch ->
                AbstractMap.SimpleEntry(Point(x, y), ch)
            }
        }
}

interface Grid {
    operator fun contains(point: Point): Boolean
    operator fun get(point: Point): Char?
    operator fun set(point: Point, ch: Char)
    fun keys(): Set<Point>
    fun values(): Sequence<Map.Entry<Point, Char>>
    fun toMutableMap(): MutableMap<Point, Char> = values().map { it.key to it.value }.toMap().toMutableMap()

    fun bottomRightPoint() = this.keys().maxBy { it.x + it.y }

    fun get2DArray(nonFilled: Char = ' '): MutableList<MutableList<Char>> {
        val minY = max(0, this.keys().minOf { it.y })
        val minX = max(0, this.keys().minOf { it.x })
        val maxY = this.keys().maxOf { it.y }
        val maxX = this.keys().maxOf { it.x }

        return (minY..maxY).map { y ->
            (minX..maxX).map { x ->
                this[Point(x, y)] ?: nonFilled
            }.toMutableList()
        }.toMutableList()
    }

    fun printGrid(nonFilled: Char = ' ') {
        val arrayToPrint = this.get2DArray(nonFilled)
        arrayToPrint.forEach { line ->
            println()
            line.forEach { print(it) }
        }
        println()
    }

    fun floodFill(startPoint: Point, newValue: Char): Grid {
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
        return GridMap(newGrid)
    }

    /**
     * Calculate the area of a polygon, from its vertices coordinates - uses the Shoelace formula
     * https://en.wikipedia.org/wiki/Shoelace_formula
     * if charsToConsider is null, all positions in the grid will be considered
     */
    fun areaOfPolygon(charsToConsider: Char? = null): Long {
        val vertices = this.values()
            .filter { charsToConsider == null || it.value == charsToConsider }
            .map { it.key }
            .toList()

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
    fun numInteriorPoints(numBoundaryPoints: Long, charsToConsider: Char? = null) =
        this.areaOfPolygon(charsToConsider) - numBoundaryPoints / 2 + 1
}

fun List<String>.toGridMap(): GridMap =
    this.indices.flatMap { y ->
        this[y].indices.map { x ->
            Point(x, y)
        }
    }.associateWith { this.atPos(it) }
        .toMutableMap()
        .run { GridMap(this) }


fun List<String>.toGrid(): GridList = GridList(this)

fun Set<Point>.cartesianNeighboursInGrid(pos: Point) = pos.cardinalNeighbours().filter { it in this }.toSet()

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
    toVisitFromPos: (Point) -> Collection<Pair<Point, Distance>> = { curPos ->
        this.cartesianNeighboursInGrid(curPos).map { it to 1 }
    },
): Map<Point, DistanceWithPreviousNode<Point>> = shortestPath(startPos, targetPos, toVisitFromPos)

data class DistanceWithPreviousNode<T>(
    val distance: Distance,
    val previous: Set<T>
)

fun <T> shortestPathWithMinDistances(
    startPos: T,
    shouldStop: (Map<T, DistanceWithPreviousNode<T>>) -> Boolean = { true },
    toVisitFromPos: (T, Map<T, DistanceWithPreviousNode<T>>) -> Collection<Pair<T, Distance>>,
): Map<T, DistanceWithPreviousNode<T>> {
    val minDistanceByNode = mutableMapOf<T, DistanceWithPreviousNode<T>>().apply {
        put(startPos, DistanceWithPreviousNode(0, emptySet()))
    }
    val toVisit = PriorityQueue<T>(compareBy { minDistanceByNode[it]?.distance }).apply {
        add(startPos)
    }

    while (toVisit.isNotEmpty() && shouldStop(minDistanceByNode)) {
        val curNode = toVisit.remove()
        val (curDistance, _) = minDistanceByNode[curNode]!!
        toVisitFromPos(curNode, minDistanceByNode)
            .forEach { (newNode, distanceFromPrevious) ->
                val newDistance = curDistance + distanceFromPrevious
                val curDistanceByNode = minDistanceByNode[newNode]
                if (curDistanceByNode == null || newDistance < curDistanceByNode.distance) {
                    minDistanceByNode[newNode] = DistanceWithPreviousNode(newDistance, setOf(curNode))
                    toVisit.add(newNode)
                } else if (newDistance == curDistanceByNode.distance) {
                    minDistanceByNode[newNode] =
                        DistanceWithPreviousNode(newDistance, curDistanceByNode.previous + curNode)
                    //toVisit.add(newNode)
                }
            }
    }

    return minDistanceByNode
}

fun <T> shortestPath(
    startPos: T,
    targetPos: T? = null,
    toVisitFromPos: (T, Distance) -> Collection<Pair<T, Distance>>,
): Map<T, DistanceWithPreviousNode<T>> {
    val minDistanceByNode = mutableMapOf<T, DistanceWithPreviousNode<T>>().apply {
        put(startPos, DistanceWithPreviousNode(0, emptySet()))
    }
    val toVisit = PriorityQueue<T>(compareBy { minDistanceByNode[it]?.distance }).apply {
        add(startPos)
    }

    while (toVisit.isNotEmpty() && targetPos !in minDistanceByNode) {
        val curNode = toVisit.remove()
        val (curDistance, _) = minDistanceByNode[curNode]!!
        toVisitFromPos(curNode, curDistance)
            .forEach { (newNode, distanceFromPrevious) ->
                val newDistance = curDistance + distanceFromPrevious
                val curDistanceByNode = minDistanceByNode[newNode]
                if (curDistanceByNode == null || newDistance < curDistanceByNode.distance) {
                    minDistanceByNode[newNode] = DistanceWithPreviousNode(newDistance, setOf(curNode))
                    toVisit.add(newNode)
                } else if (newDistance == curDistanceByNode.distance) {
                    minDistanceByNode[newNode] =
                        DistanceWithPreviousNode(newDistance, curDistanceByNode.previous + curNode)
                    //toVisit.add(newNode)
                }
            }
    }

    return minDistanceByNode
}