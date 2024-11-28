package com.paparazziapps.pretamistapp.domain.clients

import kotlinx.serialization.Serializable

@Serializable
data class ClientsRequest(
    val id: String? = null,
    val document: String?= null,
    val name: String?= null,
    val lastName: String?= null,
    val email: String?= null,
    val phone: String?= null,
    val dateCreated: Long?= null,
) : java.io.Serializable

data class ClientDomain(
    val id: String,
    val document: String,
    val name: String,
    val lastName: String,
    val email: String,
    val phone: String,
)

data class ClientDomainSelect(
    val id: String,
    val document: String,
    val name: String,
    val lastName: String,
    val email: String,
    val phone: String,
    var isSelected: Boolean = false,
)

fun ClientDomain.toClientDomainSelect(): ClientDomainSelect {
    return ClientDomainSelect(
        id = id,
        document = document,
        name = name,
        lastName = lastName,
        email = email,
        phone = phone,
    )
}

fun List<ClientDomain>.toClientDomainSelect(): List<ClientDomainSelect> {
    return map { it.toClientDomainSelect() }
}

fun ClientsRequest.toClientDomain(): ClientDomain {
    return ClientDomain(
        id = id ?: "",
        document = document ?: "",
        name = name ?: "",
        lastName = lastName ?: "",
        email = email ?: "",
        phone = phone ?: "",
    )
}

fun ClientDomain.toClientsRequest(): ClientsRequest {
    return ClientsRequest(
        id = id,
        document = document,
        name = name,
        lastName = lastName,
        email = email,
        phone = phone,
        dateCreated = System.currentTimeMillis(),
    )
}
