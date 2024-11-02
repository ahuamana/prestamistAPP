package com.paparazziapps.pretamistapp.data.providers

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.paparazziapps.pretamistapp.application.MyPreferences
import com.paparazziapps.pretamistapp.data.di.FirebaseService
import com.paparazziapps.pretamistapp.data.network.NetworkOperation
import com.paparazziapps.pretamistapp.data.network.PAResult
import kotlinx.coroutines.tasks.await

class LoginProvider(
    private val firebaseService: FirebaseService
) {

    suspend fun loginEmail(email: String, pass: String): PAResult<AuthResult> {
        return NetworkOperation.safeApiCall {
            firebaseService.auth.signInWithEmailAndPassword(email, pass).await()
        }
    }

    suspend fun loginAnonymously(): PAResult<AuthResult> {
        return  NetworkOperation.safeApiCall {
            firebaseService.auth.signInAnonymously().await()
        }
    }

    fun signOut() {
        return firebaseService.auth.signOut()
    }

}