package com.paparazziapps.pretamistapp.domain

import java.time.LocalDate

interface Loan {
    fun calculatePayment(
        principal: Double, annualRate: Double, numberOfPayments: Int, startDate: LocalDate, roundedTo1Decimal:Boolean, isAmortized: Boolean = true
    ): LoanPayment
}