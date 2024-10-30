package com.paparazziapps.pretamistapp.domain

import com.paparazziapps.pretamistapp.domain.utils.roundToOneDecimal
import java.time.LocalDate
import kotlin.math.ceil
import kotlin.math.pow
import kotlin.math.roundToInt

/**
 * BaseLoan is an abstract class that provides a base implementation for different types of loans.
 *
 * @property periodsPerYear The number of periods per year for the loan. This is abstract and must be implemented by subclasses.
 */
abstract class BaseLoan : Loan {
    protected abstract val periodsPerYear: Int

    /**
     * Calculates the payment for a loan.
     *
     * @param principal The principal amount of the loan. e.g. 1000.0 for a loan of $1000.
     * @param annualRate The annual interest rate of the loan. e.g. 0.05 for a 5% annual interest rate.
     * @param termInMonths The term of the loan in months. e.g. 12 for a 1-year loan.
     * @return A LoanPayment object containing the total interest, periodic payment, and total payment for the loan.
     */
    override fun calculatePayment(
        principal: Double,
        annualRate: Double,
        numberOfPayments: Int,
        startDate: LocalDate,
        isAmortized: Boolean,
        roundedTo1Decimal: Boolean
    ): LoanPayment {
        val periodicRate = annualRate / periodsPerYear
        val exactRegularPayment = calculateExactRegularPayment(principal, periodicRate, numberOfPayments)
        val roundedRegularPayment = if (roundedTo1Decimal) (exactRegularPayment * 10).roundToInt() / 10.0 else exactRegularPayment
        val schedule = generatePaymentSchedule(principal, periodicRate, numberOfPayments, roundedRegularPayment, startDate)

        val totalInterest = schedule.sumOf { it.interestPaid }
        val totalPayment = schedule.sumOf { it.payment }

        return LoanPayment.createToOneDecimal(totalInterest, roundedRegularPayment, totalPayment, schedule)
    }

    private fun calculateExactRegularPayment(principal: Double, periodicRate: Double, numberOfPayments: Int): Double {
        return (principal * periodicRate) / (1 - (1 + periodicRate).pow(-numberOfPayments))
    }

    private fun generatePaymentSchedule(
        principal: Double,
        periodicRate: Double,
        numberOfPayments: Int,
        roundedRegularPayment: Double,
        startDate: LocalDate
    ): List<PaymentSchedule> {
        var remainingBalance = principal
        var currentDate = startDate
        val schedule = mutableListOf<PaymentSchedule>()

        for (paymentNumber in 1..numberOfPayments) {
            currentDate = getNextPaymentDate(currentDate)
            val interestPaid = remainingBalance * periodicRate
            var principalPaid = roundedRegularPayment - interestPaid
            var payment = roundedRegularPayment

            if (paymentNumber == numberOfPayments) {
                // Adjust the final payment to cover any remaining balance
                principalPaid = remainingBalance
                payment = (principalPaid + interestPaid * 10).roundToInt() / 10.0
            }

            remainingBalance -= principalPaid

            schedule.add(PaymentSchedule(
                date = currentDate,
                payment = payment,
                principalPaid = principalPaid,
                interestPaid = interestPaid,
                remainingBalance = remainingBalance.coerceAtLeast(0.0) // Ensure balance doesn't go below 0
            ))

            if (remainingBalance <= 0) break
        }

        return schedule
    }

    private fun getNextPaymentDate(currentDate: LocalDate): LocalDate {
        return when (periodsPerYear) {
            365 -> currentDate.plusDays(1)
            52 -> currentDate.plusWeeks(1)
            26 -> currentDate.plusWeeks(2)
            12 -> currentDate.plusMonths(1)
            6 -> currentDate.plusMonths(2)
            4 -> currentDate.plusMonths(3)
            2 -> currentDate.plusMonths(6)
            1 -> currentDate.plusYears(1)
            else -> currentDate.plusMonths(1)
        }
    }

    /**
     * Calculates the periodic payment for a loan.
     *
     * @param principal The principal amount of the loan.
     * @param periodicRate The periodic interest rate of the loan. e.g. 0.05 for a 5% annual interest rate.
     * @param numberOfPayments The total number of payments for the loan. e.g. 12 for a 1-year loan.
     * @return The periodic payment amount.
     */
    private fun calculatePeriodicPayment(principal: Double, periodicRate: Double, numberOfPayments: Double): Double {
        return principal * (periodicRate * (1 + periodicRate).pow(numberOfPayments)) /
                ((1 + periodicRate).pow(numberOfPayments) - 1)
    }
}