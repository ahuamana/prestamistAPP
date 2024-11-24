package com.paparazziapps.pretamistapp.data.remote.clients

import com.google.firebase.firestore.DocumentSnapshot
import com.paparazziapps.pretamistapp.data.network.PAResult
import com.paparazziapps.pretamistapp.data.providers.ClientsProvider
import com.paparazziapps.pretamistapp.domain.clients.ClientDomain

class RemoteClientsDataSourceImpl(
    private val clientsProvider: ClientsProvider
) : RemoteClientsDataSource {

    override suspend fun createClient(clientDomain: ClientDomain): PAResult<Void> {
        return clientsProvider.createClient(clientDomain)
    }

    override suspend fun searchClientByEmailV2(email: String): PAResult<DocumentSnapshot> {
        return clientsProvider.searchClientByEmailV2(email)
    }

    override suspend fun getClientsOnlyFirst20(): PAResult<List<ClientDomain>> {
        return clientsProvider.getClientsOnlyFirst20()
    }
}