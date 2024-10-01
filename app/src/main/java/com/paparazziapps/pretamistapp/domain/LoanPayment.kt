package com.paparazziapps.pretamistapp.domain

import kotlin.math.ceil

data class LoanPayment (
    val interest: Double,
    val periodicPayment: Double,
    val totalPayment: Double,
    val schedule: List<PaymentSchedule>
) {
    companion object {
        //round to 2 decimals
        fun create(interest: Double, periodicPayment: Double, totalPayment: Double, schedule: List<PaymentSchedule>): LoanPayment {
            return LoanPayment(
                ceil(interest * 100) / 100,
                ceil(periodicPayment * 100) / 100,
                ceil(totalPayment * 100) / 100,
                schedule
            )
        }

        //round to 1 decimal
        fun createToOneDecimal(interest: Double, periodicPayment: Double, totalPayment: Double, schedule: List<PaymentSchedule>): LoanPayment {
            return LoanPayment(
                ceil(interest * 10) / 10,
                ceil(periodicPayment * 10) / 10,
                ceil(totalPayment * 10) / 10,
                schedule
            )
        }
    }
}

