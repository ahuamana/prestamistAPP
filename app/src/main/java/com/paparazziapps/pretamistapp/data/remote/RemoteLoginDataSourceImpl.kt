package com.paparazziapps.pretamistapp.data.remote

import com.paparazziapps.pretamistapp.data.network.GlobalResponse
import com.paparazziapps.pretamistapp.data.network.PAResult
import com.paparazziapps.pretamistapp.data.providers.LoginProvider
import com.paparazziapps.pretamistapp.data.providers.RegisterProvider
import com.paparazziapps.pretamistapp.domain.UserForm

class RemoteLoginDataSourceImpl (
    private val registerProvider: RegisterProvider,
    private val loginProvider: LoginProvider,
) : RemoteLoginDataSource {
    override suspend fun loginEmail(email: String, pass: String): PAResult<GlobalResponse> {
        return loginProvider.loginEmail(email, pass)
    }

    override suspend fun loginWithEmailV2(email: String, pass: String): PAResult<GlobalResponse> {
        return loginProvider.loginWithEmailV2(email, pass)
    }

    override suspend fun loginAnonymously(): PAResult<GlobalResponse> {
        return loginProvider.loginAnonymously()
    }

    override suspend fun createUser(email: String, pass: String): PAResult<GlobalResponse> {
        return registerProvider.createUser(email, pass)
    }

    override fun signOut() {
       loginProvider.signOut()
    }

}