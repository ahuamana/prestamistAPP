package com.paparazziapps.pretamistapp.domain

import com.paparazziapps.pretamistapp.domain.utils.DateConverter
import org.junit.Assert.*
import org.junit.Test

class DateConverterTest {
    @Test
    fun testUnixToFormattedDate() {
        val dateConverter = DateConverter()
        val unixTimestamp = 1623456000L
        val expected = "Jun 11 - 2021"
        val actual = dateConverter.unixToFormattedDate(unixTimestamp)
        assertEquals(expected, actual)
    }

    @Test
    fun testUnixToFormattedDate16Nov2024() {
        val dateConverter = DateConverter()
        val unixTimestamp = 1731793553L
        val expected = "Nov 16 - 2024"
        val actual = dateConverter.unixToFormattedDate(unixTimestamp)
        assertEquals(expected, actual)
    }

    @Test
    fun testUnixToFormattedDate16Nov2024WithSystemCurrentMi() {
        val dateConverter = DateConverter()
        val unixTimestamp = 1731794000911L
        val expected = "Nov 16 - 2024"
        val actual = dateConverter.unixToFormattedDate(unixTimestamp)
        assertEquals(expected, actual)
    }
}