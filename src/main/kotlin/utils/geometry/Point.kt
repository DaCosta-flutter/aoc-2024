package utils.geometry

import kotlin.math.abs

data class Point(
    val x: Int,
    val y: Int,
)

operator fun Point.rem(sizeToCorrect: Point): Point {
    var newX = x % sizeToCorrect.x
    var newY = y % sizeToCorrect.y

    if (newX < 0) {
        newX += sizeToCorrect.x
    }
    if (newY < 0) {
        newY += sizeToCorrect.y
    }

    return this.copy(x = newX, y = newY)
}

fun Point.vecTo(o: Point) = Point(o.x - this.x, o.y - this.y)

operator fun Point.plus(o: Point) = copy(x = this.x + o.x, y = this.y + o.y)
operator fun Point.times(o: Int) = copy(x = this.x * o, y = this.y * o)
operator fun Point.unaryMinus() = this * -1

operator fun List<List<Char>>.get(pos: Point) = this[pos.y][pos.x]
fun List<String>.atPos(pos: Point) = this[pos.y][pos.x]

fun Point.isInside(list: List<List<Any>>) = x > 0 && y > 0 && y < list.size && x < list[y].size

fun Set<Point>.to2dList(ch: Char = '#', nonFilled: Char = ' '): MutableList<MutableList<Char>> {
    val size = Point(this.maxOf { it.x }, this.maxOf { it.y })

    return (0..size.y).map { y ->
        (0..size.x).map { x ->
            if (Point(x, y) in this) ch else nonFilled
        }.toMutableList()
    }.toMutableList()
}

fun Point.up() = this.moveTo(Direction.UP)
fun Point.down() = this.moveTo(Direction.DOWN)
fun Point.left() = this.moveTo(Direction.LEFT)
fun Point.right() = this.moveTo(Direction.RIGHT)

fun Point.cardinalNeighbours() = setOf(
    this.up(), this.down(), this.left(), this.right()
)

fun Point.neighbours() = Direction.entries.map { this.moveTo(it) }.toSet()

fun Point.manhattanDistance(other: Point): Int = abs(this.x - other.x) + abs(this.y - other.y)

enum class Direction {
    UP, UP_LEFT, LEFT, DOWN_LEFT, DOWN, DOWN_RIGHT, RIGHT, UP_RIGHT
}

fun Point.moveTo(d: Direction, num: Int = 1) = when (d) {
    Direction.UP -> copy(y = y - num)
    Direction.UP_LEFT -> copy(y = y - num, x = x - num)
    Direction.DOWN -> copy(y = y + num)
    Direction.DOWN_LEFT -> copy(y = y + num, x = x - num)
    Direction.LEFT -> copy(x = x - num)
    Direction.RIGHT -> copy(x = x + num)
    Direction.DOWN_RIGHT -> copy(x = x + num, y = y + num)
    Direction.UP_RIGHT -> copy(x = x + num, y = y - num)
}

fun Point.pointsTo(num: Int, d: Direction): List<Point> = buildList {
    var currentPos = this@pointsTo
    repeat(num) {
        currentPos = currentPos.moveTo(d)
        add(currentPos)
    }
}

data class Line(
    val m: Double,
    val b: Double
)

fun Line.yAt(x: Int) = m * x + b

fun Line.isIn(p: Point) = abs(this.yAt(p.x) - p.y) < 0.0000001

fun Point.lineTo(o: Point): Line {
    val m = (o.y - this.y) / (o.x - this.x).toDouble()
    val b = this.y - m * this.x
    return Line(m, b)
}