package com.paparazziapps.pretamistapp.modulos.login.pojo

data class User (
    var nombres: String? = null,
    var apellidos: String? = null,
    var sucursalId: Int? = null,
    var sucursal: String? = null,
    var email: String? = null,
    var password: String? = null,
    var activeUser: Boolean = false,
    var admin: Boolean = false,
    var superAdmin: Boolean = false
)