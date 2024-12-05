package com.toldoven.aoc.notebook

import utils.println
import utils.sessionTokenPath

typealias Rules = Map<Int, Set<Int>>

fun main() {

    data class Message(
        val numbers: List<Int>,
        val indexByNumber: Map<Int, List<Int>>,
    )


    data class Input(
        val valuesAfterXRules: Rules,
        val messages: List<Message>,
    )

    fun Message.isValid(rules: Rules): Boolean {
        return this.numbers
            .mapIndexed { i, v -> Pair(i, v) }
            .all { (idx, num) ->
                rules[num].orEmpty()
                    .map { numShouldBeAfter -> indexByNumber[numShouldBeAfter].orEmpty() }
                    .all { indicesShouldBeAfter -> indicesShouldBeAfter.all { it > idx } }
            }
    }

    fun Message.sort(rules: Rules): List<Int> {
        return this.numbers.sortedWith() { o1, o2 ->
            val o1rules = rules[o1].orEmpty()
            val o2Rules = rules[o2].orEmpty()

            if (o2 in o1rules) {
                -1
            } else if (o1 in o2Rules) {
                +1
            } else 0
        }
    }

    fun parse(strings: List<String>): Input {
        var reachedEndRules = false
        var curIdx = 0
        val valuesAfterXRules: HashMap<Int, MutableSet<Int>> = HashMap()
        while (!reachedEndRules) {
            var str = strings[curIdx]
            val rules = str.split("|").map { it.toInt() }
            valuesAfterXRules.computeIfAbsent(rules[0]) { mutableSetOf() }.add(rules[1])
            reachedEndRules = strings[++curIdx].isBlank()
        }

        val messages = strings.subList(curIdx + 1, strings.size)
            .map { str ->
                val list = str.trim().split(",")
                    .map { it.toInt() }
                val mapped = list.mapIndexed { i, v -> Pair(i, v) }
                    .groupBy { it.second }
                    .mapValues { listPairs -> listPairs.value.map { it.first } }
                Message(list, mapped)
            }
        return Input(valuesAfterXRules, messages)
    }

    fun part1(input: List<String>): Long {
        val inp = parse(input)

        return inp.messages.filter { it.isValid(inp.valuesAfterXRules) }
            .map { it.numbers[it.numbers.lastIndex / 2].toLong() }
            .sum()
    }

    fun part2(input: List<String>): Long {
        val inp = parse(input)

        return inp.messages.filterNot { it.isValid(inp.valuesAfterXRules) }
            .map { it.sort(inp.valuesAfterXRules) }
            .also { it.println() }
            .map { it[it.lastIndex / 2].toLong() }
            .also { it.println() }
            .sum()
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