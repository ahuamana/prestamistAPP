package com.paparazziapps.pretamistapp.domain

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class InformationReceiptDomain(
    val idReceipt:String,
    val codeOperation:Long,
    val fullName:String,
    val names:String,
    val lastNames:String,
    val phoneNumber:String,
    val totalAmountToPay:Double,
    val quotes:Int,
    val quotesPaidNew:Int,
    val amountPerQuote:Double,
    val typeLoan:Int,
    val loanStartDateUnix:Long,
    val email:String,
) : java.io.Serializable
