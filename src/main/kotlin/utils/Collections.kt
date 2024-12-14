package utils

fun <T> Collection<T>.getSubsets(): List<Set<T>> {
    val subsets = mutableListOf<Set<T>>()
    subsets.add(emptySet()) // Start with the empty set

    for (element in this) {
        // For each element in the set, add it to existing subsets to create new subsets
        val newSubsets = subsets.map { subset -> subset + element }
        subsets.addAll(newSubsets)
    }

    return subsets
}

fun <T> Collection<T>.generatePermutations(): Set<List<T>> {
    if (this.isEmpty()) return setOf(emptyList())

    val result = mutableSetOf<List<T>>()
    for (item in this) {
        val remainingSet = this - item
        val permutationsOfRemaining = remainingSet.generatePermutations()
        for (permutation in permutationsOfRemaining) {
            result.add(listOf(item) + permutation)
        }
    }
    return result
}

fun main() {
    println(setOf(1, 2, 3, 4).getSubsets())
}