package com.paparazziapps.pretamistapp.domain

import androidx.annotation.Keep
import java.time.LocalDate

@Keep
data class PaymentSchedule(
    val date: LocalDate,
    val payment: Double,
    val principalPaid: Double,
    val interestPaid: Double,
    val remainingBalance: Double
)
