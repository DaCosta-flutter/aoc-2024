package com.toldoven.aoc.notebook

import utils.println
import utils.sessionTokenPath
import java.util.*
import kotlin.time.measureTime

fun main() {

    val aoc = AocClient.fromFile(sessionTokenPath).interactiveDay(2024, 9)


    data class File(
        val id: Int,
        val numBlocks: Int,
        var startIdx: Int,
        var freeBlocks: Int
    )

    data class Disk(
        val files: SortedMap<Int, File>,
        val diskRepresentation: MutableList<Int?>
    )

    fun parseDisk(input: List<String>): Disk {
        var currentId = 0;
        val line = input[0]
        val disk = mutableListOf<Int?>()
        val files = sortedMapOf<Int, File>()

        while ((currentId * 2) in line.indices) {
            val fileBlocks = line[2 * currentId].toString().toInt()
            val startIdx = disk.size
            repeat(fileBlocks) { disk.add(currentId) }
            var freeBlocks = 0
            if ((2 * currentId + 1) in line.indices) {
                freeBlocks = line[2 * currentId + 1].toString().toInt()
                repeat(freeBlocks) { disk.add(null) }
            }
            files[startIdx] = File(currentId, fileBlocks, startIdx, freeBlocks)
            currentId++
        }
        return Disk(files, disk)
    }

    fun part1(input: List<String>): Long {
        val disk = parseDisk(input).diskRepresentation

        var freePointer = 0
        var endPointer = disk.lastIndex

        while (freePointer < endPointer) {
            while (freePointer in disk.indices && disk[freePointer] != null) {
                freePointer++
            }
            while (endPointer in disk.indices && disk[endPointer] == null) {
                endPointer--
            }
            if (freePointer < endPointer) {
                disk[freePointer] = disk[endPointer]
                disk[endPointer] = null
            }
        }

        var checksum = 0L
        disk.forEachIndexed { index, id -> checksum += (id ?: 0) * index.toLong() }

        return checksum
    }

    fun part2(input: List<String>): Long {
        val disk = parseDisk(input)

        var toReassignPointerIdx = disk.files.lastKey()
        var fileWithFreeBlocks = disk.files.filter { it.value.freeBlocks != 0 }.firstNotNullOf { it.value }

        while (fileWithFreeBlocks.startIdx < toReassignPointerIdx) {
            val toReassign = disk.files[toReassignPointerIdx]!!

            println("Reassigning $toReassign")
            while (fileWithFreeBlocks.startIdx < toReassign.startIdx) {
                if (toReassign.numBlocks <= fileWithFreeBlocks.freeBlocks) {
                    disk.files.remove(toReassign.startIdx)
                    toReassign.startIdx = fileWithFreeBlocks.startIdx + fileWithFreeBlocks.numBlocks
                    toReassign.freeBlocks = fileWithFreeBlocks.freeBlocks - toReassign.numBlocks
                    fileWithFreeBlocks.freeBlocks = 0
                    disk.files[toReassign.startIdx] = toReassign
                    break
                }

                fileWithFreeBlocks =
                    disk.files.tailMap(fileWithFreeBlocks.startIdx + 1).filter { it.value.freeBlocks != 0 }
                        .firstNotNullOfOrNull { it.value } ?: break
            }
            toReassignPointerIdx = disk.files.headMap(toReassignPointerIdx).lastKey()
            fileWithFreeBlocks = disk.files.filter { it.value.freeBlocks != 0 }.firstNotNullOf { it.value }
        }

        var checksum = 0L
        disk.files.forEach { (_, file) ->
            var curIdx = file.startIdx
            repeat(file.numBlocks) { checksum += file.id * curIdx++ }
        }

        return checksum
    }

    // test if implementation meets criteria from the description, like:
    // Check test inputs
    val testInput = """
        2333133121414131402
    """.trimIndent().split("\n")
    utils.check(1928L, part1(testInput), "Part 1")
    utils.check(2858, part2(testInput), "Part 2")

    val input = aoc.input()
    measureTime {
        val result = part1(input).also { println("Part 1: $it") }
        //aoc.submitPartOne(result)
    }.also { it.println() }
    measureTime {
        val result = part2(input).also { println("Part 2: $it") }
        //aoc.submitPartTwo(result)
    }.also { it.println() }
}