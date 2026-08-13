package com.paparazziapps.pretamistapp.domain.clients

import androidx.annotation.Keep
import kotlinx.serialization.Serializable


@Keep
@Serializable
data class ClientsRequest(
    val id: String? = null,
    val document: String?= null,
    val name: String?= null,
    val lastName: String?= null,
    val email: String?= null,
    val phone: String?= null,
    val dateCreated: Long?= null,
    val typeDocument: String?= null,
    val note: String?= null,
    val address: String?= null
) : java.io.Serializable

@Keep
data class ClientDomain(
    val id: String,
    val typeDocument: String,
    val document: String,
    val name: String,
    val lastName: String,
    val email: String,
    val phone: String,
    val note: String,
    val address: String
)

@Keep
@Serializable
data class ClientDomainSelect(
    val id: String,
    val document: String,
    val name: String,
    val lastName: String,
    val email: String,
    val phone: String,
    var isSelected: Boolean = false,
):java.io.Serializable


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

@Keep
fun List<ClientDomain>.toClientDomainSelect(): List<ClientDomainSelect> {
    return map { it.toClientDomainSelect() }
}

@Keep
fun ClientsRequest.toClientDomain(): ClientDomain {
    return ClientDomain(
        id = id ?: "",
        document = document ?: "",
        name = name ?: "",
        lastName = lastName ?: "",
        email = email ?: "",
        phone = phone ?: "",
        typeDocument = typeDocument ?: "",
        note = note ?: "",
        address = address ?: ""
    )
}

@Keep
fun ClientDomain.toClientsRequest(): ClientsRequest {
    return ClientsRequest(
        id = id,
        document = document,
        name = name,
        lastName = lastName,
        email = email,
        phone = phone,
        dateCreated = System.currentTimeMillis(),
        typeDocument = typeDocument
    )
}
