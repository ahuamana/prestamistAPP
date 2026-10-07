package com.paparazziapps.pretamistapp.data.remote

import com.paparazziapps.pretamistapp.data.network.GlobalResponse
import com.paparazziapps.pretamistapp.data.network.PAResult

interface RemoteLoginDataSource {
    suspend fun loginEmail(email: String, pass: String): PAResult<GlobalResponse>
    suspend fun loginWithEmailV2(email: String, pass: String): PAResult<GlobalResponse>
    suspend fun loginAnonymously(): PAResult<GlobalResponse>
    suspend fun createUser(email: String, pass: String): PAResult<GlobalResponse>
    fun signOut()
}