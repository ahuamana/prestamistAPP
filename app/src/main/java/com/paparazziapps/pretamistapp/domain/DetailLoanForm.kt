package com.paparazziapps.pretamistapp.domain

data class DetailLoanForm (
    var idDetailLoan:String,
    var idLoan:String,
    var paymentDate:String,
    var totalAmountToPay:Double,
    var branchId: Int, //This is the branch id is used to filter the data in indexes in firebase
    var codeOperation:Long
)