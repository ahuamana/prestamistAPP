package com.paparazziapps.pretamistapp.modulos.login.pojo

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class Sucursales (
    @SerialName("id")
    val id: Int? = null,

    @SerialName("name")
    val name: String? = null
)

