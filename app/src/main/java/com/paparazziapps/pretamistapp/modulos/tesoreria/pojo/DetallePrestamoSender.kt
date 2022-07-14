package com.paparazziapps.pretamistapp.modulos.tesoreria.pojo

data class DetallePrestamoSender (
    var id:String?= null,
    var idPrestamo:String?= null,
    var fechaPago:String?=null,
    var pagoTotal:Double?= null,
    var sucursalId: Int? = null,
    var unixtime:Long?=null
)