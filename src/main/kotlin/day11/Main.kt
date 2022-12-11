package day11

import InputLoader
import io.kotest.matchers.shouldBe

private const val ROUNDS = 20

private data class Monkey(
    val items: MutableList<Int>,
    val operation: (old: Int) -> Int,
    val prime: Int,
    val trueTarget: Int,
    val falseTarget: Int,
)

private fun String.parseOperation(): (old: Int) -> Int =
    split(" ").let { (operator, operand) ->
        { old: Int ->
            val op = when (operand) {
                "old" -> old
                else -> operand.toInt()
            }
            when (operator) {
                "+" -> old + op
                "*" -> old * op
                "-" -> old - op
                "/" -> old / op
                else -> error("Unknown operator '$operator'")
            }
        }
    }

private fun List<String>.parseMonkey() = Monkey(
    get(1).removePrefix("  Starting items: ").split(", ").map { it.toInt() }.toMutableList(),
    get(2).removePrefix("  Operation: new = old ").parseOperation(),
    get(3).removePrefix("  Test: divisible by ").toInt(),
    get(4).removePrefix("    If true: throw to monkey ").toInt(),
    get(5).removePrefix("    If false: throw to monkey ").toInt(),
)

private fun String.parseMonkeys(): List<Monkey> =
    lines().chunked(7).map { chunk -> chunk.parseMonkey() }

private infix fun Int.isModableBy(mod: Int): Boolean = mod(mod) == 0
private infix fun Int.mods(mod: Int): Boolean = mod.mod(this) == 0

private fun String.part01(): Int =
    parseMonkeys().let { monkeys ->
        MutableList(monkeys.size) { 0 }.also { counter ->
            repeat(ROUNDS) {
                monkeys.forEachIndexed { index, monkey ->
                    counter[index] += monkey.items.size

                    monkey.items.forEach { item ->
                        val newWorriedness = monkey.operation(item) / 3

                        if (newWorriedness isModableBy monkey.prime) {
                            monkeys[monkey.trueTarget].items
                        } else {
                            monkeys[monkey.falseTarget].items
                        } += newWorriedness
                    }
                    monkey.items.clear()
                }
            }
        }.let { counter ->
            counter.max().let { max ->
                max * counter.filter { it != max }.max()
            }
        }
    }

private fun String.part02(): Int = 0

fun main() {
    testInput.part01() shouldBe PART_01_RES

    val input = InputLoader.loadInput("day11")
    println(input.part01())
    println(input.part02())
}

private val testInput = """
Monkey 0:
  Starting items: 79, 98
  Operation: new = old * 19
  Test: divisible by 23
    If true: throw to monkey 2
    If false: throw to monkey 3

Monkey 1:
  Starting items: 54, 65, 75, 74
  Operation: new = old + 6
  Test: divisible by 19
    If true: throw to monkey 2
    If false: throw to monkey 0

Monkey 2:
  Starting items: 79, 60, 97
  Operation: new = old * old
  Test: divisible by 13
    If true: throw to monkey 1
    If false: throw to monkey 3

Monkey 3:
  Starting items: 74
  Operation: new = old + 3
  Test: divisible by 17
    If true: throw to monkey 0
    If false: throw to monkey 1
""".trimIndent()

private const val PART_01_RES = 10605
