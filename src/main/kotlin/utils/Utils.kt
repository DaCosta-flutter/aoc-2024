package utils

import java.math.BigInteger
import java.security.MessageDigest
import kotlin.io.path.Path
import kotlin.io.path.readLines
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.math.roundToLong

val sessionTokenPath = "/Users/costaj/Documents/aoc-kotlin-notebook/.aocCache/session.txt"

/**
 * Reads lines from the given input txt file.
 */
fun readInput2023(name: String) = Path("src/y2023/$name.txt").readLines()
fun readInput(name: String) = Path("src/$name.txt").readLines()

fun Double.isInt() = abs(this.roundToInt().toDouble() - this) < 0.00001
fun Double.isLong() = abs(this.roundToLong().toDouble() - this) < 0.001

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')

/**
 * The cleaner shorthand for printing output.
 */
fun Any?.println() = println(this)

fun <V> check(expected: V, actual: V, checkName: String? = null) {
    if (expected != actual) {
        throw Exception("Check ${checkName ?: ""} failed. Expected '$expected', but found '$actual'")
    }
    println("Check $checkName passed")
}