package com.paparazziapps.pretamistapp.data.providers

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.paparazziapps.pretamistapp.data.di.FirebaseService
import com.paparazziapps.pretamistapp.data.network.NetworkOperation
import com.paparazziapps.pretamistapp.data.network.PAResult
import kotlinx.coroutines.tasks.await

class RegisterProvider(
    private val firebaseService: FirebaseService
) {

    suspend fun createUser(email:String, pass:String): PAResult<AuthResult> {
        return NetworkOperation.safeApiCall {
            firebaseService.auth.createUserWithEmailAndPassword(email, pass).await()
        }
    }

}