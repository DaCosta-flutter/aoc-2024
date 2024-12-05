package utils

fun LongRange.intersectRange(with: LongRange): LongRange {
    val startRange = if (with.first > this.first) with.first else this.first
    val endRange = if (with.last < this.last) with.last else this.last
    return startRange..endRange
}

fun LongRange.notIntersected(withRanges: Collection<LongRange>): Set<LongRange> {
    var lastNotIntersectedValue = this.first

    return withRanges.sortedBy { it.first }
        .map { otherRange ->
            val intersection = this.intersectRange(otherRange)
            val range = lastNotIntersectedValue until intersection.first
            lastNotIntersectedValue = otherRange.last + 1
            range
        }
        .filterNot { it.isEmpty() }
        .toMutableSet().apply {
            val finalRange = lastNotIntersectedValue..this@notIntersected.last
            if (!finalRange.isEmpty()) {
                add(finalRange)
            }
        }
}