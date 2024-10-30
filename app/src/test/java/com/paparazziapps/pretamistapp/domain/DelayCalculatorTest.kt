package com.paparazziapps.pretamistapp.domain

import org.junit.Assert.*
import org.junit.Test

class DelayCalculatorTest {

    @Test
    fun `calculateWeeksDelayed should return 0 when daysDelayed is less than 7`() {
        val delayCalculator = DelayCalculator()
        val daysDelayed = 6
        val expected = 0
        val actual = delayCalculator.calculateWeeksDelayed(daysDelayed)
        assertEquals(expected, actual)
    }

    @Test
    fun `calculateWeeksDelayed should return 1 when daysDelayed is 7`() {
        val delayCalculator = DelayCalculator()
        val daysDelayed = 7
        val expected = 1
        val actual = delayCalculator.calculateWeeksDelayed(daysDelayed)
        assertEquals(expected, actual)
    }

    @Test
    fun `calculateWeeksDelayed should return 2 when daysDelayed is 14`() {
        val delayCalculator = DelayCalculator()
        val daysDelayed = 14
        val expected = 2
        val actual = delayCalculator.calculateWeeksDelayed(daysDelayed)
        assertEquals(expected, actual)
    }

    @Test
    fun `calculateWeeksDelayed should return 3 when daysDelayed is between 21 and 27`() {
        val delayCalculator = DelayCalculator()
        val daysDelayed = 24
        val expected = 3
        val actual = delayCalculator.calculateWeeksDelayed(daysDelayed)
        assertEquals(expected, actual)
    }
}