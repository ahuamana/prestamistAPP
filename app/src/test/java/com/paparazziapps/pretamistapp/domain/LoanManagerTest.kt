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

}