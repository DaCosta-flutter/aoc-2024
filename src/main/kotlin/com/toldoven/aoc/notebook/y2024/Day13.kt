package com.toldoven.aoc.notebook.y2024

import com.toldoven.aoc.notebook.AocClient
import org.apache.commons.math3.linear.Array2DRowRealMatrix
import org.apache.commons.math3.linear.ArrayRealVector
import org.apache.commons.math3.linear.DecompositionSolver
import org.apache.commons.math3.linear.LUDecomposition
import utils.geometry.Point
import utils.geometry.plus
import utils.isLong
import utils.println
import utils.sessionTokenPath
import kotlin.math.roundToLong
import kotlin.time.measureTime

fun main() {

    val aoc = AocClient.fromFile(sessionTokenPath).interactiveDay(2024, 13)


    data class CacheKey(
        val originPos: Point,
        val numAPresses: Int,
        val numBPresses: Int
    )

    data class Arcade(
        val aVector: Point,
        val bVector: Point,
        val prize: Point,
    )

    fun Arcade.tryPrize(
        currentPos: Point = Point(0, 0),
        currentNumberOfPressesA: Int = 0,
        currentNumberOfPressesB: Int = 0,
        minCostsFromPos: MutableMap<CacheKey, Long?> = mutableMapOf()
    ): Long? {
        val cacheKey = CacheKey(currentPos, currentNumberOfPressesA, currentNumberOfPressesB)
        if (cacheKey in minCostsFromPos) {
            return minCostsFromPos[cacheKey]
        }
        if (currentPos == prize) {
            return 0
        } else if (currentPos.x > prize.x || currentPos.y > prize.y || currentNumberOfPressesA > 100 || currentNumberOfPressesB > 100) {
            return null
        }
        val aCost =
            tryPrize(currentPos + aVector, currentNumberOfPressesA + 1, currentNumberOfPressesB, minCostsFromPos)?.plus(
                3
            )
        val bCost =
            tryPrize(currentPos + bVector, currentNumberOfPressesA, currentNumberOfPressesB + 1, minCostsFromPos)?.plus(
                1
            )

        return setOf(aCost, bCost).filterNotNull().minOrNull().also {
            minCostsFromPos[cacheKey] = it
        }
    }

    fun Arcade.tryPrizePt2(factor: Double = 0.0): Long? {
        val coefficients = Array2DRowRealMatrix(
            arrayOf(
                doubleArrayOf(aVector.x.toDouble(), bVector.x.toDouble()),  // Coefficients for equation 1
                doubleArrayOf(aVector.y.toDouble(), bVector.y.toDouble())  // Coefficients for equation 2
            )
        )

        val constants =
            ArrayRealVector(
                doubleArrayOf(
                    prize.x.toDouble() + factor,
                    prize.y.toDouble() + factor
                )
            )

        val solver: DecompositionSolver = LUDecomposition(coefficients).solver
        val solution = solver.solve(constants)
        val numAPresses = solution.getEntry(0).run {
            if (!this.isLong() || this < 0.0 || this.isNaN() || this.isInfinite()) {
                return null
            }
            this.roundToLong()
        }
        val numBPresses = solution.getEntry(1).run {
            if (!this.isLong() || this < 0.0 || this.isNaN() || this.isInfinite()) {
                return null
            }
            this.roundToLong()
        }
        return numAPresses * 3 + numBPresses
    }

    fun parseArcades(input: List<String>): List<Arcade> {
        val arcades = mutableListOf<Arcade>()
        var i = 0
        while (i in input.indices) {
            val aVector = input[i++].split(": ")[1].split(", ").map { it.trim() }.run {
                Point(this[0].split("X+")[1].toInt(), this[1].split("Y+")[1].toInt())
            }
            val bVector = input[i++].split(": ")[1].split(", ").map { it.trim() }.run {
                Point(this[0].split("X+")[1].toInt(), this[1].split("Y+")[1].toInt())
            }

            val prize = input[i++].split(": ")[1].split(", ").map { it.trim() }.run {
                Point(this[0].split("X=")[1].toInt(), this[1].split("Y=")[1].toInt())
            }
            i++
            arcades.add(Arcade(aVector, bVector, prize))
        }
        return arcades
    }

    fun part1(input: List<String>): Long {
        val arcades = parseArcades(input)

        val amountToWin = arcades.map { it.tryPrizePt2() }
        return amountToWin.filterNotNull().sum()
    }

    fun part2(input: List<String>): Long {
        val arcades = parseArcades(input)

        val amountToWin = arcades.map { it.tryPrizePt2(factor = 10000000000000.0) }
        return amountToWin.filterNotNull().sum()
    }

    // test if implementation meets criteria from the description, like:
    // Check test inputs
    val testInput = """
        Button A: X+94, Y+34
        Button B: X+22, Y+67
        Prize: X=8400, Y=5400

        Button A: X+26, Y+66
        Button B: X+67, Y+21
        Prize: X=12748, Y=12176

        Button A: X+17, Y+86
        Button B: X+84, Y+37
        Prize: X=7870, Y=6450

        Button A: X+69, Y+23
        Button B: X+27, Y+71
        Prize: X=18641, Y=10279
    """.trimIndent().split("\n")
    utils.check(480L, part1(testInput), "Part 1")

    val input = aoc.input()
    measureTime {
        part1(input).also { println("Part 1: $it") }
    }.also { it.println() }
    measureTime {
        part2(input).also { println("Part 2: $it") }
    }.also { it.println() }
}