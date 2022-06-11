package com.paparazziapps.pretamistapp.modulos.login.pojo

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class Sucursales (
    @SerialName("Sucursales")
    val sucursales: List<SucusarBody>? = null
)

@Serializable
data class SucusarBody (
    @SerialName("name")
    val name: String? = null
)