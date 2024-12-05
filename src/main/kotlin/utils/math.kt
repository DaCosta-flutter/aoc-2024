package utils

fun Collection<Long>.product() = this.fold(1L) { acc, cur -> acc * cur }
fun Collection<Int>.product() = this.fold(1) { acc, cur -> acc * cur }

fun findLCM(numbers: List<Long>): Long {
    return if (numbers.size == 1) {
        numbers[0]
    } else {
        numbers.fold(1) { acc, num -> findLCMTwoNums(acc, num) }
    }
}

private fun findLCMTwoNums(a: Long, b: Long) = a / findGCD(a, b) * b

fun findGCD(a: Long, b: Long): Long {
    return if (b == 0L) a else findGCD(b, a % b)
}

fun chineseRemainderTheorem(
    remainders: List<Long>,
    moduli: List<Long>
): Long {
    val product = moduli.reduce { acc, modulus -> acc * modulus }

    var result = 0L
    for (i in remainders.indices) {
        val ai = remainders[i]
        val ni = moduli[i]
        val bi = product / ni

        result += ai * modInverse(bi, ni) * bi
    }

    return result % product
}

private fun modInverse(aOrig: Long, m: Long): Long {
    var a = aOrig
    var m0 = m
    var t: Long
    var q: Long
    var x0 = 0L
    var x1 = 1L

    if (m == 1L) return 0

    while (a > 1) {
        q = a / m0
        t = m0
        m0 = a % m0
        a = t
        t = x0
        x0 = x1 - q * x0
        x1 = t
    }

    return if (x1 < 0) x1 + m else x1
}

fun main() {
    val remainders = listOf(2, 3, 2)
    val moduli = listOf(3, 5, 7)

    val result = chineseRemainderTheorem(remainders.map { it.toLong() }, moduli.map { it.toLong() })
    println("Result: $result")
}
