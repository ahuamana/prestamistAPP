package com.paparazziapps.pretamistapp.data.remote

import com.google.firebase.auth.AuthResult
import com.paparazziapps.pretamistapp.data.network.PAResult

interface RemoteLoginDataSource {
    suspend fun loginEmail(email: String, pass: String): PAResult<AuthResult>
    suspend fun loginAnonymously(): PAResult<AuthResult>
    suspend fun createUser(email: String, pass: String): PAResult<AuthResult>
    fun signOut()
}