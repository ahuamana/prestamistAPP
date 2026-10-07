package com.paparazziapps.pretamistapp.data.remote.clients

import com.paparazziapps.pretamistapp.data.network.GlobalResponse
import com.paparazziapps.pretamistapp.data.network.PAResult
import com.paparazziapps.pretamistapp.data.providers.ClientsProviderImpl
import com.paparazziapps.pretamistapp.domain.clients.ClientDomain

class RemoteClientsDataSourceImpl(
    private val clientsProviderImpl: ClientsProviderImpl
) : RemoteClientsDataSource {

    override suspend fun createClient(clientDomain: ClientDomain): PAResult<GlobalResponse> {
        return clientsProviderImpl.createClient(clientDomain)
    }

    override suspend fun searchClientByEmailV2(email: String): PAResult<GlobalResponse> {
        return clientsProviderImpl.searchClientByEmailV2(email)
    }

    override suspend fun searchByClientName(name: String): PAResult<GlobalResponse> {
        return clientsProviderImpl.searchByClientName(name)
    }

    override suspend fun getClientsOnlyFirst20(): PAResult<GlobalResponse> {
        return clientsProviderImpl.getClientsOnlyFirst20()
    }
}