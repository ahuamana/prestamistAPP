package com.paparazziapps.pretamistapp.domain

import org.junit.Assert.*
import org.junit.Test

class DelayCalculatorTest {

    @Test
    fun `calculate delay for daily payment`() {
        val delayCalculator = DelayCalculator()
        val daysDelayed = 5
        val expected = 5
        val result = delayCalculator.calculateDelay(PaymentScheduledEnum.DAILY, daysDelayed)
        assertEquals(expected, result)
    }

    @Test
    fun `calculate delay for weekly payment`() {
        val delayCalculator = DelayCalculator()
        val daysDelayed = 10
        val expected = 3
        val result = delayCalculator.calculateDelay(PaymentScheduledEnum.WEEKLY, daysDelayed)
        assertEquals(expected, result)
    }

    @Test
    fun `calculate delay for fortnightly payment`() {
        val delayCalculator = DelayCalculator()
        val daysDelayed = 20
        val expected = 5
        val result = delayCalculator.calculateDelay(PaymentScheduledEnum.FORTNIGHTLY, daysDelayed)
        assertEquals(expected, result)
    }
}