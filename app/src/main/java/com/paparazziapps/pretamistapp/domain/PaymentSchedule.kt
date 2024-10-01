package com.paparazziapps.pretamistapp.domain

import java.time.LocalDate

data class PaymentSchedule(
    val date: LocalDate,
    val payment: Double,
    val principalPaid: Double,
    val interestPaid: Double,
    val remainingBalance: Double
)
