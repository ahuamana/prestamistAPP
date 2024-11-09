package com.paparazziapps.pretamistapp.domain

data class InformationReceiptDomain(
    val idReceipt:String,
    val codeOperation:Long,
    val fullName:String,
    val phoneNumber:String,
    val email:String,
    val totalAmountToPay:Double,

)
