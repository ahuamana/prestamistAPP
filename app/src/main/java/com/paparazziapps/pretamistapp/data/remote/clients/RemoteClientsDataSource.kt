package com.paparazziapps.pretamistapp.data.remote.clients

import com.paparazziapps.pretamistapp.data.network.GlobalResponse
import com.paparazziapps.pretamistapp.data.network.PAResult
import com.paparazziapps.pretamistapp.domain.clients.ClientDomain

interface RemoteClientsDataSource {
    suspend fun createClient(clientDomain: ClientDomain): PAResult<GlobalResponse>
    suspend fun searchClientByEmailV2(email: String): PAResult<GlobalResponse>
    suspend fun searchByClientName(name: String): PAResult<GlobalResponse>
    suspend fun getClientsOnlyFirst20(): PAResult<GlobalResponse>
}