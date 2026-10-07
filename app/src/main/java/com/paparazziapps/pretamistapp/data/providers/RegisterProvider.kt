package com.paparazziapps.pretamistapp.data.providers

import com.paparazziapps.pretamistapp.data.network.GlobalResponse
import com.paparazziapps.pretamistapp.libraries.database.firebase.FDatabaseImpl
import com.paparazziapps.pretamistapp.data.network.NetworkOperation
import com.paparazziapps.pretamistapp.data.network.PAResult

class RegisterProvider private constructor(
    private val fDatabaseImpl: FDatabaseImpl
) {

    suspend fun createUser(email:String, pass:String): PAResult<GlobalResponse> {
        return NetworkOperation.safeApiCall {
            fDatabaseImpl.createUserWithEmailAndPassword(email, pass)
        }
    }

    companion object {
        fun instance(): RegisterProvider {
            return RegisterProvider(fDatabaseImpl = FDatabaseImpl.instance())
        }
    }
}