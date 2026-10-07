package com.paparazziapps.pretamistapp.data.providers

import com.paparazziapps.pretamistapp.data.network.GlobalResponse
import com.paparazziapps.pretamistapp.libraries.database.firebase.FDatabaseImpl
import com.paparazziapps.pretamistapp.data.network.NetworkOperation
import com.paparazziapps.pretamistapp.data.network.PAResult

class LoginProvider private constructor(
    private val fDatabase: FDatabaseImpl,
) {

    suspend fun loginEmail(email: String, pass: String): PAResult<GlobalResponse> {
        return NetworkOperation.safeApiCall {
            fDatabase.signInWithEmailAndPassword(email, pass)
        }
    }

    suspend fun loginWithEmailV2(email: String, pass: String): PAResult<GlobalResponse> {
        return NetworkOperation.safeApiCall {

            //Login with email and password
            fDatabase.signInWithEmailAndPassword(email, pass)
        }
    }

    suspend fun loginAnonymously(): PAResult<GlobalResponse> {
        return  NetworkOperation.safeApiCall {
            fDatabase.signInAnonymously()
        }
    }

    fun signOut() {
        return fDatabase.signOut()
    }

    companion object {
        fun instance(): LoginProvider {
            return LoginProvider(FDatabaseImpl.instance())
        }
    }

}