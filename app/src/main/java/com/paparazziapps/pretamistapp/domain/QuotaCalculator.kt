package com.paparazziapps.pretamistapp.domain

import com.paparazziapps.pretamistapp.modulos.registro.pojo.PaymentScheduledEnum
import kotlin.math.ceil

class QuotaCalculator {
    fun calculateDividedQuotas(tyLoan: PaymentScheduledEnum, daysMissing: Int): Int {
        return when (tyLoan) {
            PaymentScheduledEnum.DAILY -> daysMissing
            PaymentScheduledEnum.WEEKLY -> ceil(daysMissing / 7.0).toInt()
            PaymentScheduledEnum.FORTNIGHTLY -> ceil(daysMissing / 15.0).toInt()
            PaymentScheduledEnum.MONTHLY -> ceil(daysMissing / 30.0).toInt()
            PaymentScheduledEnum.BIMONTHLY -> ceil(daysMissing / 60.0).toInt()
            PaymentScheduledEnum.QUARTERLY -> ceil(daysMissing / 90.0).toInt()
            PaymentScheduledEnum.SEMIANNUAL -> ceil(daysMissing / 180.0).toInt()
            PaymentScheduledEnum.ANNUAL -> ceil(daysMissing / 365.0).toInt()
        }
    }
}