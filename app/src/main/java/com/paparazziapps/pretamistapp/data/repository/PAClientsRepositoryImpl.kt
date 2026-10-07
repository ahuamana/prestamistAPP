package com.paparazziapps.pretamistapp.data.repository

import com.paparazziapps.pretamistapp.data.network.GlobalResponse
import com.paparazziapps.pretamistapp.data.network.PAResult
import com.paparazziapps.pretamistapp.data.remote.clients.RemoteClientsDataSource
import com.paparazziapps.pretamistapp.domain.clients.ClientDomain

class PAClientsRepositoryImpl(
    private val remoteClientsDataSource: RemoteClientsDataSource,
) : PAClientsRepository {
    override suspend fun createClient(clientDomain: ClientDomain): PAResult<GlobalResponse> {
        return remoteClientsDataSource.createClient(clientDomain)
    }

    override suspend fun searchClientByEmailV2(email: String): PAResult<GlobalResponse> {
        return remoteClientsDataSource.searchClientByEmailV2(email)
    }

    override suspend fun searchByClientName(name: String): PAResult<GlobalResponse> {
        return remoteClientsDataSource.searchByClientName(name)
    }

    override suspend fun getClientsOnlyFirst20(): PAResult<GlobalResponse> {
        return remoteClientsDataSource.getClientsOnlyFirst20()
    }
}
