package com.paparazziapps.pretamistapp.data.providers

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.paparazziapps.pretamistapp.data.PADataConstants
import com.paparazziapps.pretamistapp.data.di.FirebaseService
import com.paparazziapps.pretamistapp.data.network.NetworkOperation
import com.paparazziapps.pretamistapp.data.network.PAResult
import com.paparazziapps.pretamistapp.domain.User
import kotlinx.coroutines.tasks.await

class UserProvider(
    private val firebaseService: FirebaseService
) {

    private val mCollection: CollectionReference by lazy { firebaseService.firestore.collection(PADataConstants.USERS_COLLECTION) }

    suspend fun createUser(user: User): PAResult<Void> {
        return NetworkOperation.safeApiCall {
            mCollection.document(user.email?:"").set(user).await()
        }
    }

    suspend fun searchUserByEmail(email: String): PAResult<DocumentSnapshot> {
        return NetworkOperation.safeApiCall {
            mCollection.document(email).get().await()
        }
    }
}