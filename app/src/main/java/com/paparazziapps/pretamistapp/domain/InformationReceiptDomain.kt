package com.paparazziapps.pretamistapp.domain

import kotlinx.serialization.Serializable

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
    val loanStartDateUnix:Long
) : java.io.Serializable
