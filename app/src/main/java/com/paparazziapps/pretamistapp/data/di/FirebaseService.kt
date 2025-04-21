package com.paparazziapps.pretamistapp.data.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings

class FirebaseService {

    val firestore: FirebaseFirestore by lazy {
        val firestore = FirebaseFirestore.getInstance()

        // Configure Fire store settings here
        val settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true) // Enable offline persistence
            .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
            .build()

        firestore.firestoreSettings = settings
        firestore
    }

    val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
}