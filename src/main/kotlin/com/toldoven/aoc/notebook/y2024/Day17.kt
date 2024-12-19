package com.toldoven.aoc.notebook.y2024

import com.toldoven.aoc.notebook.AocClient
import utils.println
import utils.sessionTokenPath
import kotlin.time.measureTime

fun main() {

    val aoc = AocClient.fromFile(sessionTokenPath).interactiveDay(2024, 17)

    class Computer(
        private val instructions: List<Int>,
        private var registerA: Long = 0,
        private var registerB: Long = 0,
        private var registerC: Long = 0,
    ) {
        private var currentIdx = 0

        fun comboOperandOf(operand: Long) = when (operand) {
            0L, 1L, 2L, 3L -> operand
            4L -> registerA
            5L -> registerB
            6L -> registerC
            7L -> throw IllegalArgumentException("is reserved")
            else -> throw IllegalArgumentException("$operand not valid")
        }

        fun compute(): List<String> {
            val output = mutableListOf<String>()

            while (currentIdx in instructions.indices) {
                val operator = instructions[currentIdx++]
                val operand = instructions[currentIdx++].toLong()

                when (operator) {
                    0 -> {
                        registerA = (registerA / (Math.pow(2.0, comboOperandOf(operand).toDouble()))).toLong()
                    }

                    1 -> {
                        registerB = registerB.xor(operand)
                    }

                    2 -> {
                        registerB = comboOperandOf(operand).mod(8L)
                    }

                    3 -> {
                        if (registerA != 0L) {
                            currentIdx = operand.toInt()
                        }
                    }

                    4 -> {
                        registerB = registerB.xor(registerC)
                    }

                    5 -> {
                        output.add(comboOperandOf(operand).mod(8).toString())
                    }

                    6 -> {
                        registerB = (registerA / (Math.pow(2.0, comboOperandOf(operand).toDouble()))).toLong()
                    }

                    7 -> {
                        registerC = (registerA / (Math.pow(2.0, comboOperandOf(operand).toDouble()))).toLong()
                    }
                }
            }

            return output
        }
    }

    fun part1(input: List<String>): Long {
        val registerA = input[0].split("A: ")[1].toLong()
        val registerB = input[1].split("B: ")[1].toLong()
        val registerC = input[2].split("C: ")[1].toLong()

        val programInstructions = input[4].split(": ")[1].split(",").map { it.toInt() }

        val computer = Computer(programInstructions, registerA, registerB, registerC)

        computer.compute().also { it.joinToString(",").println() }

        return input.size.toLong()
    }

    fun part2(input: List<String>): Long {
        val registerA = input[0].split("A: ")[1].toLong()
        val registerB = input[1].split("B: ")[1].toLong()
        val registerC = input[2].split("C: ")[1].toLong()

        val listChars = "7777777777777777".toMutableList()

        val programInstructions = input[4].split(": ")[1].split(",").map { it.toInt() }

        fun dfs(curIdx: Int): Long? {
            val idxToCheck = programInstructions.size - curIdx - 1
            if (idxToCheck >= programInstructions.size || curIdx >= programInstructions.size) {
                return  listChars.joinToString("").toLong(8)
            }
            for (i in 0..7) {
                listChars[curIdx] = i.toString().toCharArray()[0]
                val result = Computer(programInstructions, listChars.joinToString("").toLong(8)).compute()

                if (result.size != programInstructions.size) {
                    continue
                }

                val instructionsShort = programInstructions.subList(idxToCheck, programInstructions.size)
                val resultShort = result.map { it.toInt() }.subList(idxToCheck, programInstructions.size)
                if (instructionsShort.indices.all { resultShort[it] == instructionsShort[it] } ) {
                    dfs(curIdx + 1)?.run { return this }
                }
            }
            listChars[curIdx] = '7'
            return null
        }

        val result = dfs(0).also { println("dfs is $it") }
        Computer(programInstructions, result!!, registerB, registerC).compute().also { it.println() }


        return result
    }

    // test if implementation meets criteria from the description, like:
    // Check test inputs
    val testInput = """
        Register A: 729
        Register B: 0
        Register C: 0

        Program: 0,1,5,4,3,0
    """.trimIndent().split("\n")

    val testInputPt2 = """
        Register A: 2024
        Register B: 0
        Register C: 0

        Program: 0,3,5,4,3,0
    """.trimIndent().split("\n")
    utils.check(testInput.size.toLong(), part1(testInput), "Part 1")

    val input = aoc.input()
    measureTime {
        part1(input).also { println("Part 1: $it") }
    }.also { it.println() }
    measureTime {
        part2(input).also { println("Part 2: $it") }
    }.also { it.println() }
}