package com.toldoven.aoc.notebook

import utils.println
import utils.sessionTokenPath

typealias Rules = Map<Int, Set<Int>>
typealias Message = List<Int>

fun main() {

    data class Input(
        val precedenceRules: Rules,
        val messages: List<Message>,
    )

    fun Message.sort(rules: Rules): List<Int> {
        return this.sortedWith() { o1, o2 ->
            val numbersMustBeAfter1 = rules[o1].orEmpty()
            val numbersMustBeAfter2 = rules[o2].orEmpty()

            if (o2 in numbersMustBeAfter1) {
                -1
            } else if (o1 in numbersMustBeAfter2) {
                +1
            } else 0
        }
    }

    fun parse(strings: List<String>): Input {
        val rules = strings.takeWhile { it.isNotBlank() }
            .map { str -> str.split("|").map(String::toInt) }
            .groupBy({ it[0] }, { it[1] })
            .mapValues { it.value.toSet() }

        val messages = strings
            .reversed()
            .takeWhile { it.isNotBlank() }
            .map { str -> str.trim().split(",").map { it.toInt() } }
        return Input(rules, messages)
    }

    fun part1(input: List<String>): Long {
        val inp = parse(input)

        return inp.messages.filter { it.sort(inp.precedenceRules) == it }
            .sumOf { it[it.lastIndex / 2].toLong() }
    }

    fun part2(input: List<String>): Long {
        val inp = parse(input)

        return inp.messages
            .mapNotNull { msg -> msg.sort(inp.precedenceRules).run { if (this != msg) this else null } }
            .sumOf { it[it.lastIndex / 2].toLong() }
    }

    // test if implementation meets criteria from the description, like:
    // Check test inputs
    val testInput = """
        47|53
        97|13
        97|61
        97|47
        75|29
        61|13
        75|53
        29|13
        97|29
        53|29
        61|53
        97|53
        61|29
        47|13
        75|47
        97|75
        47|61
        75|61
        47|29
        75|13
        53|13

        75,47,61,53,29
        97,61,53,29,13
        75,29,13
        75,97,47,61,53
        61,13,29
        97,13,75,29,47
    """.trimIndent().split("\n")
    utils.check(143, part1(testInput), "Part 1")
    utils.check(123, part2(testInput), "Part 2")

    val aoc = AocClient.fromFile(sessionTokenPath).interactiveDay(2024, 5)
    val input = aoc.input()
    val resultPart1 = part1(input).also { it.println() }
    //aoc.submitPartOne(resultPart1)
    val resultPart2 = part2(input).also { it.println() }
    //aoc.submitPartTwo(resultPart2)
}