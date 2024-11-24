package com.paparazziapps.pretamistapp.data.providers

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.paparazziapps.pretamistapp.data.PADataConstants
import com.paparazziapps.pretamistapp.data.di.FirebaseService
import com.paparazziapps.pretamistapp.data.network.NetworkOperation
import com.paparazziapps.pretamistapp.data.network.PAResult
import com.paparazziapps.pretamistapp.domain.clients.ClientDomain
import com.paparazziapps.pretamistapp.domain.clients.ClientsRequest
import com.paparazziapps.pretamistapp.domain.clients.toClientDomain
import com.paparazziapps.pretamistapp.domain.clients.toClientsRequest
import kotlinx.coroutines.tasks.await

class ClientsProvider(
    private val firebaseProvider: FirebaseService
) {

    private val mCollection: CollectionReference by lazy { firebaseProvider.firestore.collection(
        PADataConstants.CLIENTS_COLLECTION) }


    suspend fun createClient(clientDomain: ClientDomain): PAResult<Void> {
        val form = clientDomain.toClientsRequest()

        return NetworkOperation.safeApiCall {
            mCollection.document(form.id).set(form).await()
        }
    }

    suspend fun searchClientByEmailV2(email: String): PAResult<DocumentSnapshot> {
        return NetworkOperation.safeApiCall {
            mCollection
                .document(email)
                .get()
                .await()
        }
    }

    suspend fun getClientsOnlyFirst20(): PAResult<List<ClientDomain>> {
        return NetworkOperation.safeApiCall {
            mCollection
                .limit(20)
                .get()
                .await().toObjects(ClientsRequest::class.java).map { it.toClientDomain() }
        }
    }

}