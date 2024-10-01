package com.paparazziapps.pretamistapp.domain

import java.time.LocalDate

/**
 * LoanManager is responsible for creating different types of loans and calculating payments.
 *
 * @property createLoan creates a loan of a specific type.
 * @property calculatePayment calculates the payment for a specific loan.
 */
class LoanManager {

    /**
     * Creates a loan of a specific type.
     *
     * @param type The type of the loan to be created.
     * @return The created loan.
     */
    fun createLoan(type: LoanType): Loan {
        return when (type) {
            LoanType.DAILY -> DailyLoan()
            LoanType.WEEKLY -> WeeklyLoan()
            LoanType.FORTNIGHTLY -> FortnightlyLoan()
            LoanType.MONTHLY -> MonthlyLoan()
            LoanType.BIMONTHLY -> BimonthlyLoan()
            LoanType.QUARTERLY -> QuarterlyLoan()
            LoanType.SEMIANNUAL -> SemiannualLoan()
            LoanType.ANNUAL -> AnnualLoan()
        }
    }

    /**
     * Calculates the payment for a specific loan.
     *
     * @param loan The loan for which the payment is to be calculated. e.g. DailyLoan, WeeklyLoan, etc.
     * @param principal The principal amount of the loan. e.g. 1000.0 for a loan of $1000.
     * @param annualRate The annual interest rate of the loan. e.g. 0.05 for a 5% annual interest rate.
     * @param termInMonths The term of the loan in months. e.g. 12 for a 1-year loan.
     * @return The calculated loan payment.
     */
    fun calculatePayment(loan: Loan, principal: Double, annualRate: Double, termInMonths: Int, localDate: LocalDate, isAmortized:Boolean, roundedToOneDecimal:Boolean): LoanPayment {
        return loan.calculatePayment(principal, annualRate, termInMonths,localDate, roundedToOneDecimal, isAmortized = isAmortized)
    }
}