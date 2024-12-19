package com.toldoven.aoc.notebook.y2024

import com.toldoven.aoc.notebook.AocClient
import utils.geometry.*
import utils.println
import utils.sessionTokenPath
import kotlin.time.measureTime

fun main() {

    val aoc = AocClient.fromFile(sessionTokenPath).interactiveDay(2024, 15)

    fun GridMap.tryMove(pos: Point, d: Direction): Boolean {
        val toMoveTo = pos.moveTo(d)
        if (this[toMoveTo] == '.') {
            this[toMoveTo] = this[pos]!!
            this[pos] = '.'
            return true
        } else if (this[toMoveTo] == '#') {
            return false
        } else if (this[toMoveTo] == 'O') {
            val wasMoved = this.tryMove(toMoveTo, d)
            if (wasMoved) {
                this[toMoveTo] = this[pos]!!
                this[pos] = '.'
                return true
            } else {
                return false
            }
        }
        return false
    }

    fun Grid.counterPartPos(pos: Point) = when (this[pos]) {
        '[' -> pos.moveTo(Direction.RIGHT)
        ']' -> pos.moveTo(Direction.LEFT)
        else -> throw IllegalArgumentException()
    }

    fun GridMap.canMove(
        pos: Point,
        d: Direction,
        counterPartWasChecked: Boolean = false,
        checked: Set<Point> = emptySet()
    ): Boolean {
        val toMoveTo = pos.moveTo(d)
        val toMoveToValue = this[toMoveTo]

        return when (toMoveToValue) {
            '.' -> true
            '[', ']' -> {
                val counterPart = this.counterPartPos(toMoveTo)
                val newchecked = checked + counterPart + pos
                this.canMove(toMoveTo, d, false, newchecked) &&
                        (counterPart in checked || this.canMove(counterPart, d, false, newchecked))
            }

            else -> false
        }
    }

    fun GridMap.tryMovePt2(pos: Point, d: Direction, counterPartMoved: Boolean = false): Map<Point, Char> {
        val toMoveTo = pos.moveTo(d)
        val toMoveToValue = this[toMoveTo]!!
        val thisPosValue = this[pos]!!
        if (thisPosValue == '[' || thisPosValue == ']') {
            val counterPartPos = this.counterPartPos(pos)
            if (this.canMove(pos, d) && (counterPartMoved || this.canMove(counterPartPos, d))) {
                val toReturn = mutableMapOf<Point, Char>()
                if (toMoveToValue == '[' || toMoveToValue == ']') {
                    toReturn.putAll(this.tryMovePt2(toMoveTo, d, toMoveTo == counterPartPos))
                }
                if (!counterPartMoved) {
                    this.tryMovePt2(counterPartPos, d, true).forEach { k, v ->
                        if (v != '.' || k !in toReturn) {
                            toReturn[k] = v
                        }
                    }
                }
                toReturn.putAll(mapOf(toMoveTo to thisPosValue))
                if (pos !in toReturn) {
                    toReturn[pos] = '.'
                }
                return toReturn
            }
            return emptyMap()
        }

        if (toMoveToValue == '.') {
            return mapOf(toMoveTo to thisPosValue, pos to '.')
        } else if (toMoveToValue == '[' || toMoveToValue == ']') {
            val changedPoints = this.tryMovePt2(toMoveTo, d)
            if (changedPoints.isNotEmpty()) {
                return changedPoints + mapOf(toMoveTo to thisPosValue) + (if (pos !in changedPoints) mapOf(pos to '.') else emptyMap())
            } else {
                return emptyMap()
            }
        }
        return emptyMap()
    }

    fun parseInput(input: List<String>): Pair<GridMap, List<Direction>> {
        val grid = input.takeWhile { it.isNotBlank() }.toGridMap()

        val sequence = input.indexOf("").run {
            val sequenceStrs = input.subList(this, input.size)
            sequenceStrs
                .asSequence()
                .flatMap { str ->
                    str.filter { it != '\n' }.map { ch ->
                        when (ch) {
                            '<' -> Direction.LEFT
                            '>' -> Direction.RIGHT
                            'v' -> Direction.DOWN
                            '^' -> Direction.UP
                            else -> throw IllegalArgumentException()
                        }
                    }
                }.toList()
        }
        return Pair(grid, sequence)
    }

    fun part1(input: List<String>): Long {
        val (grid, sequence) = parseInput(input)

        var robotPosition = grid.values().filter { it.value == '@' }.map { it.key }.first()

        sequence.forEach { dir ->
            val wasMoved = grid.tryMove(robotPosition, dir)
            if (wasMoved) {
                robotPosition = robotPosition.moveTo(dir)
            }
        }

        return grid.values().filter { it.value == 'O' }.sumOf { it.key.y * 100 + it.key.x.toLong() }
    }

    fun part2(input: List<String>): Long {
        val indexOfEmpty = input.indexOf("")
        var inputChanged = input.mapIndexed { i, line ->
            if (i >= indexOfEmpty) {
                line
            } else {
                line.map { ch ->
                    when (ch) {
                        '#' -> "##"
                        '.' -> ".."
                        'O' -> "[]"
                        '@' -> "@."
                        else -> throw IllegalArgumentException("$ch not allowed")
                    }
                }.joinToString("")
            }
        }
        /*
                inputChanged = """
                ##....##..##[]......[]...@##..........[][]..[].............[]........[]...####..[]..........[]....##
                ##....##[]..##..........[]...[][].....##....[]....[]##........[]...[][]...........##............####
                ##..[][]##[]...........[]...............[][][][]....##......[].[]...[]....[]........[]......##....##
                ##..[][]..[]..[]......[][]...........[].....####[]...[][]...[]..........[]..[][]......[]......[][]##
                ##..##[]........[][]..[]...[].......[]........##[]##..[][]..........[]..........##....##[]........##
                ##............##[]...[].[]##...[].......##..##..##....[]##................[]....[][][]....[]....[]##
                ##[]..##....[]####..[][][]............[]..##[]..[]..##[]..##....##........[]....[][]......[][]....##
                ##[]..........[][]..[]..[]##[]....####....##............[]......[][]..[][][][]..[]..##..##........##
                ####[]......##.[]...##....[]......[][]..........##.......[][]...##....[]....[]..##..[][]....##....##

                vvvvv
            """.trimIndent().split("\n")*/

        val (grid, sequence) = parseInput(inputChanged)

        var robotPosition = grid.values().filter { it.value == '@' }.map { it.key }.first()

        grid.printGrid()

        sequence.forEach { dir ->

            val movedPoints = grid.tryMovePt2(robotPosition, dir)
            movedPoints.forEach { (t, u) -> grid[t] = u }
            robotPosition = grid.values().filter { it.value == '@' }.map { it.key }.first()

            if (movedPoints.values.any { it == '[' }) {
                grid.printGrid()
            }
            println("moved to $dir")
            //readln()
        }
        grid.printGrid()

        return grid.values().filter { it.value == '[' }.sumOf { it.key.y * 100 + it.key.x.toLong() }
    }

// test if implementation meets criteria from the description, like:
// Check test inputs
    var testInput = """
        ##########
        #..O..O.O#
        #......O.#
        #.OO..O.O#
        #..O@..O.#
        #O#..O...#
        #O..O..O.#
        #.OO.O.OO#
        #....O...#
        ##########

        <vv>^<v^>v>^vv^v>v<>v^v<v<^vv<<<^><<><>>v<vvv<>^v^>^<<<><<v<<<v^vv^v>^
        vvv<<^>^v^^><<>>><>^<<><^vv^^<>vvv<>><^^v>^>vv<>v<<<<v<^v>^<^^>>>^<v<v
        ><>vv>v^v^<>><>>>><^^>vv>v<^^^>>v^v^<^^>v^^>v^<^v>v<>>v^v^<v>v^^<^^vv<
        <<v<^>>^^^^>>>v^<>vvv^><v<<<>^^^vv^<vvv>^>v<^^^^v<>^>vvvv><>>v^<<^^^^^
        ^><^><>>><>^^<<^^v>>><^<v>^<vv>>v>>>^v><>^v><<<<v>>v<v<v>vvv>^<><<>^><
        ^>><>^v<><^vvv<^^<><v<<<<<><^v<<<><<<^^<v<^^^><^>>^<v^><<<^>>^v<v^v<v^
        >^>>^v>vv>^<<^v<>><<><<v<<v><>v<^vv<<<>^^v^>^^>>><<^v>>v^v><^^>>^<>vv^
        <><^^>^^^<><vvvvv^v<v<<>^v<v>v<<^><<><<><<<^^<<<^<<>><<><^^^>^^<>^>v<>
        ^^>vv<^v^v<vv>^<><v<^v>^^^>>>^^vvv^>vvv<>>>^<^>>>>>^<<^v>^vvv<>^<><<v>
        v^^>>><<^^<>>^v^<v^vv<>v^<<>^<^v^v><^<<<><<^<v><v<>vv>>v><v^<vv<>v^<<^
    """.trimIndent().split("\n")
    /*testInput = """
       ##########
       #..O..O.O#
       #......O.#
       #.OO..O.O#
       #..O@OO..#
       #O#..O...#
       #O..O..O.#
       #.OO.O.OO#
       #....O...#
       ##########

       >>>>^<v^>v>^vv^v>v<>v^v<v<^vv<<<^><<><>>v<vvv<>^v^>^<<<><<v<<<v^vv^v>^
       vvv<<^>^v^^><<>>><>^<<><^vv^^<>vvv<>><^^v>^>vv<>v<<<<v<^v>^<^^>>>^<v<v
       ><>vv>v^v^<>><>>>><^^>vv>v<^^^>>v^v^<^^>v^^>v^<^v>v<>>v^v^<v>v^^<^^vv<
       <<v<^>>^^^^>>>v^<>vvv^><v<<<>^^^vv^<vvv>^>v<^^^^v<>^>vvvv><>>v^<<^^^^^
       ^><^><>>><>^^<<^^v>>><^<v>^<vv>>v>>>^v><>^v><<<<v>>v<v<v>vvv>^<><<>^><
       ^>><>^v<><^vvv<^^<><v<<<<<><^v<<<><<<^^<v<^^^><^>>^<v^><<<^>>^v<v^v<v^
       >^>>^v>vv>^<<^v<>><<><<v<<v><>v<^vv<<<>^^v^>^^>>><<^v>>v^v><^^>>^<>vv^
       <><^^>^^^<><vvvvv^v<v<<>^v<v>v<<^><<><<><<<^^<<<^<<>><<><^^^>^^<>^>v<>
       ^^>vv<^v^v<vv>^<><v<^v>^^^>>>^^vvv^>vvv<>>>^<^>>>>>^<<^v>^vvv<>^<><<v>
       v^^>>><<^^<>>^v^<v^vv<>v^<<>^<^v^v><^<<<><<^<v><v<>vv>>v><v^<vv<>v^<<^
    """.trimIndent().split("\n")*/

    val smallExampel = """
        #######
        #...#.#
        #.....#
        #..OO@#
        #..O..#
        #.....#
        #######

        <vv<<^^<<^^
    """.trimIndent().split("\n")

    val anotherExample = """
        #######
        #.....#
        #.OO@.#
        #.....#
        #######

        <<
    """.trimIndent().split("\n")


    val anotherExample2 = """
        #######
        #.....#
        #.O#..#
        #..O@.#
        #.....#
        #######

        <v<<^
    """.trimIndent().split("\n")

    val example511 = """
        #######
        #.....#
        #.#O..#
        #..O@.#
        #.....#
        #######

        <v<^
    """.trimIndent().split("\n")

    val example816 = """
        ######
        #....#
        #.O..#
        #.OO@#
        #.O..#
        #....#
        ######

        <vv<<^
    """.trimIndent().split("\n")

    val customExample = """
        ########
        #@O.O..#
        ########

        >>>>>>>>>
    """.trimIndent().split("\n")

    part1(testInput)
    utils.check(222, part2(customExample), "Part 2")
//part2(smallExampel)
    utils.check(816, part2(example816), "Part 2")
    utils.check(406, part2(anotherExample), "Part 2")
    utils.check(509, part2(anotherExample2), "Part 2")
    utils.check(511, part2(example511), "Part 2")

    println("Starting last check")
    utils.check(9021, part2(testInput), "Part 2")

    val input = aoc.input()
    measureTime {
        part1(input).also { println("Part 1: $it") }
    }.also { it.println() }
    measureTime {
        part2(input).also { println("Part 2: $it") }
    }.also { it.println() }
}