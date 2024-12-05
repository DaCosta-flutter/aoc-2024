package com.toldoven.aoc.notebook


sealed interface Rule {
    fun matches(s: String): Set<Int>

}

class StringRule(private val rule: String) : Rule {
    override fun matches(s: String): Set<Int> {
        return if (s.startsWith(rule)) setOf(rule.length) else emptySet()
    }
}

class CompositeRule(private val rules: List<Rule>) : Rule {
    override fun matches(s: String): Set<Int> {
        val curIdx = mutableSetOf<Int>().apply { add(0) }
        rules
            .forEach { rule ->
                val idxes = curIdx.toList()
                idxes.forEach { idx ->
                    val result = rule.matches(s.substring(idx))
                        .map { it + idx }
                    curIdx.remove(idx)
                    curIdx.addAll(result)
                }
            }
        return curIdx
    }
}

class RuleReference(private val rulesMap: Map<Int, Rule>, private val ruleNum: Int) : Rule {

    override fun matches(s: String): Set<Int> {
        return rulesMap[ruleNum]!!.matches(s)
    }
}

class OrRule(private val rules: List<Rule>) : Rule {

    override fun matches(s: String): Set<Int> {
        return rules.map { rule -> rule.matches(s) }.flatten().toSet()
    }
}

fun parseCompositeRule(rulesStr: String, map: MutableMap<Int, Rule>): CompositeRule {
    val rules = rulesStr.trim().split(" ")
    val toUse = rules.map { ruleStr ->
        RuleReference(map, ruleStr.toInt())
    }
    return CompositeRule(toUse)
}

fun parse(lines: List<String>): Pair<MutableMap<Int, Rule>, List<String>> {
    var curLine = 0
    val rules = mutableMapOf<Int, Rule>()
    while (lines[curLine].isNotBlank()) {
        val line = lines[curLine]
        val num = line.split(":")[0].toInt()
        val rulesStr = line.split(":")[1].trim()
        if (rulesStr.contains("|")) {
            val rulesInOr = rulesStr.split("|")
                .map { it.trim() }
                .map { parseCompositeRule(it, rules) }
                .toList()
            rules[num] = OrRule(rulesInOr)
        } else if ("\"" in rulesStr) {

            rules[num] = StringRule(rulesStr.trim('"'))
        } else {
            rules[num] = parseCompositeRule(rulesStr, rules)
        }
        curLine++
    }

    val messages = lines.subList(curLine + 1, lines.size)
    return Pair(rules, messages)
}

fun main() {

    val aoc =
        AocClient.fromFile("/Users/costaj/Documents/aoc-kotlin-notebook/.aocCache/session.txt").interactiveDay(2020, 19)

    val example = """42: 9 14 | 10 1
9: 14 27 | 1 26
10: 23 14 | 28 1
1: "a"
11: 42 31
5: 1 14 | 15 1
19: 14 1 | 14 14
12: 24 14 | 19 1
16: 15 1 | 14 14
31: 14 17 | 1 13
6: 14 14 | 1 14
2: 1 24 | 14 4
0: 8 11
13: 14 3 | 1 12
15: 1 | 14
17: 14 2 | 1 7
23: 25 1 | 22 14
28: 16 1
4: 1 1
20: 14 14 | 1 15
3: 5 14 | 16 1
27: 1 6 | 14 18
14: "b"
21: 14 1 | 1 14
25: 1 1 | 1 14
22: 14 14
8: 42
26: 14 22 | 1 20
18: 15 15
7: 14 5 | 1 21
24: 14 1

abbbbbabbbaaaababbaabbbbabababbbabbbbbbabaaaa
bbabbbbaabaabba
babbbbaabbbbbabbbbbbaabaaabaaa
aaabbbbbbaaaabaababaabababbabaaabbababababaaa
bbbbbbbaaaabbbbaaabbabaaa
bbbababbbbaaaaaaaabbababaaababaabab
ababaaaaaabaaab
ababaaaaabbbaba
baabbaaaabbaaaababbaababb
abbbbabbbbaaaababbbbbbaaaababb
aaaaabbaabaaaaababaa
aaaabbaaaabbaaa
aaaabbaabbaaaaaaabbbabbbaaabbaabaaa
babaaabbbaaabaababbaabababaaab
aabbbbbaabbbaaaaaabbbbbababaaaaabbaaabba
""".split("\n")


    val (rules, messages) = parse(aoc.input())

    rules[8] = OrRule(
        listOf(
            RuleReference(rules, 42),
            CompositeRule(listOf(RuleReference(rules, 42), RuleReference(rules, 8)))
        )
    )
    rules[11] = OrRule(
        listOf(
            CompositeRule(listOf(RuleReference(rules, 42), RuleReference(rules, 31))),
            CompositeRule(listOf(RuleReference(rules, 42), RuleReference(rules, 11), RuleReference(rules, 31))),
        )
    )

    rules[0]!!.matches(messages[0])


    val valid = messages
        .filter { msg -> rules[0]!!.matches(msg).any { idx -> idx == msg.length } }

    println(valid)
    println(valid.size)
}
