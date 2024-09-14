package com.paparazziapps.pretamistapp.domain.utils

import kotlin.math.ceil
import kotlin.math.round

//Create a function to receive string in 1, 2 , 3, 4, 5, 6, 7, 8 ... 100 and return the percentage in double
fun String.toPercentage(): Double {
    return this.toDoubleOrNull()?.div(100) ?: 0.0
}

fun roundToOneDecimal(num: Double): Double {
    return ceil(num * 10) / 10
}
