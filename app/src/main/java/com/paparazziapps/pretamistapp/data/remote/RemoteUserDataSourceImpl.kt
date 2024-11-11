package com.paparazziapps.pretamistapp.data.remote

import com.google.firebase.firestore.DocumentSnapshot
import com.paparazziapps.pretamistapp.data.network.PAResult
import com.paparazziapps.pretamistapp.data.providers.UserProvider
import com.paparazziapps.pretamistapp.domain.UserForm

class RemoteUserDataSourceImpl(
    private val userProvider: UserProvider
) : RemoteUserDataSource {
    override suspend fun createUser(userForm: UserForm): PAResult<Void> {
        return userProvider.createUser(userForm)
    }

    override suspend fun searchUserByEmail(email: String): PAResult<DocumentSnapshot> {
        return userProvider.searchUserByEmail(email)
    }
}