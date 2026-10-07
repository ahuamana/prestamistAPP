package com.paparazziapps.pretamistapp.data.remote

import com.paparazziapps.pretamistapp.data.network.GlobalResponse
import com.paparazziapps.pretamistapp.data.network.PAResult
import com.paparazziapps.pretamistapp.data.providers.UserProviderImpl
import com.paparazziapps.pretamistapp.domain.UserForm

class RemoteUserDataSourceImpl(
    private val userProviderImpl: UserProviderImpl
) : RemoteUserDataSource {
    override suspend fun createUser(userForm: UserForm): PAResult<GlobalResponse> {
        return userProviderImpl.createUser(userForm)
    }

    override suspend fun searchUserByEmail(email: String): PAResult<GlobalResponse> {
        return userProviderImpl.searchUserByEmail(email)
    }
}