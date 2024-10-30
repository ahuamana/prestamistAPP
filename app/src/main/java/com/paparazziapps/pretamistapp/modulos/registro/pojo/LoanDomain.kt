package com.paparazziapps.pretamistapp.modulos.registro.pojo

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class LoanDomain (
    @SerialName("id")
    var id:String?=null,
    @SerialName("nombres")
    var nombres:String? = null,
    @SerialName("apellidos")
    var apellidos: String? = null,
    @SerialName("dni")
    var dni:String? = null,
    @SerialName("celular")
    var celular:String? = null,
    @SerialName("fecha")
    var fecha_start_loan:String? = null,
    @SerialName("unixtime")
    var unixtime:Long?= null,
    @SerialName("unixtimeRegistered")
    var unixtimeRegistered: Long? = null,
    @SerialName("capital")
    var capital:Int? = null,
    @SerialName("interes")
    var interes:Int? = null,
    @SerialName("plazo_vto")
    var plazo_vto_in_days:Int?=null,
    //Calcular dias retrasados y
    @SerialName("dias_restantes_por_pagar")
    var dias_restantes_por_pagar:Int?=null,
    @SerialName("fechaUltimoPago")
    var fechaUltimoPago: String? = null,
    @SerialName("diasPagados")
    var diasPagados: Int? = null,
    @SerialName("montoTotalAPagar")
    var montoTotalAPagar:Double?=null,
    @SerialName("montoDiarioAPagar")
    var montoDiarioAPagar:Double?=null,
    @SerialName("state")
    var state:String?= null, //CERRADO,ABIERTO

    //Sucursal
    @SerialName("sucursalId")
    var sucursalId:Int?=null,
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

    )

enum class TypePrestamo(val value: Int) {
    TITLE(0),
    CARD(1)
}