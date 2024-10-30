package com.paparazziapps.pretamistapp.domain

class DelayCalculator {

    fun calculateWeeksDelayed(daysDelayed: Int): Int {
        return if (daysDelayed >= 7) {
            daysDelayed / 7
        } else {
            0
        }
    }

    fun calculateFortnightsDelayed(daysDelayed: Int): Int {
        return if (daysDelayed >= 15) {
            daysDelayed / 15
        } else {
            0
        }
    }

    fun calculateMonthsDelayed(daysDelayed: Int): Int {
        return if (daysDelayed >= 30) {
            daysDelayed / 30
        } else {
            0
        }
    }

    fun calculateBimonthsDelayed(daysDelayed: Int): Int {
        return if (daysDelayed >= 60) {
            daysDelayed / 60
        } else {
            0
        }
    }

    fun calculateQuartersDelayed(daysDelayed: Int): Int {
        return if (daysDelayed >= 90) {
            daysDelayed / 90
        } else {
            0
        }
    }

    fun calculateSemestersDelayed(daysDelayed: Int): Int {
        return if (daysDelayed >= 180) {
            daysDelayed / 180
        } else {
            0
        }
    }

    fun calculateYearsDelayed(daysDelayed: Int): Int {
        return if (daysDelayed >= 365) {
            daysDelayed / 365
        } else {
            0
        }
    }
}