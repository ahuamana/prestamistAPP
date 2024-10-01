package com.paparazziapps.pretamistapp.domain

import android.util.Log
import com.paparazziapps.pretamistapp.domain.utils.toPercentage
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.time.LocalDate

@RunWith(JUnit4::class)
class LoanManagerTest {

    private val tag: String = LoanManagerTest::class.java.simpleName

    @Test
    fun `createLoan should return DailyLoan when LoanType is DAILY`() {
        val loanManager = LoanManager()
        val loan = loanManager.createLoan(LoanType.DAILY)
        assertTrue(loan is DailyLoan)
    }

    @Test
    fun `createLoan should return WeeklyLoan when LoanType is WEEKLY`() {
        val loanManager = LoanManager()
        val loan = loanManager.createLoan(LoanType.WEEKLY)
        assertTrue(loan is WeeklyLoan)
    }

    @Test
    fun `createLoan should return FortnightlyLoan when LoanType is FORTNIGHTLY`() {
        val loanManager = LoanManager()
        val loan = loanManager.createLoan(LoanType.FORTNIGHTLY)
        assertTrue(loan is FortnightlyLoan)
    }

    @Test
    fun `createLoan should return MonthlyLoan when LoanType is MONTHLY`() {
        val loanManager = LoanManager()
        val loan = loanManager.createLoan(LoanType.MONTHLY)
        assertTrue(loan is MonthlyLoan)
    }

    @Test
    fun `createLoan should return BimonthlyLoan when LoanType is BIMONTHLY`() {
        val loanManager = LoanManager()
        val loan = loanManager.createLoan(LoanType.BIMONTHLY)
        assertTrue(loan is BimonthlyLoan)
    }

    @Test
    fun `createLoan should return QuarterlyLoan when LoanType is QUARTERLY`() {
        val loanManager = LoanManager()
        val loan = loanManager.createLoan(LoanType.QUARTERLY)
        assertTrue(loan is QuarterlyLoan)
    }

    @Test
    fun `createLoan should return SemiannualLoan when LoanType is SEMIANNUAL`() {
        val loanManager = LoanManager()
        val loan = loanManager.createLoan(LoanType.SEMIANNUAL)
        assertTrue(loan is SemiannualLoan)
    }

    @Test
    fun `createLoan should return AnnualLoan when LoanType is ANNUAL`() {
        val loanManager = LoanManager()
        val loan = loanManager.createLoan(LoanType.ANNUAL)
        assertTrue(loan is AnnualLoan)
    }

    @Test
    fun `calculateInterest should return correct interest for DailyLoan`() {
        val loanManager = LoanManager()
        val monthlyLoan = loanManager.createLoan(LoanType.DAILY)
        val interest  = "5".toPercentage()
        val numberPayments = 2 // 1 year
        val localDate = LocalDate.now()
        val amountLoan = 1000.0
        val payment : LoanPayment = loanManager.calculatePayment(monthlyLoan, amountLoan, interest, numberPayments, localDate, isAmortized = true, roundedToOneDecimal = false)
        println("Interes aplicado: $interest")
        println("Numero de cuotas: $numberPayments")
        println("Pago mensual: ${payment.periodicPayment}")
        println("Interés total: ${payment.interest}")
        println("Pago total: ${payment.totalPayment}")
        println("Fecha de inicio: $localDate")
        println("Detalle de pagos:")
        payment.schedule.forEachIndexed { index, it ->
            println("$index Fecha: ${it.date} - Pago: ${it.payment} - Principal: ${it.principalPaid} - Interes: ${it.interestPaid} - Balance: ${it.remainingBalance}")
        }
        assertEquals(27.29, payment.interest, 0.0)
    }

    @Test
    fun `calculateInterest should return correct interest for WeeklyLoan`() {
        val loanManager = LoanManager()
        val monthlyLoan = loanManager.createLoan(LoanType.WEEKLY)
        val interest  = "5".toPercentage()
        val numberPayments = 52 // 1 year
        val localDate = LocalDate.now()
        val payment : LoanPayment = loanManager.calculatePayment(monthlyLoan, 1000.0, interest, numberPayments, localDate, isAmortized = true, roundedToOneDecimal = false)
        println("Interes aplicado: $interest")
        println("Numero de cuotas: $numberPayments")
        println("Pago mensual: ${payment.periodicPayment}")
        println("Interés total: ${payment.interest}")
        println("Pago total: ${payment.totalPayment}")
        println("Fecha de inicio: $localDate")
        println("Detalle de pagos:")
        payment.schedule.forEachIndexed { index, it ->
            println("$index Fecha: ${it.date} - Pago: ${it.payment} - Principal: ${it.principalPaid} - Interes: ${it.interestPaid} - Balance: ${it.remainingBalance}")
        }
        assertEquals(27.29, payment.interest, 0.0)
    }

    @Test
    fun `calculateInterest should return correct interest for MonthlyLoan`() {
        val loanManager = LoanManager()
        val monthlyLoan = loanManager.createLoan(LoanType.MONTHLY)
        val interest  = "100".toPercentage()
        val numberPayments = 12 // 1 year
        val localDate = LocalDate.now()
        val payment : LoanPayment = loanManager.calculatePayment(monthlyLoan, 1000.0, interest, numberPayments, localDate, isAmortized = false, roundedToOneDecimal = false)
        println("Interes aplicado: $interest")
        println("Numero de cuotas: $numberPayments")
        println("Pago mensual: ${payment.periodicPayment}")
        println("Interés total: ${payment.interest}")
        println("Pago total: ${payment.totalPayment}")
        println("Fecha de inicio: $localDate")
        println("Detalle de pagos:")
        payment.schedule.forEach {
            println("Fecha: ${it.date} - Pago: ${it.payment} - Principal: ${it.principalPaid} - Interes: ${it.interestPaid} - Balance: ${it.remainingBalance}")
        }
        assertEquals(27.29, payment.interest, 0.0)
    }
}