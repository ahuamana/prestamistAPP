package com.paparazziapps.pretamistapp.data.providers

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.paparazziapps.pretamistapp.data.PADataConstants
import com.paparazziapps.pretamistapp.domain.User

class UserProvider {

    private val mCollection: CollectionReference by lazy { FirebaseFirestore.getInstance().collection(PADataConstants.USERS_COLLECTION) }

    fun create(user: User): Task<Void> {
        return mCollection.document(user.email?:"").set(user)
    }

    fun searchUserByEmail(email: String?): Task<DocumentSnapshot> {
        return mCollection.document(email!!).get()
    }
}