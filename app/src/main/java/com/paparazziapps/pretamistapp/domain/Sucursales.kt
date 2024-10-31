package com.paparazziapps.pretamistapp.domain

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Sucursales (
    @SerialName("id")
    var id: Int = 0,
    @SerialName("name")
    var name: String = "",
    @SerialName("address")
    var address: String = ""
){
    constructor() : this(0, "", "")
    constructor(dict:Map<String, Any>) : this(
        dict["id"] as Int,
        dict["name"] as String,
        dict["address"] as String
    )

    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "name" to name,
            "address" to address
        )
    }
}

@Serializable
class Sucursal {
    @SerialName("id")
    var id: Int? = null
    @SerialName("name")
    var name: String? = null

    private constructor() {}
    constructor(id: Int?, name: String?) {
        this.id = id
        this.name = name
    }
}

