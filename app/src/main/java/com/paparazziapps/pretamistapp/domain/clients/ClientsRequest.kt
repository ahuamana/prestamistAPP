package com.paparazziapps.pretamistapp.domain.clients

data class ClientsRequest(
    val id: String,
    val dni: String,
    val name: String,
    val lastName: String,
    val email: String,
    val phone: String,
    val dateCreated: Long,
)

data class ClientDomain(
    val id: String,
    val dni: String,
    val name: String,
    val lastName: String,
    val email: String,
    val phone: String,
)

fun ClientsRequest.toClientDomain(): ClientDomain {
    return ClientDomain(
        id = id,
        dni = dni,
        name = name,
        lastName = lastName,
        email = email,
        phone = phone,
    )
}

fun ClientDomain.toClientsRequest(): ClientsRequest {
    return ClientsRequest(
        id = id,
        dni = dni,
        name = name,
        lastName = lastName,
        email = email,
        phone = phone,
        dateCreated = System.currentTimeMillis(),
    )
}
