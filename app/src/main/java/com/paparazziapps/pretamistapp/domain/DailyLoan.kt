package com.paparazziapps.pretamistapp.domain



class DailyLoan : BaseLoan() {
    override val periodsPerYear = 365 // 365 days in a year
}

class WeeklyLoan : BaseLoan() {
    override val periodsPerYear = 52 // 52 weeks in a year
}

class FortnightlyLoan : BaseLoan() {
    override val periodsPerYear = 26 // 26 fortnights in a year
}

class MonthlyLoan : BaseLoan() {
    override val periodsPerYear = 12 // 12 months in a year
}

class BimonthlyLoan : BaseLoan() {
    override val periodsPerYear = 6 // 6 bimonthly periods in a year
}

class QuarterlyLoan : BaseLoan() {
    override val periodsPerYear = 4 // 4 quarters in a year
}

class SemiannualLoan : BaseLoan() {
    override val periodsPerYear = 2 // 2 semiannual periods in a year
}

class AnnualLoan : BaseLoan() {
    override val periodsPerYear = 1 // 1 annual period in a year
}

// Enum class to represent loan types
enum class LoanType(val id: Int, val description: String, val days: Int) {
    DAILY(1, "Diario (cada día)", 1),
    WEEKLY(2, "Semanal (cada 7 días)", 7),
    FORTNIGHTLY(3, "Quincenal (cada 15 días)", 15),
    MONTHLY(4, "Mensual (cada 30 días)", 30),
    BIMONTHLY(5, "Bimestral (cada 60 días)", 60),
    QUARTERLY(6, "Trimestral (cada 90 días)", 90),
    SEMIANNUAL(7, "Semestral (cada 180 días)", 180),
    ANNUAL(8, "Anual (cada 365 días)", 365)
}