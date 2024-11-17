package com.paparazziapps.pretamistapp.data.remote

import com.google.firebase.auth.AuthResult
import com.paparazziapps.pretamistapp.data.network.PAResult
import com.paparazziapps.pretamistapp.domain.UserForm

interface RemoteLoginDataSource {
    suspend fun loginEmail(email: String, pass: String): PAResult<AuthResult>
    suspend fun loginWithEmailV2(email: String, pass: String): PAResult<UserForm>
    suspend fun loginAnonymously(): PAResult<AuthResult>
    suspend fun createUser(email: String, pass: String): PAResult<AuthResult>
    fun signOut()
}