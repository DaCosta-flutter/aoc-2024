package com.toldoven.aoc.notebook.y2024

import com.toldoven.aoc.notebook.AocClient
import utils.println
import utils.sessionTokenPath
import java.util.*
import kotlin.time.measureTime

fun main() {

    val aoc = AocClient.fromFile(sessionTokenPath).interactiveDay(2024, 9)

    data class File(
        val id: Int,
        val numBlocks: Int,
        val startIdx: Int,
        val freeBlocks: Int
    )

    data class Disk(
        val files: TreeSet<File>, // Sort files by startIdx
        val diskRepresentation: MutableList<Int?>
    )

    fun parseDisk(input: List<String>): Disk {
        var currentId = 0;
        val line = input[0]
        val disk = mutableListOf<Int?>()
        val comparatorIds: (File, File) -> Int = { o1: File, o2: File -> o1.id - o2.id }
        val files = TreeSet(comparatorIds)

        while ((currentId * 2) in line.indices) {
            val fileBlocks = line[2 * currentId].toString().toInt()
            val startIdx = disk.size
            repeat(fileBlocks) { disk.add(currentId) }
            var freeBlocks = 0
            if ((2 * currentId + 1) in line.indices) {
                freeBlocks = line[2 * currentId + 1].toString().toInt()
                repeat(freeBlocks) { disk.add(null) }
            }
            files.add(File(currentId, fileBlocks, startIdx, freeBlocks))
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

        val filesByNumberOfFreeBlocks = TreeMap<Int, TreeSet<File>>().apply {
            disk.files
                .filterNot { it.freeBlocks == 0 }
                .forEach { file ->
                    this.computeIfAbsent(file.freeBlocks) { TreeSet({ o1: File, o2: File -> o1.startIdx - o2.startIdx }) }
                        .add(file)
                }
        }

        var fileToCheck = disk.files.lastOrNull()

        while (fileToCheck != null) {
            val fileWithEnoughFreeBlocks = filesByNumberOfFreeBlocks
                .mapNotNull { it.value.firstOrNull() }
                .filter { it.startIdx < fileToCheck!!.startIdx && it.freeBlocks >= fileToCheck!!.numBlocks }
                .minByOrNull { it.startIdx }
            if (fileWithEnoughFreeBlocks != null) {
                filesByNumberOfFreeBlocks[fileWithEnoughFreeBlocks.freeBlocks]!!.remove(fileWithEnoughFreeBlocks)

                fileToCheck.run {
                    disk.files.remove(this)
                    filesByNumberOfFreeBlocks[this.freeBlocks]?.remove(this)
                }

                val fileToReassign = fileToCheck.copy(
                    startIdx = fileWithEnoughFreeBlocks.startIdx + fileWithEnoughFreeBlocks.numBlocks,
                    freeBlocks = fileWithEnoughFreeBlocks.freeBlocks - fileToCheck.numBlocks
                )

                fileToReassign.run {
                    disk.files.add(this)
                    if (this.freeBlocks > 0) {
                        filesByNumberOfFreeBlocks[this.freeBlocks]!!.add(this)
                    }
                }

            }

            fileToCheck = disk.files.lower(fileToCheck)
        }

        var checksum = 0L
        disk.files.forEach { file ->
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