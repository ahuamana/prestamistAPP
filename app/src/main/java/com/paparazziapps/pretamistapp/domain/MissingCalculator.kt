package com.paparazziapps.pretamistapp.domain

class MissingCalculator {

    fun calculateDaysMissing(daysMissing: Int): Int {
        return daysMissing
    }

    fun calculateWeeksMissing(daysMissing: Int): Int {
        return if (daysMissing >= 7) {
            daysMissing / 7
        } else {
            0
        }
    }

    fun calculateFortnightsMissing(daysMissing: Int): Int {
        return if (daysMissing >= 15) {
            daysMissing / 15
        } else {
            0
        }
    }

    fun calculateMonthsMissing(daysMissing: Int): Int {
        return if (daysMissing >= 30) {
            daysMissing / 30
        } else {
            0
        }
    }

    fun calculateBimonthsMissing(daysMissing: Int): Int {
        return if (daysMissing >= 60) {
            daysMissing / 60
        } else {
            0
        }
    }

    fun calculateQuartersMissing(daysMissing: Int): Int {
        return if (daysMissing >= 90) {
            daysMissing / 90
        } else {
            0
        }
    }

    fun calculateSemestersMissing(daysMissing: Int): Int {
        return if (daysMissing >= 180) {
            daysMissing / 180
        } else {
            0
        }
    }

    fun calculateYearsMissing(daysMissing: Int): Int {
        return if (daysMissing >= 365) {
            daysMissing / 365
        } else {
            0
        }
    }
}