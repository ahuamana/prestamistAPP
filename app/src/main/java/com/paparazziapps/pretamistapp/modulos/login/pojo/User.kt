package com.paparazziapps.pretamistapp.modulos.login.pojo

data class User (
    var nombres: String? = null,
    var apellidos: String? = null,
    var sucursal: String? = null,
    var email: String? = null,
    var password: String? = null,
    var isActiveUser: Boolean = false,
    var isAdmin: Boolean = false,
    var isSuperAdmin: Boolean = false
)