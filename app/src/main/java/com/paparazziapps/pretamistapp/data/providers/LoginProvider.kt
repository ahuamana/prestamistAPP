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

    private val mAuth: FirebaseAuth by lazy { firebaseService.auth }

    suspend fun loginEmail(email: String, pass: String): PAResult<AuthResult> {
        return NetworkOperation.safeApiCall {
            mAuth.signInWithEmailAndPassword(email, pass).await()
        }
    }

    suspend fun loginAnonymously(): PAResult<AuthResult> {
        return  NetworkOperation.safeApiCall {
            mAuth.signInAnonymously().await()
        }
    }

    fun signOut() {
        return mAuth.signOut()
    }

}