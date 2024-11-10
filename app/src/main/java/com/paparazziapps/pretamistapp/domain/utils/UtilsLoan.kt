package com.paparazziapps.pretamistapp.domain.utils

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.ceil

//Create a function to receive string in 1, 2 , 3, 4, 5, 6, 7, 8 ... 100 and return the percentage in double
fun String.toPercentage(): Double {
    return this.toDoubleOrNull()?.div(100) ?: 0.0
}

fun roundToOneDecimal(num: Double): Double {
    return ceil(num * 10) / 10
}


fun convertUnixTimeToFormattedDate(unixTime: Long): String {
    return try {
        // If timestamp is in seconds, convert to milliseconds
        val timeInMillis = if (unixTime < 1000000000000L) {
            unixTime * 1000
        } else {
            unixTime
        }

        val instant = Instant.ofEpochMilli(timeInMillis)
        val localDate = instant.atZone(ZoneId.systemDefault()).toLocalDateTime()
        val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
        localDate.format(formatter)
    } catch (e: Exception) {
        "Invalid date"
    }
}