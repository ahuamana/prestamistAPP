package com.paparazziapps.pretamistapp.data.providers

import com.paparazziapps.pretamistapp.data.network.GlobalResponse
import com.paparazziapps.pretamistapp.libraries.database.firebase.FDatabaseImpl
import com.paparazziapps.pretamistapp.data.network.NetworkOperation
import com.paparazziapps.pretamistapp.data.network.PAResult
import com.paparazziapps.pretamistapp.domain.clients.ClientDomain
import com.paparazziapps.pretamistapp.domain.clients.toClientsRequest

interface ClientsProvider {
    suspend fun createClient(clientDomain: ClientDomain): PAResult<GlobalResponse>
    suspend fun searchClientByEmailV2(email: String): PAResult<GlobalResponse>
    suspend fun getClientsOnlyFirst20(): PAResult<GlobalResponse>
    suspend fun searchByClientName(document: String): PAResult<GlobalResponse>
}
class ClientsProviderImpl private constructor(
    private val firebaseProvider: FDatabaseImpl
): ClientsProvider {

    override suspend fun createClient(clientDomain: ClientDomain): PAResult<GlobalResponse> {
        val form = clientDomain.toClientsRequest()

        return NetworkOperation.safeApiCall {
            test
        }
    }

    override suspend fun searchClientByEmailV2(email: String): PAResult<GlobalResponse> {
        return NetworkOperation.safeApiCall {
            test
        }
    }

    override suspend fun getClientsOnlyFirst20(): PAResult<GlobalResponse> {
        return NetworkOperation.safeApiCall {
            test
        }
    }

    override suspend fun searchByClientName(document: String): PAResult<GlobalResponse> {
        return NetworkOperation.safeApiCall {
            test
        }
    }

    companion object {
        const val CLIENTS_COLLECTION = "Clientes"
        val test = GlobalResponse(
            true
        )
        fun instance(): ClientsProviderImpl{
            return ClientsProviderImpl(FDatabaseImpl())
        }
    }

}