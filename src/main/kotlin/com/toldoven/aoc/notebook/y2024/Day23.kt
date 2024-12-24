package com.toldoven.aoc.notebook.y2024

import com.toldoven.aoc.notebook.AocClient
import utils.println
import utils.sessionTokenPath
import kotlin.time.measureTime

fun main() {

    val aoc = AocClient.fromFile(sessionTokenPath).interactiveDay(2024, 23)

    fun part1(input: List<String>): Long {
        val nodeWithConnectionTo = mutableMapOf<String, MutableSet<String>>()

        input
            .map { it.split("-").run { this[0] to this[1] } }
            .forEach { (n1, n2) ->
                nodeWithConnectionTo.computeIfAbsent(n1) { mutableSetOf(n1) }.add(n2)
                nodeWithConnectionTo.computeIfAbsent(n2) { mutableSetOf(n2) }.add(n1)
            }

        val set: MutableSet<MutableSet<String>> = mutableSetOf()

        fun has(firstKey: String) {
            val connectedToFirstKey = nodeWithConnectionTo[firstKey]!!
            for (secondKey in connectedToFirstKey) {
                if (secondKey == firstKey) {
                    continue
                }
                val connectedToSecondKey = nodeWithConnectionTo[secondKey]!!
                val intersection = connectedToSecondKey intersect connectedToFirstKey
                if (intersection.size >= 3) {
                    connectedToSecondKey
                        .filter { it != firstKey && it != secondKey && it in intersection }
                        .map { setOf(firstKey, secondKey, it) }
                        .filter { originalSet -> originalSet.any { it.startsWith("t") } }
                        .forEach { originalSet ->
                            set.add(originalSet.toMutableSet())
                        }
                }
            }
        }

        nodeWithConnectionTo.forEach { has(it.key) }

        return set.size.toLong()
    }

    fun part2(input: List<String>): Long {
        val nodeWithConnectionTo = mutableMapOf<String, MutableSet<String>>()

        input
            .map { it.split("-").run { this[0] to this[1] } }
            .forEach { (n1, n2) ->
                nodeWithConnectionTo.computeIfAbsent(n1) { mutableSetOf(n1) }.add(n2)
                nodeWithConnectionTo.computeIfAbsent(n2) { mutableSetOf(n2) }.add(n1)
            }

        var maxSet: Set<String> = emptySet()

        val cache = mutableSetOf<Set<String>>()

        fun evaluate(toEvaluate: String, currentKeys: Set<String> = emptySet(), intersection: Set<String>) {
            val connectedToNodeEvaluated = nodeWithConnectionTo[toEvaluate]!!
            val newIntersection = connectedToNodeEvaluated intersect intersection
            val newKeys = currentKeys + toEvaluate
            cache.add(newKeys)
            if (newKeys.size > maxSet.size) {
                maxSet = newKeys
            }
            connectedToNodeEvaluated
                .filter { it !in newKeys }
                .filter { it in intersection }
                .filter { (newKeys + it) !in cache }
                .forEach { evaluate(it, newKeys, newIntersection) }
        }

        nodeWithConnectionTo.forEach {
            println("Testing ${it.key}")
            evaluate(it.key, setOf(it.key), it.value)
        }

        maxSet.toList().sorted().println()
        return input.size.toLong()
    }

// test if implementation meets criteria from the description, like:
// Check test inputs
    val testInput = """
        kh-tc
        qp-kh
        de-cg
        ka-co
        yn-aq
        qp-ub
        cg-tb
        vc-aq
        tb-ka
        wh-tc
        yn-cg
        kh-ub
        ta-co
        de-co
        tc-td
        tb-wq
        wh-td
        ta-ka
        td-qp
        aq-cg
        wq-ub
        ub-vc
        de-ta
        wq-aq
        wq-vc
        wh-yn
        ka-de
        kh-ta
        co-tc
        wh-qp
        tb-vc
        td-yn
    """.trimIndent().split("\n")
    utils.check(7, part1(testInput), "Part 1")
    utils.check(testInput.size.toLong(), part2(testInput), "Part 2")

    val input = aoc.input()
    measureTime {
        part1(input).also { println("Part 1: $it") }
    }.also { it.println() }
    measureTime {
        part2(input).also { println("Part 2: $it") }
    }.also { it.println() }
}