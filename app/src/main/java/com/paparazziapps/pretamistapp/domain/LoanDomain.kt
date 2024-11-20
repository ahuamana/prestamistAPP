package com.paparazziapps.pretamistapp.domain

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class LoanDomain (
    @SerialName("id")
    var id:String?=null,
    @SerialName("names")
    var names:String? = null,
    @SerialName("lastnames")
    var lastnames: String? = null,
    @SerialName("dni")
    var dni:String? = null,
    @SerialName("cellular")
    var cellular:String? = null,
    @SerialName("loanStartDateFormatted")
    var loanStartDateFormatted:String? = null, // Stores date in DD/MM/YYYY format
    @SerialName("loanStartDateUnix")
    var loanStartDateUnix:Long?= null,  // Same start date but in Unix timestamp
    @SerialName("loanCreationDateUnix")
    var loanCreationDateUnix:Long?=null, // // When the loan was created/registered in system
    @SerialName("capital")
    var capital:Int? = null,
    @SerialName("interest")
    var interest:Int? = null,
    //Calcular dias retrasados y
    @SerialName("lastPaymentDate")
    var lastPaymentDate: String? = null,
    @SerialName("totalAmountToPay")
    var totalAmountToPay:Double?=null,
    @SerialName("amountPerQuota")
    var amountPerQuota:Double?=null,
    @SerialName("state")
    var state:String?= null, //CERRADO,ABIERTO

    //Sucursal
    @SerialName("branchId")
    var branchId:Int?=null,
    @SerialName("type")
    var type:Int?=null,
    @SerialName("title")
    var title:String?=null,


    //New fields for the new version v2.0
    @SerialName("typeLoan")
    var typeLoan:Int?=null,
    @SerialName("typeLoanName")
    var typeLoanName:String?=null,
    @SerialName("typeLoanDays")
    var typeLoanDays:Int?=null,
    @SerialName("quotas")
    var quotas:Int?=null,
    @SerialName("quotasPaid")
    var quotasPaid:Int?=null,
    @SerialName("quotasPending")
    var quotasPending:Int?=null,
    @SerialName("email")
    val email:String?=null,
    )

enum class TypePrestamo(val value: Int) {
    TITLE(0),
    CARD(1)
}