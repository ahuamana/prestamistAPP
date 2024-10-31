package com.paparazziapps.pretamistapp.data.providers

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.paparazziapps.pretamistapp.data.PADataConstants
import com.paparazziapps.pretamistapp.data.network.NetworkOperation
import com.paparazziapps.pretamistapp.data.network.PAResult
import com.paparazziapps.pretamistapp.domain.User
import kotlinx.coroutines.tasks.await

class UserProvider {

    private val mCollection: CollectionReference by lazy { FirebaseFirestore.getInstance().collection(PADataConstants.USERS_COLLECTION) }

    suspend fun create(user: User): PAResult<Void> {
        return NetworkOperation.safeApiCall {
            mCollection.document(user.email?:"").set(user).await()
        }
    }

    fun searchUserByEmail(email: String?): Task<DocumentSnapshot> {
        return mCollection.document(email!!).get()
    }
}