package com.paparazziapps.pretamistapp.data.remote.clients

import com.google.firebase.firestore.DocumentSnapshot
import com.paparazziapps.pretamistapp.data.network.PAResult
import com.paparazziapps.pretamistapp.domain.clients.ClientDomain

interface RemoteClientsDataSource {
    suspend fun createClient(clientDomain: ClientDomain): PAResult<Void>
    suspend fun searchClientByEmailV2(email: String): PAResult<DocumentSnapshot>
    suspend fun getClientsOnlyFirst20(): PAResult<List<ClientDomain>>
}