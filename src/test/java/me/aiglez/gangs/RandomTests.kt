package me.aiglez.gangs

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class RandomTests {

    @Test
    fun `test core upgrade`() {
        val maxLevel = 18
        val currentLevel = 18

        val expected = false
        val actual = maxLevel != currentLevel

        Assertions.assertEquals(expected, actual)
    }

}