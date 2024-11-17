package com.paparazziapps.pretamistapp.domain

import kotlinx.serialization.Serializable

@Serializable
data class DetailLoanForm (
    var idDetailLoan:String,
    var idLoan:String,
    var paymentDate:String,
    var totalAmountToPay:Double,
    var branchId: Int, //This is the branch id is used to filter the data in indexes in firebase
    var codeOperation:Long
)

@Serializable
data class DetailLoanResponse(
    val idDetailLoan:String? = null,
    val idLoan:String? = null,
    val paymentDate:String? = null,
    val totalAmountToPay:Double? = null,
    val branchId: Int? = null,
    val codeOperation:Long? = null
)

fun DetailLoanForm.toDetailLoanDomain():DetailLoanDomain {
    return DetailLoanDomain(
        idLoan = this.idLoan,
        totalAmountToPay = this.totalAmountToPay
    )
}

fun DetailLoanDomain.toDetailLoanForm():DetailLoanForm {
    return DetailLoanForm(
        idDetailLoan = "",
        idLoan = this.idLoan,
        paymentDate = "",
        totalAmountToPay = this.totalAmountToPay,
        branchId = 0,
        codeOperation = 0
    )
}

fun DetailLoanResponse.toDetailLoanDomain():DetailLoanDomain {
    return DetailLoanDomain(
        idLoan = this.idLoan?:"",
        totalAmountToPay = this.totalAmountToPay?:0.0
    )
}