package com.paparazziapps.pretamistapp.data.remote

import com.google.firebase.auth.AuthResult
import com.paparazziapps.pretamistapp.data.network.PAResult
import com.paparazziapps.pretamistapp.data.providers.BranchesProvider
import com.paparazziapps.pretamistapp.data.providers.DetailLoanProvider
import com.paparazziapps.pretamistapp.data.providers.LoanProvider
import com.paparazziapps.pretamistapp.data.providers.LoginProvider
import com.paparazziapps.pretamistapp.data.providers.RegisterProvider
import com.paparazziapps.pretamistapp.domain.UserForm

class RemoteLoginDataSourceImpl (
    private val registerProvider: RegisterProvider,
    private val loginProvider: LoginProvider,
) : RemoteLoginDataSource {
    override suspend fun loginEmail(email: String, pass: String): PAResult<AuthResult> {
        return loginProvider.loginEmail(email, pass)
    }

    override suspend fun loginWithEmailV2(email: String, pass: String): PAResult<UserForm> {
        return loginProvider.loginWithEmailV2(email, pass)
    }

    override suspend fun loginAnonymously(): PAResult<AuthResult> {
        return loginProvider.loginAnonymously()
    }

    override suspend fun createUser(email: String, pass: String): PAResult<AuthResult> {
        return registerProvider.createUser(email, pass)
    }

    override fun signOut() {
       loginProvider.signOut()
    }

}