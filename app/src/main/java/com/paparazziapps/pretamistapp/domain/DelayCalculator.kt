package com.paparazziapps.pretamistapp.domain

class DelayCalculator {

    fun calculateDelay(tyLoan: PaymentScheduledEnum, daysDelayed: Int): Int {
        return when (tyLoan) {
            PaymentScheduledEnum.DAILY -> daysDelayed
            PaymentScheduledEnum.WEEKLY -> calculateWeeksDelayedInDays(daysDelayed)
            PaymentScheduledEnum.FORTNIGHTLY -> calculateFortnightsDelayedInDays(daysDelayed)
            PaymentScheduledEnum.MONTHLY -> calculateMonthsDelayedInDays(daysDelayed)
            PaymentScheduledEnum.BIMONTHLY -> calculateBimonthsDelayedInDays(daysDelayed)
            PaymentScheduledEnum.QUARTERLY -> calculateQuartersDelayedInDays(daysDelayed)
            PaymentScheduledEnum.SEMIANNUAL -> calculateSemestersDelayedInDays(daysDelayed)
            PaymentScheduledEnum.ANNUAL -> calculateYearsDelayedInDays(daysDelayed)
        }
    }

    private fun calculateWeeksDelayedInDays(daysDelayed: Int): Int {
        return if (daysDelayed > 7) {
            (daysDelayed - 7)
        } else {
            0
        }
    }

    private fun calculateFortnightsDelayedInDays(daysDelayed: Int): Int {
        return if (daysDelayed > 15) {
            (daysDelayed - 15)
        } else {
            0
        }
    }

    private fun calculateMonthsDelayedInDays(daysDelayed: Int): Int {
        return if (daysDelayed > 30) {
            (daysDelayed - 30)
        } else {
            0
        }
    }

    private fun calculateBimonthsDelayedInDays(daysDelayed: Int): Int {
        return if (daysDelayed > 60) {
            (daysDelayed - 60)
        } else {
            0
        }
    }

    private fun calculateQuartersDelayedInDays(daysDelayed: Int): Int {
        return if (daysDelayed > 90) {
            (daysDelayed - 90)
        } else {
            0
        }
    }

    private fun calculateSemestersDelayedInDays(daysDelayed: Int): Int {
        return if (daysDelayed > 180) {
            (daysDelayed - 180)
        } else {
            0
        }
    }

    private fun calculateYearsDelayedInDays(daysDelayed: Int): Int {
        return if (daysDelayed > 365) {
            (daysDelayed - 365)
        } else {
            0
        }
    }
}