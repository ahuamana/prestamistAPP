package com.paparazziapps.pretamistapp.domain.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

class DateConverter() {
    private val timeZone = TimeZone.getTimeZone("GMT-5")
    private val defaultDateFormat = SimpleDateFormat("MMM dd - yyyy").apply {
        timeZone = this@DateConverter.timeZone
    }


    /**
     * Converts Unix timestamp to formatted date string using GMT-5
     * @param timestamp Unix timestamp in seconds
     * @return Formatted date string (e.g., "Jun 12 - 2023")
     */
    fun unixToFormattedDate(timestamp: Long): String {
        // Check if timestamp is in seconds or milliseconds
        val milliseconds = if (timestamp > 999999999999L) { // If timestamp is in milliseconds
            timestamp
        } else { // If timestamp is in seconds
            timestamp * 1000L
        }

        val date = Date(milliseconds)
        return defaultDateFormat.format(date)
    }

    /**
     * Converts Unix timestamp to formatted date string using custom format
     * @param unixTimestamp Unix timestamp in seconds
     * @param pattern Custom date pattern (e.g., "yyyy-MM-dd")
     * @return Formatted date string according to pattern
     */
    fun unixToCustomFormat(unixTimestamp: Long, pattern: String): String {
        val date = Date(unixTimestamp * 1000)
        val customFormat = SimpleDateFormat(pattern).apply {
            timeZone = this@DateConverter.timeZone
        }
        return customFormat.format(date)
    }

    /**
     * Gets current date in GMT-5 with default format
     * @return Current date formatted string
     */
    fun getCurrentDate(): String {
        return unixToFormattedDate(System.currentTimeMillis() / 1000)
    }

    /**
     * Converts date string back to Unix timestamp
     * @param dateStr Date string in format "MMM dd - yyyy"
     * @return Unix timestamp in seconds, or null if parsing fails
     */
    fun dateToUnix(dateStr: String): Long? {
        return try {
            val date = defaultDateFormat.parse(dateStr)?: Date()
            date.time / 1000
        } catch (e: Exception) {
            null
        }
    }

    companion object {
        // Singleton instance if needed
        val instance = DateConverter()
    }

}