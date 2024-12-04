package com.paparazziapps.pretamistapp.data.repository

import com.google.firebase.firestore.DocumentSnapshot
import com.paparazziapps.pretamistapp.data.network.PAResult
import com.paparazziapps.pretamistapp.data.remote.clients.RemoteClientsDataSource
import com.paparazziapps.pretamistapp.domain.clients.ClientDomain

class PAClientsRepositoryImpl(
    private val remoteClientsDataSource: RemoteClientsDataSource,
) : PAClientsRepository {
    override suspend fun createClient(clientDomain: ClientDomain): PAResult<Void> {
        return remoteClientsDataSource.createClient(clientDomain)
    }

    override suspend fun searchClientByEmailV2(email: String): PAResult<DocumentSnapshot> {
        return remoteClientsDataSource.searchClientByEmailV2(email)
    }

    override suspend fun searchByClientName(name: String): PAResult<List<ClientDomain>> {
        return remoteClientsDataSource.searchByClientName(name)
    }

    override suspend fun getClientsOnlyFirst20(): PAResult<List<ClientDomain>> {
        return remoteClientsDataSource.getClientsOnlyFirst20()
    }
}
