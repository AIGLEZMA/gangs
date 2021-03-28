package me.aiglez.gangs

import me.aiglez.gangs.utils.Placeholders
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class LoreTests {

    private val INPUT = listOf(
        "Here is the first line of input",
        "Second line of input",
        "A third line of input",
        "{lore}",
        "The last line of input"
    )

    private val INSERT = listOf(
        "Inserted this",
        "And this"
    )

    private val EXPECTED = listOf(
        "Here is the first line of input",
        "Second line of input",
        "A third line of input",
        "Inserted this",
        "And this",
        "The last line of input"
    )

    private val PLACEHOLDER = "{lore}"

    @Test
    fun `ensure has index`() {
        val index = INPUT.indexOf(PLACEHOLDER)
        Assertions.assertNotSame(-1, index, "placeholder not found")
    }


    @Test
    fun `insert lore at index`() {
        val actual = mutableListOf<String>()

        //actual.addAll(INPUT.flatMap { elem -> if (elem == PLACEHOLDER) INSERT else listOf(elem) })
        for ((index, line) in INPUT.withIndex()) {
            if (line == PLACEHOLDER) {
                actual.addAll(index, INSERT)
            } else {
                actual.add(line)
            }
        }

        Assertions.assertEquals(EXPECTED.count(), actual.count())
        Assertions.assertEquals(EXPECTED, actual)
    }

    @Test
    fun `handle placeholders`() {
        val modify = mutableListOf(
            "First line here",
            "Then a second one",
            "Change {0}",
            "And change this too: {1}",
            "That's all"
        )

        val expected = mutableListOf(
            "First line here",
            "Then a second one",
            "Change 1",
            "And change this too: ONE",
            "That's all"
        )

        val actual = Placeholders.replaceIn(
            modify, 1, "ONE"
        )

        Assertions.assertEquals(expected, actual)
    }

    @Test
    fun `addAll test`() {
        val insertIn = mutableListOf(
            "ZA", "B", "C", "WD", "G", "H"
        )
        val insert = mutableListOf(
            "E", "F"
        )
        val expected = mutableListOf(
            "ZA", "B", "C", "WD", "E", "F", "G", "H"
        )

        val actual = mutableListOf<String>()
        actual.addAll(insertIn)
        actual.addAll(4, insert)

        Assertions.assertEquals(expected, actual)
    }

    @Test
    fun `insert test`() {
        val actual = Placeholders.insertCollection(INPUT, PLACEHOLDER, INSERT)

        Assertions.assertEquals(EXPECTED.count(), actual.count())
        Assertions.assertEquals(EXPECTED, actual)
    }

    @Test
    fun `insert then replace`() {
        val upgradeCost = 1000
        val levelLore = mutableListOf(
            "STONE: $10",
            "DIRT: $100"
        )
        val lore = mutableListOf(
            "This level is locked",
            "[blocks]",
            "Upgrade Cost: {0}"
        )
        val expected = mutableListOf(
            "This level is locked",
            "STONE: $10",
            "DIRT: $100",
            "Upgrade Cost: 1000"
        )

        Assertions.assertEquals(expected,
            Placeholders.insertCollection(
                Placeholders.replaceIn(lore, upgradeCost),
            "[blocks]", levelLore
        ))
    }
}