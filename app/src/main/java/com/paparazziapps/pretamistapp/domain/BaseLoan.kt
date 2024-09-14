package com.paparazziapps.pretamistapp.domain

import com.paparazziapps.pretamistapp.domain.utils.PaymentSchedule
import com.paparazziapps.pretamistapp.domain.utils.roundToOneDecimal
import java.time.LocalDate
import kotlin.math.ceil
import kotlin.math.pow
import kotlin.math.round

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
    override fun calculatePayment(principal: Double, annualRate: Double, numberOfPayments: Int, startDate: LocalDate, roundedTo1Decimal:Boolean): LoanPayment {
        val periodicRate = annualRate / periodsPerYear

        // Calculate the periodic payment, total payment, and total interest
        val periodicPayment = calculatePeriodicPayment(principal, periodicRate, numberOfPayments.toDouble())

        // Generate the payment schedule
        val schedule = generatePaymentSchedule(principal, periodicRate, numberOfPayments, periodicPayment, startDate)


        val totalPayment = periodicPayment * numberOfPayments
        val totalInterest = totalPayment - principal

        return if (!roundedTo1Decimal) {
            LoanPayment.create(totalInterest, periodicPayment, totalPayment, schedule)
        } else LoanPayment.createToOneDecimal(totalInterest, periodicPayment, totalPayment, schedule)
    }



    private fun generatePaymentSchedule(principal: Double, periodicRate: Double, numberOfPayments: Int, periodicPayment: Double, startDate: LocalDate): List<PaymentSchedule> {
        var remainingBalance = principal
        var currentDate = startDate // Start date for the first payment

        //Adjust the start date based on the periods per year
        currentDate = when (periodsPerYear) {
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

        // Create a list of payments with only the integer part
        val payments = MutableList(numberOfPayments) { periodicPayment.toString().split(".")[0].toDoubleOrNull()?:0.0 }

        // Create a list of the decimal parts of each payment
        val paymentDecimals = MutableList(numberOfPayments) { periodicPayment - payments[it] }

        // Calculate the sum of the decimal parts
        val totalDecimals = paymentDecimals.sum()

        // Add the sum of the decimal parts to the final payment include the decimals
        payments[payments.size - 1]  = roundToOneDecimal(payments[payments.size - 1] + totalDecimals)

        // Create the payment schedule
        return (1..numberOfPayments).map {
            val interestPaid = remainingBalance * periodicRate
            val principalPaid = payments[it - 1] - interestPaid
            remainingBalance -= principalPaid

            PaymentSchedule(
                date = currentDate,
                payment = payments[it - 1].toDouble(),
                principalPaid = principalPaid,
                interestPaid = interestPaid,
                remainingBalance = remainingBalance
            ).also {
                currentDate = when (periodsPerYear) {
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