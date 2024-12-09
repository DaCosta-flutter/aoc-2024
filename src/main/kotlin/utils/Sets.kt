package utils

fun <T> Set<T>.getSubsets(): List<Set<T>> {
    val subsets = mutableListOf<Set<T>>()
    subsets.add(emptySet()) // Start with the empty set

    for (element in this) {
        // For each element in the set, add it to existing subsets to create new subsets
        val newSubsets = subsets.map { subset -> subset + element }
        subsets.addAll(newSubsets)
    }

    return subsets
}

fun main() {
    println(setOf(1,2,3,4).getSubsets())
}