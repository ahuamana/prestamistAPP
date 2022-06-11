package com.paparazziapps.pretamistapp.modulos.login.providers

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.paparazziapps.pretamistapp.modulos.login.pojo.User

class UserProvider {

    companion object{
        private lateinit var mCollection: CollectionReference
    }

    init {
        mCollection = FirebaseFirestore.getInstance().collection("Users")

        val settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()

        FirebaseFirestore.getInstance().firestoreSettings = settings
    }

    fun create(user: User): Task<Void> {
        return mCollection.document(user.email?:"").set(user)
    }

    fun searchUserByEmail(email: String?): Task<DocumentSnapshot> {
        return mCollection.document(email!!).get()
    }
}