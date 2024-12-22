package com.toldoven.aoc.notebook.y2024

import com.toldoven.aoc.notebook.AocClient
import utils.println
import utils.sessionTokenPath
import java.math.BigDecimal
import kotlin.time.measureTime

typealias DiffSequence = List<Long>

fun main() {

    val aoc = AocClient.fromFile(sessionTokenPath).interactiveDay(2024, 22)

    fun next(num: Long, remaining: Int = 1): Long {
        if (remaining == 0) {
            return num
        }
        val toPrune = 16777216L
        var result = (num * 64) xor num
        result %= toPrune

        result = ((BigDecimal.valueOf(result).divide(BigDecimal.valueOf(32))).toDouble().toLong()) xor result
        result %= toPrune

        result = (result * 2048) xor result
        result %= toPrune

        return next(result, remaining - 1)
    }

    fun part1(input: List<String>): Long {
        val secretNumbers = input.map { it.toLong() }

        val after2000 = secretNumbers.map {
            next(it, 2000)
        }

        return after2000.sum()
    }

    data class Price(
        val price: Long,
        val diffToPrevious: Long,
        val matchingSequence: DiffSequence?
    )

    fun getAllPriceDifferencesFromSecretNumber(initialSecretNumber: Long): List<Price> {
        var currentSecretNumber = initialSecretNumber
        val prices = mutableListOf<Price>()
        (0 until 2000).forEach { idx ->
            val previousSecretNumber = currentSecretNumber
            currentSecretNumber = next(previousSecretNumber)
            val diffToPrevious = currentSecretNumber % 10 - previousSecretNumber % 10
            val matchingSequence = if (idx < 3) null else
                prices.subList(idx - 3, idx).map {
                    it.diffToPrevious
                } + diffToPrevious
            Price(
                currentSecretNumber % 10,
                diffToPrevious,
                matchingSequence
            ).run { prices.add(this) }
        }
        return prices
    }

    fun priceBySequence(prices: List<Price>): Map<DiffSequence, Price> {
        val priceBySequence = mutableMapOf<DiffSequence, Price>()

        prices.filter { it.matchingSequence != null }.forEach {
            if (it.matchingSequence !in priceBySequence) {
                priceBySequence[it.matchingSequence!!] = it
            }
        }
        return priceBySequence
    }

    fun part2(input: List<String>): Long {
        val secretNumbers = input.map { it.toLong() }

        val priceBySequences = secretNumbers
            .map { getAllPriceDifferencesFromSecretNumber(it) }
            .map { priceBySequence(it) }

        val allSequences = priceBySequences.flatMap { it.keys }.toSet()

        val max = allSequences.maxOf { sequence ->
            priceBySequences.mapNotNull { map -> map[sequence]?.price }.sum()
        }

        return max
    }

// test if implementation meets criteria from the description, like:
// Check test inputs
    val testInput = """
        1
        10
        100
        2024
    """.trimIndent().split("\n")
    utils.check(37327623L, part1(testInput), "Part 1")

    val testInputPt2 = """
       1
       2
       3
       2024
    """.trimIndent().split("\n")
    utils.check(23, part2(testInputPt2), "Part 2")

    val input = aoc.input()
    measureTime {
        part1(input).also { println("Part 1: $it") }
    }.also { it.println() }
    measureTime {
        part2(input).also { println("Part 2: $it") }
    }.also { it.println() }
}