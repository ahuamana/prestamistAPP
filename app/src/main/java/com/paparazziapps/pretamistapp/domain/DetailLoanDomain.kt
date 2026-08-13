package com.paparazziapps.pretamistapp.domain

import androidx.annotation.Keep

@Keep
data class DetailLoanDomain(
    val idLoan:String,
    val totalAmountToPay:Double,
)